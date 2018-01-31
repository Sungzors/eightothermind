package com.phdlabs.sungwon.a8chat_android.api.data

/**
 * Created by SungWon on 1/5/2018.
 * Updated by JPAM on 01/30/2018
 */
class EventPostData(
        var mediaId: String?,
        var lat: String?,
        var lng: String?,
        var user_creator_id: Int?,
        var name: String?,
        var distribution: String?,
        val location_name: String?,
        val locks_after_24_hours: Boolean?
)