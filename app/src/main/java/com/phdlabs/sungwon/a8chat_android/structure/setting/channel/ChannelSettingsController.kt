package com.phdlabs.sungwon.a8chat_android.structure.setting.channel

import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.db.channels.ChannelsManager
import com.phdlabs.sungwon.a8chat_android.db.room.RoomManager
import com.phdlabs.sungwon.a8chat_android.db.user.UserManager
import com.phdlabs.sungwon.a8chat_android.model.room.Room
import com.phdlabs.sungwon.a8chat_android.model.user.User
import com.phdlabs.sungwon.a8chat_android.structure.setting.SettingContract
import com.phdlabs.sungwon.a8chat_android.structure.setting.fileFragments.FileSettingsFragment
import com.phdlabs.sungwon.a8chat_android.structure.setting.mediaFragments.MediaSettingFragment
import com.phdlabs.sungwon.a8chat_android.utility.Constants

/**
 * Created by JPAM on 3/12/18.
 * [ChannelSettingsController]
 * Used for business logic on [ChannelSettingsActivity]
 */
class ChannelSettingsController(val mView: SettingContract.Channel.View) : SettingContract.Channel.Controller {

    /*Properties*/
    private var mUser: User? = null

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

    /*Application User*/
    override fun getAppUserId(callback: (userId: Int?) -> Unit) {
        UserManager.instance.getCurrentUser { success, user, _ ->
            if (success) {
                user?.let {
                    mUser = user
                    user.id?.let {
                        callback(it)
                    }
                }
            }
        }
    }

    /*Get room information from Realm*/

    override fun getRoomInfo(id: Int, callback: (Room?) -> Unit) {
        RoomManager.instance.getRoomInfo(id, { response ->
            response.second?.let {
                mView.showError(it)
            } ?: run {
                response.first?.let(callback)
            }
        })
    }


    override fun getRoomParticipants(id: Int, callback: (MutableList<Int>?) -> Unit) {
        getRoomInfo(id, {
            val participantsId = mutableListOf<Int>()
            it?.participantsId?.let {
                for (participantId in it) {
                    participantId?.intValue?.let {
                        participantsId.add(it)
                    }
                }
                callback(participantsId)
            }
        })
    }

    override fun getFavorite(roomId: Int, callback: (Int) -> Unit) {
        UserManager.instance.getFavoritesCount(roomId, { count, errorMessage ->
            errorMessage?.let {
                mView.showError(it)
            } ?: run {
                callback(count!!)
            }
        })
    }

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
        ChannelsManager.instance.queryChannelMessagesByType(roomId, Constants.MessageTypes.TYPE_MEDIA)?.let {
            //Iterate over messages for media count
            for (message in it) {
                message.mediaArray?.let {
                    if (it.count() > 0) {
                        mediaCount += it.count()
                    }
                }
            }
            //If media is available set the fragment
            mView.activity?.replaceFragment(R.id.achs_fragment_container,
                    MediaSettingFragment.newInstanceChannelRoom(roomId), false)
        }
    }

    /*FILES*/
    override fun getFiles(roomId: Int) {
        var fileCount = 0
        ChannelsManager.instance.queryChannelMessagesByType(roomId, Constants.MessageTypes.TYPE_FILE)?.let {
            //Iterate over messages for a file count
            for (message in it) {
                message.files?.let {
                    if (it.count() > 0) {
                        fileCount += it.count()
                    }
                }
            }
            //If files are available set the fragment
            mView.activity?.replaceFragment(R.id.achs_fragment_container,
                    FileSettingsFragment.newInstanceChannelRoom(roomId), false)
        }
    }

    /*Channel*/
    override fun followChannel(channelId: Int, followerId: Int) {
        ChannelsManager.instance.followChannel(channelId, followerId, {
            mView.userFeedback(it)
            mView.updateRoomInfo()
        })
    }

    override fun unfollowChannel(roomId: Int) {
        ChannelsManager.instance.unfollowChannel(roomId, { message ->
            message?.let {
                mView.userFeedback(it)
                mView.updateRoomInfo()
            }
        })
    }

    override fun deleteChannel(channelId: Int) {
        ChannelsManager.instance.deleteChannel(channelId, {
            //Channel Deleted
            mView.channelDeleted()
        })
    }

}