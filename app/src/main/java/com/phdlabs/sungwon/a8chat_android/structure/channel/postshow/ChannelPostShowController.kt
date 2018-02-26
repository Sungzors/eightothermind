package com.phdlabs.sungwon.a8chat_android.structure.channel.postshow

import android.widget.Toast
import com.phdlabs.sungwon.a8chat_android.api.event.PostLikeEvent
import com.phdlabs.sungwon.a8chat_android.api.response.ErrorResponse
import com.phdlabs.sungwon.a8chat_android.api.rest.Caller
import com.phdlabs.sungwon.a8chat_android.api.rest.Rest
import com.phdlabs.sungwon.a8chat_android.api.utility.Callback8
import com.phdlabs.sungwon.a8chat_android.db.EventBusManager
import com.phdlabs.sungwon.a8chat_android.db.user.UserManager
import com.phdlabs.sungwon.a8chat_android.structure.channel.ChannelContract
import org.greenrobot.eventbus.EventBus
import retrofit2.Response

/**
 * Created by SungWon on 12/4/2017.
 */
class ChannelPostShowController(val mView: ChannelContract.PostShow.View): ChannelContract.PostShow.Controller {

    private lateinit var mCaller: Caller
    private lateinit var mEventBus: EventBus

    init {
        mView.controller = this
    }

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

    override fun likePost(messageId: Int?, userId: Int?) {
        UserManager.instance.getCurrentUser { success, user, token ->
            if (success) {
                val call = mCaller.likePost(token?.token, mView.getMessageId!!, user?.id!!)
                call.enqueue(object : Callback8<ErrorResponse, PostLikeEvent>(mEventBus) {
                    override fun onSuccess(data: ErrorResponse?) {
                        mView.onLike()
                        Toast.makeText(mView.getContext()!!, "Post Liked!", Toast.LENGTH_SHORT).show()
                    }

                    override fun onError(response: Response<ErrorResponse>?) {
                        Toast.makeText(mView.getContext()!!, "You have already liked this post!", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }

    override fun commentPost(messageId: String) {
        Toast.makeText(mView.getContext()!!, "To be implemented", Toast.LENGTH_SHORT).show()
    }

}