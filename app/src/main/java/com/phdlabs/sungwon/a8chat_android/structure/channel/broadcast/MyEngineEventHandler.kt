package com.phdlabs.sungwon.a8chat_android.structure.channel.broadcast

import android.content.Context
import io.agora.rtc.IRtcEngineEventHandler
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap

/**
 * Created by JPAM on 4/6/18.
 * RTC Engine Event Handler - Translated to Kotlin
 *
 * The SDK uses the IRtcEngineEventHandler interface class to send callback event notifications to
 * the application, and the application inherits the methods of this interface class to retrieve
 * these event notifications.
 *All methods in this interface class have their (empty) default implementations,
 * and the application can inherit only some of the required events instead of all of them.
 * In the callback methods, the application should avoid time-consuming tasks or calling
 * blocking APIs (such as SendMessage), otherwise the SDK may not work properly.
 *
 * https://docs.agora.io/en/2.1.2/product/Interactive%20Broadcast/API%20Reference/live_video_android#live-android-create-en
 */
class MyEngineEventHandler(val mContext: Context, mConfig: EngineConfig) {

    /*Properties*/
    private var mEventHandlerList: ConcurrentHashMap<AGEventHandler, Int> = ConcurrentHashMap()

    /**
     * Event Handler access methods
     * Add & Remove
     * */
    fun addEventHandler(handler: AGEventHandler) {
        this.mEventHandlerList[handler] = 0
    }

    fun removeEventHandler(handler: AGEventHandler) {
        this.mEventHandlerList.remove(handler)
    }

    /**
     * RTC Engine Event Handlers for Agora.io Live Video Broadcast
     * */
    val mRtcEventHandler: IRtcEngineEventHandler = object : IRtcEngineEventHandler() {

        //Dev logger
        private val log = LoggerFactory.getLogger(this.javaClass)

        /*First Remote Video Decoded*/
        override fun onFirstRemoteVideoDecoded(uid: Int, width: Int, height: Int, elapsed: Int) {
            //Dev
            log.debug("onFirstRemoteVideoDecoded: uid:" + (uid and 0xFFFFFFFFL.toInt()) + " width: $width height: $height elapsed: $elapsed")
            //Handler Iterator
            val it = mEventHandlerList.keys.iterator()
            while (it.hasNext()) {
                val handler = it.next()
                handler.onFirstRemoteVideoDecoded(uid, width, height, elapsed)
            }
        }

        /*First Local Video Frame*/
        override fun onFirstLocalVideoFrame(width: Int, height: Int, elapsed: Int) {
            log.debug("onFirstLocalVideoFrame: width: $width  height: $height elapsed: $elapsed")
        }

        /*User Joined*/
        override fun onUserJoined(uid: Int, elapsed: Int) {
            log.debug("onUserJoined " + (uid and 0xFFFFFFFFL.toInt()) + "elapsed: $elapsed")

            val it = mEventHandlerList.keys.iterator()
            while (it.hasNext()) {
                val handler = it.next()
                handler.onUserJoined(uid, elapsed)
            }
        }

        /*User Offline*/
        override fun onUserOffline(uid: Int, reason: Int) {
            // FIXME this callback may return times
            //Handler Iterator
            val it = mEventHandlerList.keys.iterator()
            while (it.hasNext()) {
                val handler = it.next()
                handler.onUserOffline(uid, reason)
            }
        }

        /*Video Quality Changes*/
        override fun onLastmileQuality(quality: Int) {
            super.onLastmileQuality(quality)
            //Dev
            log.debug("onLastmileQuality: $quality")
        }

        /*Error with Agora.io Connectivity*/
        override fun onError(err: Int) {
            super.onError(err)
            //Dev
            log.debug("onBroadcastError: $err")
        }

        /*Success Joining Agora.io Live Video Broadcast Channel*/
        override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
            //Dev
            log.debug("onJoinChannelSuccess: $channel + $uid + " + (uid and 0xFFFFFFFFL.toInt()) + " $elapsed")
            //Handler iterator
            val it = mEventHandlerList.keys.iterator()
            while (it.hasNext()) {
                val handler = it.next()
                channel?.let { channel ->
                    handler.onJoinChannelSuccess(channel, uid, elapsed)
                }
            }
        }

        /*Success for Rejoining Agora.io Channel*/
        override fun onRejoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
            //Dev
            log.debug("onRejoinChannelSuccess: $channel $uid $elapsed")
        }

        /*Warning for Agora.io Live Video Broadcast*/
        override fun onWarning(warn: Int) {
            log.debug("onWarning: $warn")
        }
    }
}