package com.phdlabs.sungwon.a8chat_android.api.response

import com.phdlabs.sungwon.a8chat_android.model.EventsEight
import com.phdlabs.sungwon.a8chat_android.model.Room

/**
 * Created by SungWon on 1/5/2018.
 */
class EventPostResponse: ErrorResponse() {

    var room: Room? = null
    var newChannelGroupOrEvent: EventsEight? = null
}