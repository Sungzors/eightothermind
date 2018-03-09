package com.phdlabs.sungwon.a8chat_android.structure.channel.channelshow

import android.widget.Toast
import com.phdlabs.sungwon.a8chat_android.api.event.ChannelPostGetEvent
import com.phdlabs.sungwon.a8chat_android.api.event.Event
import com.phdlabs.sungwon.a8chat_android.api.event.PostLikeEvent
import com.phdlabs.sungwon.a8chat_android.api.response.channels.MyChannelRoomsResponse
import com.phdlabs.sungwon.a8chat_android.api.response.ErrorResponse
import com.phdlabs.sungwon.a8chat_android.api.response.RoomHistoryResponse
import com.phdlabs.sungwon.a8chat_android.api.rest.Caller
import com.phdlabs.sungwon.a8chat_android.api.rest.Rest
import com.phdlabs.sungwon.a8chat_android.api.utility.Callback8
import com.phdlabs.sungwon.a8chat_android.db.EventBusManager
import com.phdlabs.sungwon.a8chat_android.db.user.UserManager
import com.phdlabs.sungwon.a8chat_android.structure.channel.ChannelContract
import org.greenrobot.eventbus.EventBus
import retrofit2.Response

/**
 * Created by SungWon on 12/12/2017.
 * Updated by JPAM on 1/31/2018
 */
class ChannelShowController(val mView : ChannelContract.ChannelShow.View): ChannelContract.ChannelShow.Controller{

    private var mCaller: Caller
    private var mEventBus: EventBus

    init {
        mView.controller = this
        mCaller = Rest.getInstance().caller
        mEventBus = EventBusManager.instance().mDataEventBus
    }

    override fun start() {
        loadChannels()
    }

    override fun resume() {
    }

    override fun pause() {
    }

    override fun stop() {
    }

    private fun loadChannels(){
        //TODO: Replace with manager
//        UserManager.instance.getCurrentUser { success, user, token ->
//            if (success) {
//                mView.showProgress()
//                val call = mCaller.getAssociatedChannels(token?.token, user?.id!!)
//                call.enqueue(object : Callback8<MyChannelRoomsResponse, Event>(mEventBus) {
//                    override fun onSuccess(data: MyChannelRoomsResponse?) {
//                        mView.addToChannels(data!!.channels!!)
//                        mView.setUpTopRecycler()
//                    }
//                })
//            }
//        }
    }

    override fun loadChannel(roomID: Int) {
//        UserManager.instance.getCurrentUser { success, user, token ->
//            if (success) {
//                mView.showProgress()
//                val call = mCaller.getChannelPosts(token?.token, roomID, user?.id!!, null)
//                call.enqueue(object : Callback8<RoomHistoryResponse, ChannelPostGetEvent>(mEventBus) {
//                    override fun onSuccess(data: RoomHistoryResponse?) {
//                        mView.addToPosts(data!!.messages!!.allMessages!!)
//                        mView.setUpPostRecycler()
//                    }
//                })
//            }
//        }
    }

    override fun likePost(messageId: Int?) {
//        messageId?.let {
//            UserManager.instance.getCurrentUser { success, user, token ->
//                if (success) {
//                    val call = mCaller.likePost(token?.token, messageId, user?.id!!)
//                    call.enqueue(object : Callback8<ErrorResponse, PostLikeEvent>(mEventBus) {
//                        override fun onSuccess(data: ErrorResponse?) {
//                            mView.onLike(messageId)
//                            Toast.makeText(mView.getContext()!!, "Post Liked!", Toast.LENGTH_SHORT).show()
//                        }
//
//                        override fun onError(response: Response<ErrorResponse>?) {
//                            Toast.makeText(mView.getContext()!!, "You have already liked this post!", Toast.LENGTH_SHORT).show()
//                        }
//                    })
//                }
//            }
//        }
    }

    override fun commentPost(messageId: Int?) {
    }
}