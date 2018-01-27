package com.phdlabs.sungwon.a8chat_android.api.response

import com.phdlabs.sungwon.a8chat_android.model.NewChannelGroupOrEvent
import com.phdlabs.sungwon.a8chat_android.model.Room

/**
 * Created by SungWon on 11/30/2017.
 * Updated by JPAM on 26/01/2018
 */
class ChannelResponse : ErrorResponse() {
    internal var room: Room? = null
    internal var newNewChannelGroupOrEvent: NewChannelGroupOrEvent? = null
}