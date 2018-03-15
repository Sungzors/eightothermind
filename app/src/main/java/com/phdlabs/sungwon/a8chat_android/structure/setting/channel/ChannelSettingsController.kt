package com.phdlabs.sungwon.a8chat_android.structure.setting.channel

import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.db.channels.ChannelsManager
import com.phdlabs.sungwon.a8chat_android.db.user.UserManager
import com.phdlabs.sungwon.a8chat_android.model.room.Room
import com.phdlabs.sungwon.a8chat_android.structure.setting.SettingContract
import com.phdlabs.sungwon.a8chat_android.structure.setting.fileFragments.FileSettingsFragment
import com.phdlabs.sungwon.a8chat_android.structure.setting.mediaFragments.MediaSettingFragment
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import com.vicpin.krealmextensions.queryFirst

/**
 * Created by JPAM on 3/12/18.
 * [ChannelSettingsController]
 * Used for business logic on [ChannelSettingsActivity]
 */
class ChannelSettingsController(val mView: SettingContract.Channel.View) : SettingContract.Channel.Controller {

    /*Init*/
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

    /*Get room information from Realm*/
    override fun getRoomInfo(id: Int): Room? = Room().queryFirst { equalTo("id", id) }

    /*Get Channel Owner Information*/
    override fun getChannelOwnerInfo(ownerId: Int) {
        UserManager.instance.getSpecificUserInfo(ownerId, { user, errorMessage ->
            errorMessage?.let {
                //Error
                mView.showError(it)
            } ?: run {
                //Success
                user?.let {
                    mView.updateChannelOwnerInfo(it)
                }
            }
        })
    }

    /*MEDIA*/
    override fun getMedia(roomId: Int) {
        var mediaCount = 0
        ChannelsManager.instance.getChannelMessagesByType(roomId, Constants.MessageTypes.TYPE_MEDIA)?.let {
            //Iterate over messages for media count
            for (message in it) {
                message.mediaArray?.let {
                    if (it.count() > 0) {
                        mediaCount += it.count()
                    }
                }
            }
            //If media is available set the fragment
            if (mediaCount > 0) {
                mView.activity?.replaceFragment(R.id.achs_fragment_container,
                        MediaSettingFragment.newInstanceChannelRoom(roomId), false)
            }
        }
    }

    /*FILES*/
    override fun getFiles(roomId: Int) {
        var fileCount = 0
        ChannelsManager.instance.getChannelMessagesByType(roomId, Constants.MessageTypes.TYPE_FILE)?.let {
            //Iterate over messages for a file count
            for (message in it) {
                message.files?.let {
                    if (it.count() > 0) {
                        fileCount += it.count()
                    }
                }
            }
            //If files are available set the fragment
            if (fileCount > 0) {
                mView.activity?.replaceFragment(R.id.achs_fragment_container,
                        FileSettingsFragment.newInstanceChannelRoom(roomId), false)
            }
        }
    }

}