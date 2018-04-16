package com.phdlabs.sungwon.a8chat_android.structure.channel.broadcast

import android.app.Activity
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.SurfaceView
import android.view.View
import android.view.ViewStub
import android.widget.ImageView
import android.widget.RelativeLayout
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.model.message.Message
import com.phdlabs.sungwon.a8chat_android.structure.application.Application
import com.phdlabs.sungwon.a8chat_android.structure.channel.broadcast.model.*
import com.phdlabs.sungwon.a8chat_android.structure.channel.broadcast.ui.GridVideoViewContainer
import com.phdlabs.sungwon.a8chat_android.structure.channel.broadcast.ui.SmallVideoViewAdapter
import com.phdlabs.sungwon.a8chat_android.structure.channel.broadcast.ui.VideoViewEventListener
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import com.phdlabs.sungwon.a8chat_android.utility.camera.CircleTransform
import com.squareup.picasso.Picasso
import com.vicpin.krealmextensions.queryFirst
import io.agora.rtc.RtcEngine
import io.agora.rtc.video.VideoCanvas
import kotlinx.android.synthetic.main.activity_broadcast.*
import org.slf4j.LoggerFactory
import java.util.HashMap

/**
 * Created by JPAM on 4/9/18.
 * Agora.io Live Video Broadcast
 * This activity should be instantiated with Client Role (Audience || Broadcaster)
 * Only if the Eight-Channel owner starts this activity it can be based in a Broadcaster
 */

//TODO: Setup broadcast controller after testing
open class BroadcastActivity : CoreActivity(), AGEventHandler {

    //Dev
    private val log = LoggerFactory.getLogger(BroadcastActivity::class.java)

    /*Layout*/
    override fun layoutId(): Int = R.layout.activity_broadcast

    /*Container*/
    override fun contentContainerId(): Int = 0

    /*Message Properties*/
    private var mBroadcastMessage: Message? = null
    private var mBroadcastRoomName: Int? = null
    private var mRoomId: Int = 0
    private var mUserId: Int = 0
    /*Video Properties*/
    private var mGridVideoViewContainer: GridVideoViewContainer? = null
    private var mSmallVideoViewAdapter: SmallVideoViewAdapter? = null
    private var mSmallVideoViewDock: RelativeLayout? = null
    private val mUidsList = HashMap<Int, SurfaceView>() // uid = 0 || uid == EngineConfig.mUid
    /*Client Role properties*/
    private fun isBroadcaster(cRole: Int): Boolean =
            cRole == io.agora.rtc.Constants.CLIENT_ROLE_BROADCASTER

    private fun isBroadcaster(): Boolean = isBroadcaster(config().mClientRole)
    /*Video View Type*/
    private val VIEW_TYPE_DEFAULT = 0
    private val VIEW_TYPE_SMALL = 1
    private var mViewType = VIEW_TYPE_DEFAULT


    /*LifeCycle*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getBroadcastMessageIntent()
    }

    override fun onDestroy() {
        super.onDestroy()
        doLeaveChannel()
        event().removeEventHandler(this)
        mUidsList.clear()
    }

    /**
     * Get messageId [Intent] from [MyChannelActivity]
     * and Query [Message]
     * @see Realm
     * */
    private fun getBroadcastMessageIntent() {
        //Event Handler
        event().addEventHandler(this)
        mRoomId = intent.getIntExtra(Constants.IntentKeys.ROOM_ID, 0)
        intent.getIntExtra(Constants.IntentKeys.USER_ID, 0).let { userId ->
            mUserId = if (userId == 0) {
                config().mUid
            } else {
                config().setUid(userId)
                userId
            }
        }
        //Broadcast Room Name
        mBroadcastRoomName = intent.getIntExtra(Constants.IntentKeys.BROADCAST_MESSAGE_ID, 0)
        //Broadcast Message info used for Live comments
        if (mBroadcastRoomName != 0) {
            Message().queryFirst { equalTo("id", mBroadcastRoomName) }?.let {
                mBroadcastMessage = it
            }
        } else {
            //No Broadcast Room available
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
        //Broadcast Client Role
        val cRole = intent.getIntExtra(Constants.Broadcast.ACTION_KEY_CROLE, 0)
        if (cRole == 0) {
            //No Client Broadcast role
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
        doConfigEngine(cRole)
        /*Video Container*/
        mGridVideoViewContainer = findById(R.id.grid_video_view_container)
        mGridVideoViewContainer?.setItemEventHandler { v, item ->
            //Dev
            log.debug("onItemDoubleClick: $v , $item")
            if (mUidsList.size < 2) {
                return@setItemEventHandler
            }
            //TODO: Only allow a single Default View
            if (mViewType == VIEW_TYPE_DEFAULT) {
                val currentItem = item as VideoStatusData
                switchToSmallVideoView(currentItem.mUid)
            } else {
                switchToDefaultVideoView()
            }
        }
        //UI -> Broadcaster of Audience
        if (isBroadcaster(cRole)) {
            var surfaceV = RtcEngine.CreateRendererView(applicationContext)
            rtcEngine().setupLocalVideo(VideoCanvas(surfaceV, VideoCanvas.RENDER_MODE_HIDDEN, 0))
            surfaceV.setZOrderOnTop(true)
            surfaceV.setZOrderMediaOverlay(true)
            mUidsList[0] = surfaceV//Get first surface View
            mGridVideoViewContainer?.initViewContainer(applicationContext, 0, mUidsList)//First is now full view
            worker().preview(true, surfaceV, 0)
            broadcasterUI(ab_flip_camera, ab_mic_control)
        } else {
            audienceUI(ab_flip_camera, ab_mic_control)
        }
        //Join Agora.io Room with roomId as Room Name
        mRoomId?.let {
            worker().joinChannel(it.toString(), config().mUid)
            //worker().joinChannel(it.toString(), mUserId)//Todo: set user ID
        }
        //Close Broadcast
        click_close.setOnClickListener {
            val intent = Intent()
            intent.putExtra(Constants.IntentKeys.BROADCAST_MESSAGE_ID, mBroadcastRoomName!!)
            intent.putExtra(Constants.IntentKeys.ROOM_ID, mRoomId)
            setResult(Activity.RESULT_OK, intent)
            //TODO: Send intent back
            finish()
        }

    }

    private fun broadcasterUI(cameraControl: ImageView, audioControl: ImageView) {
        //Camera Flip control
        Picasso.with(this).load(R.drawable.btn_switch_camera).transform(CircleTransform()).into(cameraControl)
        var cameraFlipped = false
        cameraControl.setOnClickListener {
            //Action
            worker().getRtcEngine()?.switchCamera()
            //Selection State
            cameraFlipped = if (!cameraFlipped) {
                cameraControl.setColorFilter(resources.getColor(R.color.blue_color_picker), PorterDuff.Mode.MULTIPLY)
                true
            } else {
                cameraControl.setColorFilter(resources.getColor(R.color.transparent))
                false
            }
        }

        //Mute Audio control
        Picasso.with(this).load(R.drawable.btn_mute).transform(CircleTransform()).into(audioControl)
        var audioMuted = false
        audioControl.setOnClickListener {
            //Action
            val tag = it.tag
            var flag = true
            if (tag != null && tag as Boolean) {
                flag = false
            }
            worker().getRtcEngine()?.muteLocalAudioStream(flag)
            //Selection State
            audioMuted = if (!audioMuted) {
                audioControl.setColorFilter(resources.getColor(R.color.blue_color_picker), PorterDuff.Mode.MULTIPLY)
                true
            } else {
                audioControl.setColorFilter(resources.getColor(R.color.transparent))
                false
            }
        }
    }

    //TODO: Test audience UI with Justin
    private fun audienceUI(likeControl: ImageView, comment: ImageView) {
        //Like Control
        Picasso.with(this).load(R.drawable.ic_like).transform(CircleTransform()).into(likeControl)
        likeControl.setOnClickListener {
            //TODO: LIKE POST
        }
        //Comment
        Picasso.with(this).load(R.drawable.ic_comment).transform(CircleTransform()).into(comment)
        comment.setOnClickListener {
            //TODO: Open comment dialog to publish on screen
        }
    }

    /*Engine Config*/
    private fun doConfigEngine(cRole: Int) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        var prefIndex = sharedPreferences
                .getInt(Constants.Broadcast.PrefManager.PREF_PROPERTY_PROFILE_IDX, Constants.Broadcast.DEFAULT_PROFILE_IDX)
        if (prefIndex > Constants.Broadcast.VIDEO_PROFILES.count() - 1) {
            prefIndex = Constants.Broadcast.DEFAULT_PROFILE_IDX
        }
        val vProfile = Constants.Broadcast.VIDEO_PROFILES[prefIndex] //TODO: Test with video resolutions
        //val vProfile = io.agora.rtc.Constants.VIDEO_PROFILE_DEFAULT
        worker().configEngine(cRole, vProfile)
    }

    private fun doLeaveChannel() {
        config().mChannel?.let {
            worker().leaveChannel(it)
            if (isBroadcaster()) {
                worker().preview(false, null, 0)
            }
        }
    }

    private fun doRenderRemoteUI(uid: Int) {
        runOnUiThread(Runnable {
            if (isFinishing) {
                return@Runnable
            }

            val surfaceV = RtcEngine.CreateRendererView(applicationContext)
            surfaceV.setZOrderOnTop(true)
            surfaceV.setZOrderMediaOverlay(true)
            mUidsList[uid] = surfaceV
            if (config().mUid == uid) {
                rtcEngine().setupLocalVideo(VideoCanvas(surfaceV, VideoCanvas.RENDER_MODE_HIDDEN, uid))
            } else {
                rtcEngine().setupRemoteVideo(VideoCanvas(surfaceV, VideoCanvas.RENDER_MODE_HIDDEN, uid))
            }

            if (mViewType == VIEW_TYPE_DEFAULT) {
                log.debug("doRenderRemoteUi VIEW_TYPE_DEFAULT" + " " + (uid and 0xFFFFFFFFL.toInt()))
                switchToDefaultVideoView()
            } else {
                val bigBgUid = mSmallVideoViewAdapter?.getExceptedUid()
                bigBgUid?.let {
                    log.debug("doRenderRemoteUi VIEW_TYPE_SMALL" + " " +
                            (uid and 0xFFFFFFFFL.toInt()) + " " + (it and 0xFFFFFFFFL.toInt()))
                    switchToSmallVideoView(it)
                }
            }
        })
    }

    private fun doRemoveRemoteUI(uid: Int) {
        runOnUiThread(Runnable {
            if (isFinishing) {
                return@Runnable
            }
            mUidsList.remove(uid)
            var bigBgUid = -1
            mSmallVideoViewAdapter?.getExceptedUid()?.let {
                bigBgUid = it
            }
            log.debug("doRemoveRemoteUi " + (uid and 0xFFFFFFFFL.toInt()) + " " + (bigBgUid and 0xFFFFFFFFL.toInt()))
            if (mViewType == VIEW_TYPE_DEFAULT || uid == bigBgUid) {
                switchToDefaultVideoView()
            } else {
                switchToSmallVideoView(bigBgUid)
            }
        })
    }

    private fun requestRemoteStreamType(currentHostCount: Int) {
        //Dev
        log.debug("requestRemoteStreamType $currentHostCount")
        Handler().postDelayed({
            var highest: MutableMap.MutableEntry<Int, SurfaceView>? = null
            for (pair in mUidsList.entries) {
                //Dev
                log.debug("requestRemoteStreamType " + currentHostCount +
                        " local " + (config().mUid and 0xFFFFFFFFL.toInt()) +
                        " " + (pair.key and 0xFFFFFFFFL.toInt()) + " " + pair.value.height + " " + pair.value.width)

                if (pair.key != config().mUid && (highest == null || highest.value.height < pair.value.height)) {
                    if (highest != null) {
                        rtcEngine().setRemoteVideoStreamType(highest.key, io.agora.rtc.Constants.VIDEO_STREAM_LOW)
                        //Dev
                        log.debug("setRemoteVideoStreamType switch highest VIDEO_STREAM_LOW " +
                                currentHostCount + " " + (highest.key and 0xFFFFFFFFL.toInt()) +
                                " " + highest.value.width + " " + highest.value.height)
                    }
                    highest = pair
                } else if (pair.key != config().mUid && highest != null && highest.value.height >= pair.value.height) {
                    rtcEngine().setRemoteVideoStreamType(pair.key, io.agora.rtc.Constants.VIDEO_STREAM_LOW)
                    //Dev
                    log.debug("setRemoteVideoStreamType VIDEO_STREAM_LOW " + currentHostCount + " " +
                            (pair.key and 0xFFFFFFFFL.toInt()) + " " + pair.value.width + " " + pair.value.height)
                }
            }
            if (highest != null && highest.key != 0) {
                rtcEngine().setRemoteVideoStreamType(highest.key, io.agora.rtc.Constants.VIDEO_STREAM_HIGH)
                //Dev
                log.debug("setRemoteVideoStreamType VIDEO_STREAM_HIGH " + currentHostCount + " " +
                        (highest.key and 0xFFFFFFFFL.toInt()) + " " + highest.value.width + " " + highest.value.height)
            }
        }, 500)
    }


    /*RTC Control*/
    protected fun rtcEngine(): RtcEngine =
            (application as Application).getWorkerThread()?.getRtcEngine()!!

    protected fun worker(): WorkerThread = (application as Application).getWorkerThread()!!

    protected fun config(): EngineConfig =
            (application as Application).getWorkerThread()?.getEngineConfig()!!

    protected fun event(): MyEngineEventHandler =
            (application as Application).getWorkerThread()?.eventHandler()!!

    /*VideoType Config*/
    private fun switchToSmallVideoView(uid: Int) {
        val slice = HashMap<Int, SurfaceView>(1)
        mUidsList[uid]?.let {
            slice.put(uid, it)
        }
        mGridVideoViewContainer?.initViewContainer(applicationContext, uid, slice)

        bindToSmallVideoView(uid)

        mViewType = VIEW_TYPE_SMALL

        requestRemoteStreamType(mUidsList.size)
    }

    private fun switchToDefaultVideoView() {
        mSmallVideoViewDock?.visibility = View.GONE
        mGridVideoViewContainer?.initViewContainer(applicationContext, config().mUid, mUidsList)

        mViewType = VIEW_TYPE_DEFAULT

        var sizeLimit = mUidsList.size
        if (sizeLimit > Constants.Broadcast.MAX_PEER_COUNT + 1) {
            sizeLimit = Constants.Broadcast.MAX_PEER_COUNT + 1
        }
        for (i in 0 until sizeLimit) {
            mGridVideoViewContainer?.getItem(i)?.mUid?.let {
                if (config().mUid != it) {
                    rtcEngine().setRemoteVideoStreamType(it, io.agora.rtc.Constants.VIDEO_STREAM_HIGH)
                    //Dev
                    log.debug("setRemoteVideoStreamType VIDEO_STREAM_HIGH " + mUidsList.size + " " +
                            (it and 0xFFFFFFFFL.toInt()))
                }
            }
        }
    }

    private fun bindToSmallVideoView(exceptUid: Int) {
        if (mSmallVideoViewDock == null) {
            val stub = findById<ViewStub>(R.id.small_video_view_dock)
            mSmallVideoViewDock = stub.inflate() as RelativeLayout
        }
        val recycler = findById<RecyclerView>(R.id.small_video_view_container)

        var create = false

        if (mSmallVideoViewAdapter == null) {
            create = true
            mSmallVideoViewAdapter = SmallVideoViewAdapter(this, exceptUid, mUidsList, object : VideoViewEventListener {
                override fun onItemDoubleClick(v: View?, item: Any?) {
                    switchToDefaultVideoView()
                }
            })
            mSmallVideoViewAdapter?.setHasStableIds(true)
        }
        recycler.setHasFixedSize(true)

        recycler.layoutManager = GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false)
        recycler.adapter = mSmallVideoViewAdapter

        recycler.isDrawingCacheEnabled = true
        recycler.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_AUTO

        if (!create) {
            mSmallVideoViewAdapter?.notifyUiChanged(mUidsList, exceptUid, null, null)
        }
        recycler.visibility = View.VISIBLE
        mSmallVideoViewDock?.visibility = View.VISIBLE
    }

    /*AGEventHandler*/
    override fun onFirstRemoteVideoDecoded(uid: Int, width: Int, height: Int, elapsed: Int) {
        //Nothing
    }

    override fun onJoinChannelSuccess(channel: String, uid: Int, elapsed: Int) {
        runOnUiThread(Runnable {
            if (isFinishing) {
                return@Runnable
            }

            if (mUidsList.containsKey(uid)) {
                log.debug("already added to UI, ignore it " + (uid and 0xFFFFFFFFL.toInt()) + " " + mUidsList[uid])
                return@Runnable
            }

            val isBroadcaster = isBroadcaster()
            log.debug("onJoinChannelSuccess $channel $uid $elapsed $isBroadcaster")

            worker().getEngineConfig()?.mUid = uid

            val surfaceV = mUidsList.remove(0)
            surfaceV?.let {
                mUidsList[uid] = it
            }
        })
    }

    override fun onUserOffline(uid: Int, reason: Int) {
        log.debug("onUserOffline " + (uid and 0xFFFFFFFFL.toInt()) + " " + reason)
        doRemoveRemoteUI(uid)
    }

    override fun onUserJoined(uid: Int, elapsed: Int) {
        doRenderRemoteUI(uid)
    }

}