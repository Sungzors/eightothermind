package com.phdlabs.sungwon.a8chat_android.structure.chat

import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity

/**
 * Created by SungWon on 10/18/2017.
 */
class ChatActivity: CoreActivity(), ChatContract.View{

    override lateinit var controller: ChatContract.Controller

    override fun layoutId() = R.layout.activity_chat

    override fun contentContainerId() = 0

    override fun onStart() {
        super.onStart()
        ChatController(this)
        controller.start()
    }

    override fun onResume() {
        super.onResume()
        controller.resume()
    }

    override fun onPause() {
        super.onPause()
        controller.pause()
    }

    override fun onStop() {
        super.onStop()
        controller.stop()
    }
}