package com.phdlabs.sungwon.a8chat_android.model

import com.phdlabs.sungwon.a8chat_android.model.user.User

/**
 * Created by SungWon on 10/30/2017.
 * Room describes a chat room.
 */
//TODO: Create realm class to cache Rooms
data class Room(
        /*Constructor*/
        val id: Int,
        val channel: Boolean,
        val event: Boolean,
        val groupChat: Boolean,
        val privateChat: Boolean,
        val participantsId: MutableList<Int> = mutableListOf<Int>(),
        val locked: Boolean,
        val last_activity: String

) {
    /*Local Properties*/
    var isRead: Boolean = true
    var isFavorite: Boolean = false
    var chatType: String? = null
    var subRooms: SubRoomNest? = null
    var message: Message? = null
    var user: User? = null
}