package com.phdlabs.sungwon.a8chat_android.api.response.createChannel

import com.phdlabs.sungwon.a8chat_android.api.response.ErrorResponse
import com.phdlabs.sungwon.a8chat_android.model.channel.Channel
import com.phdlabs.sungwon.a8chat_android.model.room.Room

/**
 * Created by SungWon on 11/30/2017.
 * Updated by JPAM on 26/01/2018
 */
class ChannelResponse : ErrorResponse() {
    internal var room: Room? = null
    internal var newChannelGroupOrEvent: Channel? = null
}