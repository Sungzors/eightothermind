package com.phdlabs.sungwon.a8chat_android.structure.channel.create

import com.phdlabs.sungwon.a8chat_android.api.data.PostChannelData
import com.phdlabs.sungwon.a8chat_android.api.event.ChannelPostEvent
import com.phdlabs.sungwon.a8chat_android.api.response.ChannelResponse
import com.phdlabs.sungwon.a8chat_android.api.rest.Caller
import com.phdlabs.sungwon.a8chat_android.api.rest.Rest
import com.phdlabs.sungwon.a8chat_android.api.utility.Callback8
import com.phdlabs.sungwon.a8chat_android.db.EventBusManager
import com.phdlabs.sungwon.a8chat_android.db.TemporaryManager
import com.phdlabs.sungwon.a8chat_android.model.Channel
import com.phdlabs.sungwon.a8chat_android.structure.channel.ChannelContract
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import com.phdlabs.sungwon.a8chat_android.utility.Preferences
import org.greenrobot.eventbus.EventBus

/**
 * Created by SungWon on 11/30/2017.
 */
class ChannelCreateController(val mView : ChannelContract.Create.View): ChannelContract.Create.Controller{
    private val TAG = "ChannelCreateController"

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

    override fun createChannel(data: PostChannelData) {
        val call = mCaller.postChannel(Preferences(mView.getContext()!!).getPreferenceString(Constants.PrefKeys.TOKEN_KEY), data)
        call.enqueue(object : Callback8<ChannelResponse, ChannelPostEvent>(mEventBus){
            override fun onSuccess(data: ChannelResponse?) {
                val a = data
                val chan = Channel(data!!.newChannelGroupOrEvent!!.id.toInt(), data.newChannelGroupOrEvent!!.name, data.newChannelGroupOrEvent!!.name, /*data.newChannelGroupOrEvent!!.roomId*/ data.newChannelGroupOrEvent!!.room_id)
                chan.user_creator_id = data.newChannelGroupOrEvent!!.user_creator_id
                chan.profile_picture_string = data.newChannelGroupOrEvent!!.profile_picture_string
                chan.isRead = true
                TemporaryManager.instance.mChannelList.add(chan) //TODO: add realm
                //TODO: lead to channel screen
                mView.finishActivity()
            }
        })
    }

    override fun uploadPicture() {
    }

}