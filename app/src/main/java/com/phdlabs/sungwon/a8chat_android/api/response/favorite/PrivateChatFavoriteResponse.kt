package com.phdlabs.sungwon.a8chat_android.api.response.favorite

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.phdlabs.sungwon.a8chat_android.api.response.ErrorResponse
import com.phdlabs.sungwon.a8chat_android.model.room.Room
import com.phdlabs.sungwon.a8chat_android.model.user.UserRooms

/**
 * Created by JPAM on 2/22/18.
 * Updated favorite room returned by API
 */
class PrivateChatFavoriteResponse : ErrorResponse() {
    @SerializedName("room")
    @Expose
    var room: Room? = null

    @SerializedName("userRoom")
    @Expose
    var userRoom: UserRooms? = null
}