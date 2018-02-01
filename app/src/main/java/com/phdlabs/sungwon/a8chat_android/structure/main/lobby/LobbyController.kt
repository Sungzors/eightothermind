package com.phdlabs.sungwon.a8chat_android.structure.main.lobby

import com.phdlabs.sungwon.a8chat_android.api.event.Event
import com.phdlabs.sungwon.a8chat_android.api.response.*
import com.phdlabs.sungwon.a8chat_android.api.rest.Caller
import com.phdlabs.sungwon.a8chat_android.api.rest.Rest
import com.phdlabs.sungwon.a8chat_android.api.utility.Callback8
import com.phdlabs.sungwon.a8chat_android.db.EventBusManager
import com.phdlabs.sungwon.a8chat_android.db.UserManager
import com.phdlabs.sungwon.a8chat_android.model.event.EventsEight
import com.phdlabs.sungwon.a8chat_android.model.channel.Channel
import com.phdlabs.sungwon.a8chat_android.model.room.Room
import org.greenrobot.eventbus.EventBus

/**
 * Created by SungWon on 10/17/2017.
 * Updated by JPAM on 1/31/2018
 */
class LobbyController(val mView: LobbyContract.View) : LobbyContract.Controller {

    //connects to LobbyFragment

    private val mMyChannel = mutableListOf<Channel>()
    private val mEvents = mutableListOf<EventsEight>()
    private val mChannelsFollowed = mutableListOf<Channel>()
    private val mChannel = mutableListOf<Channel>()
    private val mChat = mutableListOf<Room>()

    private val mCaller: Caller
    private val mEventBus: EventBus

    init {
        mView.controller = this
        mCaller = Rest.getInstance().caller
        mEventBus = EventBusManager.instance().mDataEventBus
    }

    override fun start() {
        callMyChannel()
        callEvent()
        callFollow()
        callChannel()
        callChats()
    }

    override fun resume() {
    }

    override fun pause() {
    }

    override fun stop() {
    }

    private fun callMyChannel() {
        UserManager.instance.getCurrentUser { success, user, token ->
            if (success) {
                val call = mCaller.getAssociatedChannels(token?.token, user?.id!!)
                call.enqueue(object : Callback8<ChannelShowArrayResponse, Event>(mEventBus) {
                    override fun onSuccess(data: ChannelShowArrayResponse?) {
                        for (channel in data!!.channels!!) {
                            mMyChannel.addAll(channel.channels)
                        }
                        if (mMyChannel.size > 0) {
                            mView.setUpMyChannelRecycler()
                        }
                    }
                })
            }
        }

    }

    private fun callEvent() {
        UserManager.instance.getCurrentUser { success, user, token ->
            if (success) {
                val call = mCaller.getEvents(token?.token, user?.id!!)
                call.enqueue(object : Callback8<EventRetrievalResponse, Event>(mEventBus) {
                    override fun onSuccess(data: EventRetrievalResponse?) {
                        mEvents.addAll(data!!.events!!)
                        if (mEvents.size > 0) {
                            mView.setUpEventsRecycler()
                        }
                    }
                })
            }
        }
    }

    private fun callFollow() {
        UserManager.instance.getCurrentUser { success, user, token ->
            if (success) {
                val call = mCaller.getFollowChannel(token?.token, user?.id!!)
                call.enqueue(object : Callback8<ChannelFollowResponse, Event>(mEventBus) {
                    override fun onSuccess(data: ChannelFollowResponse?) {
                        for (channel in data!!.channels!!.popular!!.unread!!) {
                            mChannelsFollowed.addAll(channel.channels)
                        }
                        for (channel in data.channels!!.popular!!.read!!) {
                            mChannelsFollowed.addAll(channel.channels)
                        }
                        for (channel in data.channels!!.unpopular!!.unread!!) {
                            mChannelsFollowed.addAll(channel.channels)
                        }
                        for (channel in data.channels!!.unpopular!!.read!!) {
                            mChannelsFollowed.addAll(channel.channels)
                        }
                        if (mChannelsFollowed.size > 0) {
                            mView.setUpChannelsFollowedRecycler()
                        }

                    }
                })
            }
        }
    }

    private fun callChannel() {
        UserManager.instance.getCurrentUser { success, _, token ->
            if (success) {
                val call = mCaller.getChannel(token?.token)
                call.enqueue(object : Callback8<ChannelArrayResponse, Event>(mEventBus) {
                    override fun onSuccess(data: ChannelArrayResponse?) {
                        mChannel.addAll(data!!.channels!!)
                        if (mChannel.size > 0) {
                            mView.setUpChannelRecycler()
                        }
                    }
                })
            }
        }
    }

    private fun callChats() {
        UserManager.instance.getCurrentUser { success, user, token ->
            if (success) {
                val call = mCaller.getAllChats(token?.token, user?.id!!)
                call.enqueue(object : Callback8<ChatsRetrievalResponse, Event>(mEventBus) {
                    override fun onSuccess(data: ChatsRetrievalResponse?) {
                        mChat.addAll(data!!.chats!!)
                        if (mChat.size > 0) {
                            mView.setUpChatRecycler()
                        }
                    }
                })
            }
        }

    }

    override fun getMyChannel(): MutableList<Channel> = mMyChannel

    override fun getEvents(): MutableList<EventsEight> = mEvents

    override fun getChannelsFollowed(): MutableList<Channel> = mChannelsFollowed

    override fun getChannel(): MutableList<Channel> = mChannel

    override fun getChat(): MutableList<Room> = mChat
}