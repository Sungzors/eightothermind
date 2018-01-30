package com.phdlabs.sungwon.a8chat_android.api.data

/**
 * Created by SungWon on 1/5/2018.
 * Todo: Posting model needs update
 */
class EventPostData(
        var mediaId: String?,
        var lat: String?,
        var lng: String?,
        var user_creator_id: String?,
        var name: String?,
        var distribution: MutableList<String>?,
        val location_name: String?,
        val locks_after_24_hours: Boolean?
)