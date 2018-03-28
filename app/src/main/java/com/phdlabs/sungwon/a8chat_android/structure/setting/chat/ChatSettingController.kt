package com.phdlabs.sungwon.a8chat_android.structure.setting.chat

import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.api.data.PrivateChatPatchData
import com.phdlabs.sungwon.a8chat_android.api.rest.Rest
import com.phdlabs.sungwon.a8chat_android.db.media.MediaManager
import com.phdlabs.sungwon.a8chat_android.db.room.RoomManager
import com.phdlabs.sungwon.a8chat_android.db.user.UserManager
import com.phdlabs.sungwon.a8chat_android.model.contacts.Contact
import com.phdlabs.sungwon.a8chat_android.model.room.Room
import com.phdlabs.sungwon.a8chat_android.structure.setting.SettingContract
import com.phdlabs.sungwon.a8chat_android.structure.setting.fileFragments.FileSettingsFragment
import com.phdlabs.sungwon.a8chat_android.structure.setting.mediaFragments.MediaSettingFragment
import com.vicpin.krealmextensions.query
import com.vicpin.krealmextensions.queryAll
import com.vicpin.krealmextensions.queryFirst
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


/**
 * Created by SungWon on 1/22/2018.
 * Updated by JPAM on 1/31/2018
 */
class ChatSettingController(val mView: SettingContract.Chat.View) : SettingContract.Chat.Controller {

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

    override fun favoriteRoom(room: Room?, favorite: Boolean) {
        UserManager.instance.getCurrentUser { success, user, token ->
            if (success) {
                token?.token?.let {
                    room?.id?.let {
                        user?.id?.let {
                            val call = Rest.getInstance().getmCallerRx().favoritePrivateChatRoom(
                                    token.token!!,
                                    room.id!!,
                                    PrivateChatPatchData(user.id!!, favorite))
                            call.subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({ response ->
                                        if (response.isSuccess) { //Room is favorite & Update in Realm
                                            //User Feedback
                                            response?.userRoom?.favorite?.let {
                                                //User feedback
                                                if (it) {
                                                    mView.feedback("Favorite!")
                                                } else {
                                                    mView.feedback(":(")
                                                }
                                            }
                                        } else if (response.isError) {//Room couldn't be favorite
                                            mView.showError("Could not favorite contact")
                                            mView.couldNotFavoriteContact()
                                        }
                                    }, { throwable ->
                                        mView.showError("Could not favorite contact")
                                        mView.couldNotFavoriteContact()
                                        println("Error updating favorite room: " + throwable.message)
                                    })

                        }
                    }
                }
            }
        }
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

    /*Get contact information from Realm*/
    override fun getContactInfo(id: Int): Contact? {
        if (Contact().queryAll().count() > 0) {
            return Contact().queryFirst { equalTo("id", id) }
        }
        return null
    }

    /*Get room information from Realm*/
    override fun getRoomInfo(id: Int): Room? {
        if (Room().queryAll().isNotEmpty()) {
            return Room().queryFirst { equalTo("id", id) }
        }
        return null
    }

    override fun getSharedMediaPrivate(contactId: Int) {
        MediaManager.instance.getPrivateMedia(contactId, {
            if (it.second == null) { //Success
                //Set fragment
                mView.activity?.replaceFragment(R.id.asc_fragment_container,
                        MediaSettingFragment.newInstanceChatRoom(contactId), false)
            }
        })
    }

    override fun getSharedFilesPrivate(chatRoomId: Int) {
        RoomManager.instance.getFilesFromPrivateChat(chatRoomId)?.let {
            if (it.count() > 0) {
                mView.activity?.replaceFragment(R.id.asc_fragment_container,
                        FileSettingsFragment.newInstanceChatRoom(chatRoomId), false)
            }
        }
    }
}