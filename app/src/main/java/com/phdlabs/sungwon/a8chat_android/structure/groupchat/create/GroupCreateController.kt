package com.phdlabs.sungwon.a8chat_android.structure.groupchat.create

import android.content.Intent
import com.phdlabs.sungwon.a8chat_android.api.data.GroupChatPostData
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

    override fun onPictureResult(requestCode: Int, resultCode: Int, data: Intent?) {
    }

    override fun showPicture() {

    }

    override fun createGroupChat(data: GroupChatPostData) {

    }
}