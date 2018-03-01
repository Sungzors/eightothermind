package com.phdlabs.sungwon.a8chat_android.db.room

import com.phdlabs.sungwon.a8chat_android.db.user.UserManager

/**
 * Created by paix on 2/28/18.
 * [RoomManager]
 * Used for alerting entering or leaving a room
 * It is also used for CRUD with [Room]s
 */
class RoomManager {

    /*Singleton*/
    private object Holder {
        val instance = RoomManager()
    }

    /*Companion*/
    companion object {
        val instance by lazy { Holder.instance }
    }


    /**
     * [enterRoom]
     * Alerts API the room the user entered
     * */
    fun enterRoom(roomId: Int?) {
        UserManager.instance.getCurrentUser { isSuccess, user, token ->
            if (isSuccess) {
                user?.let {
                    token?.token?.let {

                    }
                }
            }
        }
    }

    /**
     * [leaveRoom]
     * Alerts API the room the user left
     * */
    fun leaveRoom(roomId: Int?) {
        UserManager.instance.getCurrentUser { isSuccess, user, token ->
            if (isSuccess) {
                user?.let {
                    token?.token?.let {}
                }
            }
        }

    }
}