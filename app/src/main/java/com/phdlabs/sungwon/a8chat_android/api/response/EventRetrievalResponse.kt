package com.phdlabs.sungwon.a8chat_android.api.response

import com.phdlabs.sungwon.a8chat_android.model.EventsEight

/**
 * Created by SungWon on 1/24/2018.
 */
class EventRetrievalResponse: ErrorResponse(){
    val events: Array<EventsEight>? = null
}