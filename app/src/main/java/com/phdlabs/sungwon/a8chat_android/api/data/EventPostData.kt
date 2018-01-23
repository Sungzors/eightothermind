package com.phdlabs.sungwon.a8chat_android.api.data

/**
 * Created by SungWon on 1/5/2018.
 */
class EventPostData(
        val mediaId: String?,
        val location_name: String?,
        val user_creator_id: String,
        val name: String,
        //TODO: friend of friends
        val locks_after_24_hours: Boolean
)