package com.phdlabs.sungwon.a8chat_android.structure.main.lobby

import com.phdlabs.sungwon.a8chat_android.api.event.Event
import com.phdlabs.sungwon.a8chat_android.api.response.*
import com.phdlabs.sungwon.a8chat_android.api.response.channels.follow.ChannelFollowResponse
import com.phdlabs.sungwon.a8chat_android.api.rest.Caller
import com.phdlabs.sungwon.a8chat_android.api.rest.Rest
import com.phdlabs.sungwon.a8chat_android.api.utility.Callback8
import com.phdlabs.sungwon.a8chat_android.db.EventBusManager
import com.phdlabs.sungwon.a8chat_android.db.channels.ChannelsManager
import com.phdlabs.sungwon.a8chat_android.db.events.EventsManager
import com.phdlabs.sungwon.a8chat_android.db.user.UserManager
import com.phdlabs.sungwon.a8chat_android.model.channel.Channel
import com.phdlabs.sungwon.a8chat_android.model.event.EventsEight
import com.phdlabs.sungwon.a8chat_android.model.room.Room
import com.vicpin.krealmextensions.saveAll
import org.greenrobot.eventbus.EventBus

/**
 * Created by SungWon on 10/17/2017.
 * Updated by JPAM on 1/31/2018
 */
class LobbyController(val mView: LobbyContract.View,
                      private var refresh: Boolean) : LobbyContract.Controller {

    /*Properties*/
    private var mMyChannel = mutableListOf<Channel>()
    private var mEvents = mutableListOf<EventsEight>()
    private var mChannelsFollowed = mutableListOf<Channel>()
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
    }

    override fun resume() {
        callMyChannel(refresh)
        callEvent(refresh)
        callFollow(refresh)
        callChannel()
        callChats()
    }

    override fun pause() {

    }

    override fun stop() {
    }

    private fun callMyChannel(refresh: Boolean) {
        mView.showProgress()
        ChannelsManager.instance.getMyChannels(refresh, { response ->
            response.second?.let {
                //Error
                mView.hideProgress()
                /*When no channels are available it triggers a localized error message not wanted*/
                //mView.showError(it)
            } ?: run {
                mView.hideProgress()
                response.first?.let {
                    //Channels
                    mMyChannel = it.toMutableList()
                    if (mMyChannel.size > 0) {
                        //UI
                        mView.setUpMyChannelRecycler(mMyChannel)
                    }
                }
            }
        })
    }

    private fun callEvent(refresh: Boolean) {
        mView.showProgress()
        EventsManager.instance.getEvents(refresh, { response ->
            response.second?.let {
                // Error
                mView.hideProgress()
                /*When no events are available it triggers a localized error message not wanted*/
                //mView.showError(it)
            } ?: run {
                mView.hideProgress()
                response.first?.let {
                    //Events
                    mEvents = it.toMutableList()
                    if (mEvents.size > 0) {
                        //UI
                        mView.setUpEventsRecycler(mEvents)
                    }
                }
            }
        })
    }

    private fun callFollow(refresh: Boolean) {
        mView.showProgress()
        ChannelsManager.instance.getMyFollowedChannels(refresh, { popular, unpopular, errorMessage ->
            errorMessage?.let {
                //Error
                mView.hideProgress()
                /*When no followed channels are available it triggers a localized error message not wanted*/
                mView.showError(it)
            } ?: run {
                mView.hideProgress()
                mChannelsFollowed.clear()
                //Popular Channels
                popular?.let {
                    //Unread
                    it.first?.let {
                        mChannelsFollowed.addAll(it)
                        //TODO: Change UI for this item
                    }
                    //Read
                    it.second?.let {
                        mChannelsFollowed.addAll(it)
                    }
                }
                //Unpopular Channels
                unpopular?.let {
                    //Unread
                    it.first?.let {
                        mChannelsFollowed.addAll(it)
                        //TODO: Change UI for this item
                    }
                    //Read
                    it.second?.let {
                        mChannelsFollowed.addAll(it)
                    }
                }
                //UI
                if (mChannelsFollowed.size > 0) {
                    mView.setUpChannelsFollowedRecycler(mChannelsFollowed)
                }
            }
        })
    }

    private fun callChannel() {
        UserManager.instance.getCurrentUser { success, _, token ->
            if (success) {
                val call = mCaller.getChannel(token?.token)
                call.enqueue(object : Callback8<ChannelArrayResponse, Event>(mEventBus) {
                    override fun onSuccess(data: ChannelArrayResponse?) {
                        mChannel.addAll(data!!.channels!!)
                        if (mChannel.size > 0) {
                            mChannel.saveAll()
                            mView.setUpChannelRecycler(mChannel)
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
                            mChat.saveAll()
                            mView.setUpChatRecycler(mChat)
                        }
                    }
                })
            }
        }

    }

    override fun setRefreshFlag(shouldRefresh: Boolean) {
        refresh = shouldRefresh
    }

    override fun getRefreshFlag(): Boolean = refresh

    override fun refreshAll() {
        callMyChannel(true)
        callEvent(true)
        callFollow(true)
        callChannel()
        callChats()
    }

    override fun getMyChannel(): MutableList<Channel> = mMyChannel

    override fun getEvents(): MutableList<EventsEight> = mEvents

    override fun getChannelsFollowed(): MutableList<Channel> = mChannelsFollowed

    override fun getChannel(): MutableList<Channel> = mChannel

    override fun getChat(): MutableList<Room> = mChat

}