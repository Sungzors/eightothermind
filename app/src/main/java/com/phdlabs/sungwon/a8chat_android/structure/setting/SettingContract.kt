package com.phdlabs.sungwon.a8chat_android.structure.setting

import com.phdlabs.sungwon.a8chat_android.model.contacts.Contact
import com.phdlabs.sungwon.a8chat_android.model.files.File
import com.phdlabs.sungwon.a8chat_android.model.media.Media
import com.phdlabs.sungwon.a8chat_android.model.room.Room
import com.phdlabs.sungwon.a8chat_android.model.user.User
import com.phdlabs.sungwon.a8chat_android.structure.core.BaseController
import com.phdlabs.sungwon.a8chat_android.structure.core.BaseView
import com.phdlabs.sungwon.a8chat_android.structure.setting.channel.ChannelSettingsActivity
import com.phdlabs.sungwon.a8chat_android.structure.setting.chat.ChatSettingActivity

/**
 * Created by SungWon on 1/22/2018.
 * Updated by JPAM on 02/22/2018
 */
interface SettingContract {

    /**
     * Chat Settings
     * */
    interface Chat {
        interface View : BaseView<Controller> {

            var activity: ChatSettingActivity?

            /*Could not favorite contact*/
            fun couldNotFavoriteContact()

            /*User feedback*/
            fun feedback(message: String)

        }

        interface Controller : BaseController {

            /*Update room to favorite*/
            fun favoriteRoom(room: Room?, favorite: Boolean)

            fun getFavorite(roomId: Int, callback: (Int) -> Unit)

            /*Retrieve cached contact information*/
            fun getContactInfo(id: Int): Contact?

            /*Retrieve room Info*/
            fun getRoomInfo(id: Int): Room?

            /*Get shared media between two users*/
            fun getSharedMediaPrivate(contactId: Int)

            /*Get shared files between two users*/
            fun getSharedFilesPrivate(chatRoomId: Int)


        }
    }

    /**
     * Channel Settings
     * */
    interface Channel {

        interface View : BaseView<Controller> {

            var activity: ChannelSettingsActivity?
            //Channel Owner Info
            fun updateChannelOwnerInfo(channelOwner: User)

            /*Room*/
            fun updateRoomInfo()

            fun updateRoomParticipants(participantId: Int, remove: Boolean)

            /*Feedback*/
            fun userFeedback(message: String)

            /*Channel Deleted*/
            fun channelDeleted()

        }

        interface Controller : BaseController {

            /*Retrieve room Info*/
            fun getRoomInfo(id: Int, callback: (Room?) -> Unit)

            fun getRoomParticipants(id: Int, callback: (MutableList<Int>?) -> Unit)

            fun getFavorite(roomId: Int, callback: (Int) -> Unit)

            /*Channel Owner Information*/
            fun getChannelOwnerInfo(ownerId: Int)

            /*Media & Files*/
            fun getMedia(roomId: Int)

            fun getFiles(roomId: Int)

            /*App User*/
            fun getAppUserId(callback: (userId: Int?) -> Unit)

            /*Follow Channel*/
            fun followChannel(channelId: Int, followerId: Int)

            /*Un-follow Channel*/
            fun unfollowChannel(roomId: Int)

            /*Delete Channel*/
            fun deleteChannel(channelId: Int)
        }

    }

    /**
     * Media Fragment
     * */
    interface MediaFragment {
        interface View : BaseView<Controller> {
            /*Activities*/
            var chatActivity: ChatSettingActivity
            var channelActivity: ChannelSettingsActivity
        }

        interface Controller : BaseController {

            /*Query Content*/
            fun queryMediaForChatRoom(contactId: Int): List<Media>?

            fun queryMediaForChannelRoom(roomId: Int, callback: (List<Media>?) -> Unit)

        }
    }

    /**
     * File Fragment
     * */
    interface FileFragment {

        interface View : BaseView<Controller> {
            /*Activities*/
            var chatActivity: ChatSettingActivity
            var channelActivity: ChannelSettingsActivity
            fun updateFileAdapter(fileList: List<File>?)
        }

        interface Controller : BaseController {
            fun queryFiilesForChatRoom(chatRoomId: Int)
            fun queryFilesForChannelRoom(roomId: Int)
        }

    }
}