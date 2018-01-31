package com.phdlabs.sungwon.a8chat_android.api.response.createEvent

import com.phdlabs.sungwon.a8chat_android.api.response.ErrorResponse
import com.phdlabs.sungwon.a8chat_android.model.event.EventsEight
import com.phdlabs.sungwon.a8chat_android.model.room.Room

/**
 * Created by SungWon on 1/5/2018.
 * Updated by JPAM on 1/29/2018
 */
class EventPostResponse : ErrorResponse() {
    internal var room: Room? = null
    internal var newChannelGroupOrEvent: EventsEight? = null
}