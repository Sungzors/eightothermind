package com.phdlabs.sungwon.a8chat_android.model

/**
 * Created by SungWon on 10/30/2017.
 *
 * Room describes a chat room.
 */
data class Room(

        val id: Int,
        val channel: Boolean,
        val event: Boolean,
        val groupChat: Boolean,
        val privateChat: Boolean,
        val participantsId: MutableList<Int> = mutableListOf<Int>(),
        val locked: Boolean

                ) {
    var isRead: Boolean = true
    var isFavorite: Boolean = false
    var subRooms: SubRoomNest? = null
    var users: User? = null
}