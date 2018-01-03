package com.phdlabs.sungwon.a8chat_android.structure.event.create

import com.github.nkzawa.socketio.client.Socket
import com.phdlabs.sungwon.a8chat_android.structure.event.EventContract

/**
 * Created by SungWon on 1/2/2018.
 */
class EventCreateController(val mView: EventContract.Create.View) : EventContract.Create.Controller{

    private var mSocket: Socket

    init {
        mView.controller = this
        mSocket = mView.get8Application.getSocket()
    }
    override fun start() {
    }

    override fun resume() {
    }

    override fun pause() {
    }

    override fun stop() {
    }
}