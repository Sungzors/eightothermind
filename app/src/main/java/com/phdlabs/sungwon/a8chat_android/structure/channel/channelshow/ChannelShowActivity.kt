package com.phdlabs.sungwon.a8chat_android.structure.channel.channelshow

import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.structure.channel.ChannelContract
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity

/**
 * Created by SungWon on 12/12/2017.
 */
class ChannelShowActivity: CoreActivity(), ChannelContract.ChannelShow.View{
    override lateinit var controller: ChannelContract.ChannelShow.Controller

    override fun layoutId(): Int = R.layout.activity_channel

    override fun contentContainerId(): Int = 0


}