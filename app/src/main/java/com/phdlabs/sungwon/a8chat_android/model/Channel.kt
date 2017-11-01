package com.phdlabs.sungwon.a8chat_android.model

/**
 * Created by SungWon on 10/26/2017.
 *
 * Channel describes a sort of a mini-twitter bs in invision, NOT a chat room
 */
data class Channel(
        val id: Int,
        var name: String,
        val unique_id: String,
        val room_id: String
        ){
    var description: String? = null
    var color: String? = null
    var background: String? = null
    var add_to_profile: String? = null
    var user_creator_id: String? = null
}