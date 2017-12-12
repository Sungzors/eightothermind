package com.phdlabs.sungwon.a8chat_android.api.response

import com.phdlabs.sungwon.a8chat_android.model.ChannelNest

/**
 * Created by SungWon on 11/30/2017.
 * "newChannelGroupOrEvent": {
"id": 0,
"roomId": 0,
"name": "string",
"adminId": "string",
"createdAt": "string",
"profile_picture_string": "string",
"avatar": "string"
}
 */
class ChannelResponse: ErrorResponse(){
//    internal var id: Int? = null
//    internal var name: String? = null
//    internal var unique_id: String? = null
//    internal var description: String? = null
//    internal var color: String? = null
//    internal var background: String? = null
//    internal var add_to_profile: Boolean? = null
//    internal var user_creator_id: String? = null
//    internal var room_id: String? = null
//    internal var profile_picture_string: String? = null
//    internal var avatar: String? = null
    internal var newChannelGroupOrEvent : ChannelNest? = null
}