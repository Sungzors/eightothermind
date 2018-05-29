package com.phdlabs.sungwon.a8chat_android.structure.channel.mychannels

import com.phdlabs.sungwon.a8chat_android.api.event.ChannelGetEvent
import com.phdlabs.sungwon.a8chat_android.api.response.ChannelArrayResponse
import com.phdlabs.sungwon.a8chat_android.api.rest.Caller
import com.phdlabs.sungwon.a8chat_android.api.rest.Rest
import com.phdlabs.sungwon.a8chat_android.api.utility.Callback8
import com.phdlabs.sungwon.a8chat_android.db.EventBusManager
import com.phdlabs.sungwon.a8chat_android.db.TemporaryManager
import com.phdlabs.sungwon.a8chat_android.db.channels.ChannelsManager
import com.phdlabs.sungwon.a8chat_android.db.user.UserManager
import com.phdlabs.sungwon.a8chat_android.structure.channel.ChannelContract
import org.greenrobot.eventbus.EventBus

/**
 * Created by SungWon on 12/6/2017.
 */
class MyChannelsListController(val mView: ChannelContract.MyChannelsList.View) : ChannelContract.MyChannelsList.Controller {

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
        UserManager.instance.getCurrentUser { success, user, _ ->
            if (success) {
                user?.id?.let {
                    ChannelsManager.instance.getUserChannels(it, true, {
                        it.second?.let {
                            mView.showError(it)
                        } ?: run {
                            it.first?.let {
                                for (channel in it) {
                                    mView.addChannel(channel)
                                }
                                mView.updateRecycler()
                            }
                        }
                    })
                }
            }
        }
    }
}