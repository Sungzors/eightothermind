package com.phdlabs.sungwon.a8chat_android.structure.channel.broadcast.model

import android.view.SurfaceView

/**
 * Created by JPAM on 4/7/18.
 * Agora.io Live Video Broadcast Audio & Status Data
 */
class VideoStatusData(val mUid: Int, val mView: SurfaceView, val mStatus: Int, val mVolume: Int) {

    companion object {
        val DEFAULT_STATUS = 0
        val VIDEO_MUTED = 1
        val AUDIO_MUTED = VIDEO_MUTED shl 1
        val DEFAULT_VOLUME = 0
    }

    override fun toString(): String {
        return "Video Status Data {" +
                "mUid = " + (mUid and 0xFFFFFFFFL.toInt()) + "," +
                "mView = $mView " +
                "mStatus = $mStatus" +
                "mVolume = $mVolume " +
                "}"

    }

}