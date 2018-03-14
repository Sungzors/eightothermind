package com.phdlabs.sungwon.a8chat_android.structure.setting.channel

import com.phdlabs.sungwon.a8chat_android.db.user.UserManager
import com.phdlabs.sungwon.a8chat_android.model.room.Room
import com.phdlabs.sungwon.a8chat_android.structure.setting.SettingContract
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

}