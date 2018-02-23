package com.phdlabs.sungwon.a8chat_android.structure.setting.chat

import com.phdlabs.sungwon.a8chat_android.api.data.PrivateChatPatchData
import com.phdlabs.sungwon.a8chat_android.api.rest.Rest
import com.phdlabs.sungwon.a8chat_android.db.UserManager
import com.phdlabs.sungwon.a8chat_android.model.contacts.Contact
import com.phdlabs.sungwon.a8chat_android.model.room.Room
import com.phdlabs.sungwon.a8chat_android.structure.setting.SettingContract
import com.vicpin.krealmextensions.query
import com.vicpin.krealmextensions.queryAll
import com.vicpin.krealmextensions.save
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

//                                            val updatedRoom = response.room
//                                            updatedRoom?.user = room.user
//                                            updatedRoom?.user?.userRooms = response.userRoom
//                                            updatedRoom?.save()

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

    /*Get contact information from Realm*/
    override fun getContactInfo(id: Int): Contact? {
        if(Contact().queryAll().count() > 0) {
           return Contact().query { it.equalTo("id", id) }[0]
        }
        return null
    }

    /*Get room information from Realm*/
    override fun getRoomInfo(id: Int): Room? {
        if (Room().queryAll().count() > 0) {
            return Room().query { it.equalTo("id", id) }[0]
        }
        return null
    }
}