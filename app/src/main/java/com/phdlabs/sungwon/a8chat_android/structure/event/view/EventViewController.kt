package com.phdlabs.sungwon.a8chat_android.structure.event.view

import android.content.Intent
import com.github.nkzawa.socketio.client.Socket
import com.phdlabs.sungwon.a8chat_android.api.rest.Caller
import com.phdlabs.sungwon.a8chat_android.api.rest.Rest
import com.phdlabs.sungwon.a8chat_android.db.EventBusManager
import com.phdlabs.sungwon.a8chat_android.model.Message
import com.phdlabs.sungwon.a8chat_android.structure.event.EventContract
import org.greenrobot.eventbus.EventBus

/**
 * Created by SungWon on 1/8/2018.
 */
class EventViewController(val mView: EventContract.View.View): EventContract.View.Controller{

    private val TAG = "EventViewController"

    private val mSocket: Socket
    private val mCaller: Caller
    private val mEventBus: EventBus

    private var mRoomId: Int = 0

    init {
        mView.controller = this
        mSocket = mView.get8Application.getSocket()
        mCaller = Rest.getInstance().caller
        mEventBus = EventBusManager.instance().mDataEventBus
    }

    override fun start() {
    }

    override fun resume() {
    }

    override fun pause() {
    }

    override fun stop() {
    }

    override fun sendMessage() {
    }

    override fun sendChannel() {
    }

    override fun sendContact() {
    }

    override fun sendFile() {
    }

    override fun sendLocation() {
    }

    override fun sendMedia() {
    }

    override fun retrieveChatHistory() {
    }

    override fun getMessages(): MutableList<Message> {
    }

    override fun onPictureResult(requestCode: Int, resultCode: Int, data: Intent?) {
    }
}