package com.phdlabs.sungwon.a8chat_android.model

/**
 * Created by SungWon on 11/21/2017.
 */
data class EventsEight(
        val id : String,
        var name: String,
        var user_creator_id: String
){
    var distribution: String? = null
    var locks_after_24_hours: Boolean = false
    var location: MutableList<String>? = null
    var room_id: String? = null
    var isRead: Boolean = true
}

