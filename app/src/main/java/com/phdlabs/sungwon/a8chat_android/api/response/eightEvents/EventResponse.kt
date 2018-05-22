package com.phdlabs.sungwon.a8chat_android.api.response.eightEvents

import com.google.gson.annotations.SerializedName
import com.phdlabs.sungwon.a8chat_android.api.response.ErrorResponse
import com.phdlabs.sungwon.a8chat_android.model.event.EventsEight

/**
 * Created by SungWon on 1/24/2018.
 * [EventResponse]
 * Get all events in Array
 */
class EventResponse: ErrorResponse() {
    @SerializedName("events")
    var events: List<EventsEight>? = null
}