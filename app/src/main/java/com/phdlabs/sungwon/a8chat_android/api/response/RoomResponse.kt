package com.phdlabs.sungwon.a8chat_android.api.response

import com.phdlabs.sungwon.a8chat_android.model.room.Room

/**
 * Created by SungWon on 10/30/2017.
 */
class RoomResponse: ErrorResponse() {
    internal var room: Room? = null
}