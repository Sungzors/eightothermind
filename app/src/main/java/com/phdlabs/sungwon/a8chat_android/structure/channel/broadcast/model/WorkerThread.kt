package com.phdlabs.sungwon.a8chat_android.structure.channel.broadcast.model

import android.content.Context
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.preference.PreferenceManager
import android.text.TextUtils
import android.util.Log
import android.view.SurfaceView
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import io.agora.rtc.RtcEngine
import io.agora.rtc.video.VideoCanvas
import org.slf4j.LoggerFactory
import java.io.File

/**
 * Created by JPAM on 4/7/18.
 * Worker Thread used for handling Live Video Broadcast
 */
class WorkerThread(val mContext: Context) : Thread() {

    /*Properties*/
    private var mWorkerHandler: WorkerThreadHandler? = null
    private var mReady: Boolean? = null
    private var mRtcEngine: RtcEngine? = null
    private var mEngineConfig: EngineConfig? = null
    private var mEngineEventHandler: MyEngineEventHandler? = null

    init {
        this.mEngineConfig = EngineConfig()
        val pref = PreferenceManager.getDefaultSharedPreferences(mContext)
        this.mEngineConfig?.mUid = pref.getInt(Constants.Broadcast.PrefManager.PREF_PROPERTY_UID, 0)
        this.mEngineEventHandler = MyEngineEventHandler(mContext, this.mEngineConfig!!)
    }

    /*Companion*/
    companion object {
        /*Dev*/
        private val log = LoggerFactory.getLogger(WorkerThread::class.java)
        /*Properties*/
        private val ACTION_WORKER_THREAD_QUIT = 0X1010 // quit this thread
        private val ACTION_WORKER_JOIN_CHANNEL = 0X2010
        private val ACTION_WORKER_LEAVE_CHANNEL = 0X2011
        private val ACTION_WORKER_CONFIG_ENGINE = 0X2012
        private val ACTION_WORKER_PREVIEW = 0X2014

        /**
         * Worker Thread [Handler]
         * */
        private class WorkerThreadHandler(var mWorkerThread: WorkerThread?) : Handler() {
            fun release() {
                this.mWorkerThread = null
            }

            override fun handleMessage(msg: Message?) {
                if (this.mWorkerThread == null) {
                    log.warn("handler is already released! " + msg?.what)
                    return
                }

                when (msg?.what) {
                //ACTION_WORKER_THREAD_QUIT -> mWorkerThread.exit()
                    ACTION_WORKER_JOIN_CHANNEL -> {
                        val data = msg.obj as Array<String>
                        mWorkerThread?.joinChannel(data[0], msg.arg1)
                    }
                    ACTION_WORKER_LEAVE_CHANNEL -> {
                        val channel = msg.obj as String
                        mWorkerThread?.leaveChannel(channel)
                    }
                    ACTION_WORKER_CONFIG_ENGINE -> {
                        val configData = msg.obj as Array<Any>
                        mWorkerThread?.configEngine(configData[0] as Int, configData[1] as Int)
                    }
                    ACTION_WORKER_PREVIEW -> {
                        val previewData = msg.obj as Array<Any>
                        mWorkerThread?.preview(previewData[0] as Boolean, previewData[1] as SurfaceView, previewData[2] as Int)
                    }
                }
            }
        }
    }

    /*Methods*/
    fun waitForReady() {
        mReady?.let {
            while (!it) {
                try {
                    Thread.sleep(20)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                log.debug("Wait for: ${WorkerThread::class.java.simpleName}")
            }
        }
    }

    override fun run() {
        log.trace("WorkerThread start to run")
        Looper.prepare()
        mWorkerHandler = WorkerThreadHandler(this)
        ensureRtcEngineReadyLock()
        mReady = true
        //enter thread looper
        Looper.loop()
    }

    fun joinChannel(channel: String, uid: Int) {
        if (Thread.currentThread() != this) {
            log.warn("joinChannel() - worker thread async channel: $channel , uid: $uid")
            val envelope = Message()
            envelope.what = ACTION_WORKER_JOIN_CHANNEL
            envelope.obj = arrayOf(channel)
            envelope.arg1 = uid
            mWorkerHandler?.sendMessage(envelope)
            return
        }
        ensureRtcEngineReadyLock()
        mRtcEngine?.joinChannel(null, channel, "Eight", uid)
        mEngineConfig?.mChannel = channel
        log.debug("joinChannel channel: $channel, uid: $uid")
    }

    fun leaveChannel(channel: String) {
        if (Thread.currentThread() != this) {
            log.warn("leaveChannel() - worker thread async channel: $channel")
            val envelope = Message()
            envelope.what = ACTION_WORKER_LEAVE_CHANNEL
            envelope.obj = channel
            mWorkerHandler?.sendMessage(envelope)
            return
        }
        if (mRtcEngine != null) {
            mRtcEngine?.leaveChannel()
        }
        val clientRole: Int? = mEngineConfig?.mClientRole
        mEngineConfig?.reset()
        log.debug("leaveChannel channel: $channel , clientRole: $clientRole")
    }

    fun getEngineConfig(): EngineConfig? = mEngineConfig

    fun configEngine(cRole: Int, vProfile: Int) {
        if (Thread.currentThread() != this) {
            log.warn("configEngine() - worker thread async clientRole: $cRole , videoProfile: $vProfile")
            val envelope = Message()
            envelope.what = ACTION_WORKER_CONFIG_ENGINE
            envelope.obj = arrayOf(cRole, vProfile)
            mWorkerHandler?.sendMessage(envelope)
            return
        }
        ensureRtcEngineReadyLock()
        mEngineConfig?.mClientRole = cRole
        mEngineConfig?.mVideoProfile = vProfile
        mRtcEngine?.setVideoProfile(mEngineConfig?.mVideoProfile!!, true)
        mRtcEngine?.setClientRole(cRole)
        log.debug("configEngine clientRole: $cRole , videoProfile: $vProfile")
    }

    fun preview(start: Boolean, view: SurfaceView, uid: Int) {
        if (Thread.currentThread() != this) {
            log.warn("preview() - worker thread asynchronously " + start + " " + view + " " + (uid and 0XFFFFFFFFL.toInt()))
            val envelope = Message()
            envelope.what = ACTION_WORKER_PREVIEW
            envelope.obj = arrayOf(start, view, uid)
            mWorkerHandler?.sendMessage(envelope)
            return
        }
        ensureRtcEngineReadyLock()
        if (start) {
            mRtcEngine?.setupLocalVideo(VideoCanvas(view, VideoCanvas.RENDER_MODE_HIDDEN, uid))
            mRtcEngine?.startPreview()
        } else {
            mRtcEngine?.stopPreview()
        }
    }

    private fun ensureRtcEngineReadyLock(): RtcEngine? {
        if (mRtcEngine == null) {
            var appId = mContext.getString(R.string.private_app_id)
            if (TextUtils.isEmpty(appId)) {
                throw RuntimeException("Need to use Eight Agora.io App ID")
            }
            try {
                mRtcEngine = RtcEngine.create(mContext, appId, mEngineEventHandler?.mRtcEventHandler)
            } catch (e: Exception) {
                log.error(Log.getStackTraceString(e))
                throw RuntimeException("Need to check RTC SDK fatal error: ${Log.getStackTraceString(e)}")
            }
            mRtcEngine?.setChannelProfile(io.agora.rtc.Constants.CHANNEL_PROFILE_LIVE_BROADCASTING)
            mRtcEngine?.enableVideo()
            mRtcEngine?.setLogFile(Environment.getExternalStorageDirectory().toString()
                    + File.separator + mContext.packageName + "/log/agora-rtc.log")//TODO: Remove after testing
            mRtcEngine?.enableDualStreamMode(true)
        }
        return mRtcEngine
    }

    fun eventHandler(): MyEngineEventHandler? = mEngineEventHandler
    fun getRtcEngine(): RtcEngine? = mRtcEngine

    fun exit() {
        if (Thread.currentThread() != this) {
            log.warn("exit() - exit app thread async")
            mWorkerHandler?.sendEmptyMessage(ACTION_WORKER_THREAD_QUIT)
            return
        }
        mReady = false
        log.debug("exit() > start")
        Looper.myLooper().quit()
        mWorkerHandler?.release()
        log.debug("exit() > end")
    }

}