package com.phdlabs.sungwon.a8chat_android.structure.createnew

import android.content.Intent
import com.phdlabs.sungwon.a8chat_android.model.contacts.Contact
import com.phdlabs.sungwon.a8chat_android.structure.channel.mychannel.MyChannelActivity
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import com.vicpin.krealmextensions.query
import com.vicpin.krealmextensions.queryAll

/**
 * Created by JPAM on 3/12/18.
 */
class CreateNewAController(val mView: CreateNewContract.CreateNew.View) : CreateNewContract.CreateNew.Controller {

    init {
        mView.controller = this
    }

    /*LifeCycle*/
    override fun start() {
    }

    override fun resume() {
    }

    override fun pause() {
    }

    override fun stop() {
    }

    override fun queryContacts(callback: (Pair<MutableList<Contact>?, MutableList<Contact>?>) -> Unit) {
        callback(Pair(
                Contact().queryAll().toMutableList(),
                Contact().query { equalTo("isFavorite", true) }.toMutableList())
        )
    }

    override fun openChannel(channelId: Int?, channelName: String?, channelRoomId: Int?, channelCreatorId: Int?) {
        val intent = Intent(mView.getContext(), MyChannelActivity::class.java)
        intent.putExtra(Constants.IntentKeys.CHANNEL_ID, channelId)
        intent.putExtra(Constants.IntentKeys.CHANNEL_NAME, channelName)
        intent.putExtra(Constants.IntentKeys.ROOM_ID, channelRoomId)
        intent.putExtra(Constants.IntentKeys.OWNER_ID, channelCreatorId)
        mView.getAct.startActivityForResult(intent, Constants.RequestCodes.OPEN_CHANNEL)
    }

}