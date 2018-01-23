package com.phdlabs.sungwon.a8chat_android.structure.main.lobby

import com.phdlabs.sungwon.a8chat_android.api.event.Event
import com.phdlabs.sungwon.a8chat_android.api.response.ChannelArrayResponse
import com.phdlabs.sungwon.a8chat_android.api.response.ChannelFollowResponse
import com.phdlabs.sungwon.a8chat_android.api.response.ChannelShowArrayResponse
import com.phdlabs.sungwon.a8chat_android.api.rest.Caller
import com.phdlabs.sungwon.a8chat_android.api.rest.Rest
import com.phdlabs.sungwon.a8chat_android.api.utility.Callback8
import com.phdlabs.sungwon.a8chat_android.db.EventBusManager
import com.phdlabs.sungwon.a8chat_android.model.Channel
import com.phdlabs.sungwon.a8chat_android.model.EventsEight
import com.phdlabs.sungwon.a8chat_android.model.Room
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import com.phdlabs.sungwon.a8chat_android.utility.Preferences
import org.greenrobot.eventbus.EventBus

/**
 * Created by SungWon on 10/17/2017.
 */
class LobbyController(val mView: LobbyContract.View): LobbyContract.Controller {

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
    }

    override fun resume() {
    }

    override fun pause() {
    }

    override fun stop() {
    }

    private fun callMyChannel(){
        val call = mCaller.getAssociatedChannels(Preferences(mView.getContext()!!).getPreferenceString(Constants.PrefKeys.TOKEN_KEY), Preferences(mView.getContext()!!).getPreferenceInt(Constants.PrefKeys.USER_ID))
        call.enqueue(object: Callback8<ChannelShowArrayResponse, Event>(mEventBus){
            override fun onSuccess(data: ChannelShowArrayResponse?) {
                for (channel in data!!.channels!!){
                    mMyChannel.addAll(channel.channels)
                }
                if(mMyChannel.size>0){
                    mView.setUpMyChannelRecycler()
                }
            }
        })
    }

    private fun callEvent(){

    }

    private fun callFollow(){
        val call = mCaller.getFollowChannel(Preferences(mView.getContext()!!).getPreferenceString(Constants.PrefKeys.TOKEN_KEY), Preferences(mView.getContext()!!).getPreferenceInt(Constants.PrefKeys.USER_ID))
        call.enqueue(object : Callback8<ChannelFollowResponse, Event>(mEventBus){
            override fun onSuccess(data: ChannelFollowResponse?) {
                for(channel in data!!.channels!!.popular!!.unread!!){
                    mChannelsFollowed.addAll(channel.channels)
                }
                for (channel in data.channels!!.popular!!.read!!){
                    mChannelsFollowed.addAll(channel.channels)
                }
                for (channel in data.channels!!.unpopular!!.unread!!){
                    mChannelsFollowed.addAll(channel.channels)
                }
                for (channel in data.channels!!.unpopular!!.read!!){
                    mChannelsFollowed.addAll(channel.channels)
                }
                if(mChannelsFollowed.size>0){
                    mView.setUpChannelsFollowedRecycler()
                }

            }
        })
    }

    private fun callChannel() {
        val call = mCaller.getChannel(Preferences(mView.getContext()!!).getPreferenceString(Constants.PrefKeys.TOKEN_KEY))
        call.enqueue(object: Callback8<ChannelArrayResponse, Event>(mEventBus){
            override fun onSuccess(data: ChannelArrayResponse?) {
                mChannel.addAll(data!!.channels!!)
                if(mChannel.size>0){
                    mView.setUpChannelRecycler()
                }
            }
        })
    }

    override fun getMyChannel(): MutableList<Channel> = mMyChannel

    override fun getEvents(): MutableList<EventsEight> = mEvents

    override fun getChannelsFollowed(): MutableList<Channel> = mChannelsFollowed

    override fun getChannel(): MutableList<Channel> = mChannel

    override fun getChat(): MutableList<Room> = mChat
}