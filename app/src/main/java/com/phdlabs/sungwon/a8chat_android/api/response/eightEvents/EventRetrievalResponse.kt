package com.phdlabs.sungwon.a8chat_android.api.response.eightEvents

import com.phdlabs.sungwon.a8chat_android.api.response.ErrorResponse
import com.phdlabs.sungwon.a8chat_android.model.room.Room

/**
 * Created by SungWon on 1/24/2018.
 * [EventRetrievalResponse]
 * Get all events in Array
 */
class EventRetrievalResponse : ErrorResponse() {
    val events: Array<Room>? = null
}