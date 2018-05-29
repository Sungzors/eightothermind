package com.phdlabs.sungwon.a8chat_android.api.response.eightEvents

import com.phdlabs.sungwon.a8chat_android.api.response.ErrorResponse
import com.phdlabs.sungwon.a8chat_android.model.event.EventsEight

/**
 * Created by SungWon on 4/5/2018.
 */
class EventNearbyResponse: ErrorResponse() {
    val rooms: Array<EventsEight>? = null
}