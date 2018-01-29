package com.phdlabs.sungwon.a8chat_android.model.room

import com.phdlabs.sungwon.a8chat_android.model.Message

/**
 * Created by SungWon on 11/27/2017.
 *
 * For subRoom nest in Room object
 */
data class SubRoomNest(
        val id: String,
        val message: Message
)