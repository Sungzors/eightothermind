package com.phdlabs.sungwon.a8chat_android.api.response

import com.phdlabs.sungwon.a8chat_android.model.channel.ChannelShowNest

/**
 * Created by SungWon on 1/5/2018.
 */
class ChannelFollowReadNest{
    internal var read : Array<ChannelShowNest>? = null
    internal var unread : Array<ChannelShowNest>? = null
}