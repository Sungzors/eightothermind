package com.phdlabs.sungwon.a8chat_android.structure.channel.channelshow

import android.widget.Toast
import com.phdlabs.sungwon.a8chat_android.api.event.ChannelPostGetEvent
import com.phdlabs.sungwon.a8chat_android.api.event.PostLikeEvent
import com.phdlabs.sungwon.a8chat_android.api.response.ErrorResponse
import com.phdlabs.sungwon.a8chat_android.api.response.RoomHistoryResponse
import com.phdlabs.sungwon.a8chat_android.api.rest.Caller
import com.phdlabs.sungwon.a8chat_android.api.rest.Rest
import com.phdlabs.sungwon.a8chat_android.api.utility.Callback8
import com.phdlabs.sungwon.a8chat_android.db.EventBusManager
import com.phdlabs.sungwon.a8chat_android.structure.channel.ChannelContract
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import com.phdlabs.sungwon.a8chat_android.utility.Preferences
import org.greenrobot.eventbus.EventBus
import retrofit2.Response

/**
 * Created by SungWon on 12/12/2017.
 */
class ChannelShowController(val mView : ChannelContract.ChannelShow.View): ChannelContract.ChannelShow.Controller{

    private lateinit var mCaller: Caller
    private lateinit var mEventBus: EventBus

    override fun start() {
        mCaller = Rest.getInstance().caller
        mEventBus = EventBusManager.instance().mDataEventBus
    }

    override fun resume() {
    }

    override fun pause() {
    }

    override fun stop() {
    }

    override fun loadChannel(roomID: Int) {
        mView.showProgress()
        val pref = Preferences(mView.getContext()!!)
        val call = mCaller.getChannelPosts(pref.getPreferenceString(Constants.PrefKeys.TOKEN_KEY), roomID, pref.getPreferenceInt(Constants.PrefKeys.USER_ID), null)
        call.enqueue(object : Callback8<RoomHistoryResponse, ChannelPostGetEvent>(mEventBus){
            override fun onSuccess(data: RoomHistoryResponse?) {
                mView.addToPosts(data!!.messages!!.allMessages!!)
                mView.setUpPostRecycler()
            }
        })
    }

    override fun likePost(messageId: String) {
        val call = mCaller.likePost(Preferences(mView.getContext()!!).getPreferenceString(Constants.PrefKeys.TOKEN_KEY), messageId.toInt(), Preferences(mView.getContext()!!).getPreferenceInt(Constants.PrefKeys.USER_ID))
        call.enqueue(object: Callback8<ErrorResponse, PostLikeEvent>(mEventBus){
            override fun onSuccess(data: ErrorResponse?) {
                mView.onLike(messageId)
                Toast.makeText(mView.getContext()!!, "Post Liked!", Toast.LENGTH_SHORT).show()
            }

            override fun onError(response: Response<ErrorResponse>?) {
                Toast.makeText(mView.getContext()!!, "You have already liked this post!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun commentPost(messageId: String) {
    }
}