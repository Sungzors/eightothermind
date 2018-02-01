package com.phdlabs.sungwon.a8chat_android.model

import com.phdlabs.sungwon.a8chat_android.model.channel.Channel

/**
 * Created by SungWon on 12/21/2017.
 *
 * //TODO: Remove & start using realm
 */
data class ChannelShowNest(
        val channels: Array<Channel>
)