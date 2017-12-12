package com.phdlabs.sungwon.a8chat_android.api.data

/**
 * Created by SungWon on 11/30/2017.
 */
data class PostChannelData(
        val mediaId: String,
        val name: String,
        val unique_id: String,
        val description: String,
//        val color: String?,
//        val background: String?,
        val add_to_profile: Boolean,
        val user_creator_id: String
)