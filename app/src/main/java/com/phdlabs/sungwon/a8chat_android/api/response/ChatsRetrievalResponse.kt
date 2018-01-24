package com.phdlabs.sungwon.a8chat_android.api.response

import com.phdlabs.sungwon.a8chat_android.model.Room

/**
 * Created by SungWon on 1/24/2018.
 */
class ChatsRetrievalResponse: ErrorResponse(){
    val chats: Array<Room>? = null
}