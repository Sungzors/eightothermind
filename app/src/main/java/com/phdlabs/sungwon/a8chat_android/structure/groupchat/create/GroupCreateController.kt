package com.phdlabs.sungwon.a8chat_android.structure.groupchat.create

import com.phdlabs.sungwon.a8chat_android.structure.groupchat.GroupChatContract

/**
 * Created by SungWon on 3/1/2018.
 */
class GroupCreateController(val mView: GroupChatContract.Create.View) : GroupChatContract.Create.Controller {

    init {
        mView.controller = this
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