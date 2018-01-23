package com.phdlabs.sungwon.a8chat_android.structure.setting.chat

import com.phdlabs.sungwon.a8chat_android.api.data.PrivateChatPatchData
import com.phdlabs.sungwon.a8chat_android.api.event.Event
import com.phdlabs.sungwon.a8chat_android.api.response.RoomResponse
import com.phdlabs.sungwon.a8chat_android.api.rest.Caller
import com.phdlabs.sungwon.a8chat_android.api.rest.Rest
import com.phdlabs.sungwon.a8chat_android.api.utility.Callback8
import com.phdlabs.sungwon.a8chat_android.db.EventBusManager
import com.phdlabs.sungwon.a8chat_android.structure.setting.SettingContract
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import com.phdlabs.sungwon.a8chat_android.utility.Preferences
import org.greenrobot.eventbus.EventBus

/**
 * Created by SungWon on 1/22/2018.
 */
class ChatSettingController(val mView: SettingContract.Chat.View) : SettingContract.Chat.Controller{

    private var mCaller: Caller
    private var mEventBus: EventBus
    private var mRoomID = 0
    private var mFavorited = false

    init {
        mView.controller = this
        mCaller = Rest.getInstance().caller
        mEventBus = EventBusManager.instance().mDataEventBus
    }

    override fun start() {
    }

    override fun resume() {
    }

    override fun pause() {
    }

    override fun stop() {
    }

    override fun favoriteChat() {
        val call = mCaller.favoritePrivateChatRoom(
                Preferences(mView.getContext()!!).getPreferenceString(Constants.PrefKeys.TOKEN_KEY),
                mRoomID,
                PrivateChatPatchData(
                        Preferences(mView.getContext()!!).getPreferenceString(Constants.PrefKeys.USER_ID)!!,
                        !mFavorited
                )
        )
        call.enqueue(object : Callback8<RoomResponse, Event>(mEventBus){
            override fun onSuccess(data: RoomResponse?) {
            }
        })
    }

    override fun setRoomId(roomId: Int) {
        mRoomID = roomId
    }

    override fun setFavorite(isFave: Boolean) {
        mFavorited = isFave
    }
}