package com.phdlabs.sungwon.a8chat_android.structure.channel.broadcast.model

/**
 * Created by JPAM on 4/6/18.
 * Event Handler for Agora.io Live Video Broadcasting on Eight Channels
 */
interface AGEventHandler {
    fun onFirstRemoteVideoDecoded(uid: Int, width: Int, height: Int, elapsed: Int)
    fun onJoinChannelSuccess(channel: String, uid: Int, elapsed: Int)
    fun onUserOffline(uid: Int, reason: Int)
    fun onUserJoined(uid: Int, elapsed: Int)
}