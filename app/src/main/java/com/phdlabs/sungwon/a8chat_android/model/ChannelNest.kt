package com.phdlabs.sungwon.a8chat_android.model

/**
 * Created by SungWon on 12/8/2017.
 * "newChannelGroupOrEvent": {
"avatar": "https://s3.amazonaws.com/eight-testing123/1512593728207.jpeg",
"add_to_profile": true,
"id": 10,
"name": null,
"unique_id": null,
"description": null,
"color": null,
"background": null,
"user_creator_id": 23,
"room_id": 25,
"profile_picture_string": "1512593728207.jpeg",
"updatedAt": "2017-12-08T20:26:51.496Z",
"createdAt": "2017-12-08T20:26:51.496Z"
}
 */
data class ChannelNest(
        val id: String,
        val room_id: String,
        val name: String,
        val adminId: String,
        val createdAt: String,
        val profile_picture_string: String,
        val avatar: String,
        val user_creator_id: String
)