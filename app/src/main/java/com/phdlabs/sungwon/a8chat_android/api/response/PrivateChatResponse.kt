package com.phdlabs.sungwon.a8chat_android.api.response

import com.phdlabs.sungwon.a8chat_android.model.room.Room

/**
 * Created by SungWon on 11/28/2017.
 */
class PrivateChatResponse: ErrorResponse(){
    internal val unreadAndFavorite: Array<Room>? = null
    internal val unread: Array<Room>? = null
    internal val favorite: Array<Room>? = null
    internal val readAndNonFavorite: Array<Room>? = null
}