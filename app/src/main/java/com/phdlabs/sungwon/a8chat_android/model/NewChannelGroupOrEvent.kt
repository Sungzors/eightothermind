package com.phdlabs.sungwon.a8chat_android.model

/**
 * Created by SungWon on 12/8/2017.
 * Updated by JPAM on 26/01/2018
 */
//TODO: Create realm class to cache NewChannelGroupOrEvent information
data class NewChannelGroupOrEvent(
        val id: Int,
        val room_id: Int,
        val name: String,
        val user_creator_id: String,
        val createdAt: String,
        val profile_picture_string: String,
        val avatar: String
)