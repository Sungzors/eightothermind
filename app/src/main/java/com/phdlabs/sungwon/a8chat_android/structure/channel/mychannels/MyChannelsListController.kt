package com.phdlabs.sungwon.a8chat_android.structure.channel.mychannels

import com.phdlabs.sungwon.a8chat_android.api.event.ChannelGetEvent
import com.phdlabs.sungwon.a8chat_android.api.response.ChannelArrayResponse
import com.phdlabs.sungwon.a8chat_android.api.rest.Caller
import com.phdlabs.sungwon.a8chat_android.api.rest.Rest
import com.phdlabs.sungwon.a8chat_android.api.utility.Callback8
import com.phdlabs.sungwon.a8chat_android.db.EventBusManager
import com.phdlabs.sungwon.a8chat_android.db.TemporaryManager
import com.phdlabs.sungwon.a8chat_android.structure.channel.ChannelContract
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import com.phdlabs.sungwon.a8chat_android.utility.Preferences
import org.greenrobot.eventbus.EventBus

/**
 * Created by SungWon on 12/6/2017.
 */
class MyChannelsListController(val mView:ChannelContract.MyChannelsList.View): ChannelContract.MyChannelsList.Controller{

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

    override fun retrieveChannels() {
        val call = mCaller.getChannel(Preferences(mView.getContext()!!).getPreferenceString(Constants.PrefKeys.TOKEN_KEY))
        call.enqueue(object: Callback8<ChannelArrayResponse, ChannelGetEvent>(mEventBus){
            override fun onSuccess(data: ChannelArrayResponse?) {
                mEventBus.post(ChannelGetEvent())
                TemporaryManager.instance.mChannelList.clear()
                for(channel in data!!.channels!!){
                    mView.addChannel(channel)
                    TemporaryManager.instance.mChannelList.add(channel)
                }
                mView.updateRecycler()
            }
        })
    }
}