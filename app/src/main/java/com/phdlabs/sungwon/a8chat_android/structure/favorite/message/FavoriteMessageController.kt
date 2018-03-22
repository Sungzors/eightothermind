package com.phdlabs.sungwon.a8chat_android.structure.favorite.message

import com.phdlabs.sungwon.a8chat_android.api.rest.Rest
import com.phdlabs.sungwon.a8chat_android.db.user.UserManager
import com.phdlabs.sungwon.a8chat_android.model.message.Message
import com.phdlabs.sungwon.a8chat_android.structure.favorite.FavoriteContract
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by SungWon on 3/20/2018.
 */
class FavoriteMessageController(val mView: FavoriteContract.Message.View) : FavoriteContract.Message.Controller {

    private val mMessages = mutableListOf<Message>()

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

    override fun getFavorites(roomId: Int) {
        getUserId { id ->
            id?.let {
                UserManager.instance.getCurrentUser { success, _, token ->
                    if (success){
                        val call = Rest.getInstance().getmCallerRx().getRoomFaveMsg(token?.token!!, roomId, it)
                        call.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({ response ->
                                    for (messages in response.messages!!){
                                        mMessages.add(messages.message!!)
                                    }
                                    mView.setUpRecycler(mMessages)

                                }, {t: Throwable? ->
                                    mView.showError("Unable to retrieve favorite message: " + t?.message + ". User ID = " + it + ", room ID = " + roomId)
                                })
                    }
                }
            }
        }
    }

    override fun getUserId(callback: (Int?) -> Unit) {
        UserManager.instance.getCurrentUser { success, user, _ ->
            if (success) {
                user?.id.let {
                    callback(it)
                }
            }
        }
    }
}