package com.phdlabs.sungwon.a8chat_android.api.response.privateChat

import com.phdlabs.sungwon.a8chat_android.model.room.Room

/**
 * Created by paix on 3/25/18.
 * Classified Room information for easy access
 */
class ClassifiedPrivateChats {
    var unreadAndFavorite: Array<Room>? = null
    var unread: Array<Room>? = null
    var favorite: Array<Room>? = null
    var read: Array<Room>? = null
}