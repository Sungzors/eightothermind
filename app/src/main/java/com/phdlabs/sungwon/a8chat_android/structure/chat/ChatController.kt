package com.phdlabs.sungwon.a8chat_android.structure.chat

import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import com.phdlabs.sungwon.a8chat_android.db.UserManager
import java.net.URISyntaxException


/**
 * Created by SungWon on 10/18/2017.
 */
class ChatController(mView: ChatContract.View): ChatContract.Controller {

    private lateinit var mSocket: Socket

    init {
        mView.controller = this
        try {
            mSocket = IO.socket("https://eight-backend.herokuapp.com/")
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }

    }

    override fun start() {
        mSocket.connect()
        // TODO: requires room name
        var roomName = "testRoom1"
        mSocket.on("connect", {
            mSocket.emit("check-rooms-user-participates", UserManager.instance().user!!.id)
        })
    }

    override fun resume() {
    }

    override fun pause() {
    }

    override fun stop() {
    }

    override fun destroy() {
        mSocket.disconnect()
    }
}