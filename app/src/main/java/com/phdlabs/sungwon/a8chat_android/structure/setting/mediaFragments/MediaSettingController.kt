package com.phdlabs.sungwon.a8chat_android.structure.setting.mediaFragments

import com.phdlabs.sungwon.a8chat_android.db.channels.ChannelsManager
import com.phdlabs.sungwon.a8chat_android.model.media.Media
import com.phdlabs.sungwon.a8chat_android.structure.setting.SettingContract
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import com.vicpin.krealmextensions.query

/**
 * Created by SungWon on 2/1/2018.
 */
class MediaSettingController(val mView: SettingContract.MediaFragment.View) : SettingContract.MediaFragment.Controller {

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

    /*Media*/
    override fun queryMediaForChatRoom(contactId: Int): List<Media>? =
            Media().query { equalTo("sharedWithUserId", MediaSettingFragment.mContactId) ?: -1 }

    /*Files*/
    override fun queryMediaForChannelRoom(roomId: Int, callback: (List<Media>?) -> Unit) {
        ChannelsManager.instance.queryChannelMessagesByType(roomId, Constants.MessageTypes.TYPE_MEDIA)?.let {
            var collectedMedia: MutableList<Media> = mutableListOf()
            for (message in it) {
                message.mediaArray?.let {
                    if (it.count() > 0) {
                        collectedMedia.addAll(0, it)
                    }
                }
            }
            callback(collectedMedia)
        }
    }


}