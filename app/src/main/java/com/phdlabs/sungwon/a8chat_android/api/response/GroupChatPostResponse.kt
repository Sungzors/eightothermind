package com.phdlabs.sungwon.a8chat_android.api.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.phdlabs.sungwon.a8chat_android.model.room.GroupChatInfo
import com.phdlabs.sungwon.a8chat_android.model.room.Room

/**
 * Created by SungWon on 3/1/2018.
 */
class GroupChatPostResponse : ErrorResponse() {

    @SerializedName("room")
    @Expose
    var room: Room? = null

    @SerializedName("newChannelGroupOrEvent")
    @Expose
    var newChannelGroupOrEvent: GroupChatInfo? = null
}