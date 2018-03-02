package com.phdlabs.sungwon.a8chat_android.api.response.room

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.phdlabs.sungwon.a8chat_android.api.response.ErrorResponse
import com.phdlabs.sungwon.a8chat_android.model.user.UserRooms

/**
 * Created by paix on 2/28/18.
 * [EnterLeaveRoomResponse]
 * Maps the [UserRooms] the user has entered or left
 */
class EnterLeaveRoomResponse : ErrorResponse() {
    @SerializedName("userRoom")
    @Expose
    var userRoom: UserRooms? = null
}