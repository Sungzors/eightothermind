package com.phdlabs.sungwon.a8chat_android.db.room

import com.phdlabs.sungwon.a8chat_android.api.rest.Rest
import com.phdlabs.sungwon.a8chat_android.db.user.UserManager
import com.phdlabs.sungwon.a8chat_android.model.user.UserRooms
import com.vicpin.krealmextensions.query
import com.vicpin.krealmextensions.save
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

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
    fun enterRoom(roomId: Int, callback: (UserRooms?) -> Unit) {
        UserManager.instance.getCurrentUser { isSuccess, user, token ->
            if (isSuccess) {
                user?.let {
                    token?.token?.let {
                        val call = Rest.getInstance().getmCallerRx().enterRoom(it, user.id!!, roomId)
                        call.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({ response ->
                                    if (response.isSuccess) {
                                        response?.let {
                                            it.userRoom?.save()
                                            callback(getCurrentEnteredUserRoom(roomId))
                                        }
                                    } else if (response.isError) {
                                        println("Could not update user entering Room")
                                    }
                                }, { throwable ->
                                    println(throwable.localizedMessage)
                                    println(throwable.stackTrace)
                                    callback(null)
                                })
                    }
                }
            }
        }
    }

    /**
     * [leaveRoom]
     * Alerts API the room the user left
     * */
    fun leaveRoom(roomId: Int, callback: (UserRooms?) -> Unit) {
        UserManager.instance.getCurrentUser { isSuccess, user, token ->
            if (isSuccess) {
                user?.let {
                    token?.token?.let {
                        val call = Rest.getInstance().getmCallerRx().leaveRoom(it, user.id!!, roomId)
                        call.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({ response ->
                                    if (response.isSuccess) {
                                        response?.let {
                                            it.userRoom?.save()
                                            callback(getCurrentLeftUserRoom(roomId))
                                        }
                                    } else if (response.isError) {
                                        println("Could not update user leaving Room")
                                        callback(null)
                                    }
                                }, { throwable ->
                                    println(throwable.localizedMessage)
                                    println(throwable.stackTrace)
                                    callback(null)
                                })
                    }
                }
            }
        }

    }

    /*Queries*/

    /**
     * [getCurrentEnteredUserRoom]
     * @return [UserRooms] object
     * */
    fun getCurrentEnteredUserRoom(roomId: Int): UserRooms? {
        val userRooms = UserRooms().query {
            equalTo("roomId", roomId)
            equalTo("current_room", true)
        }
        if (userRooms.count() > 0) {
            return userRooms[0]
        }
        return null
    }

    /**
     * [getCurrentLeftUserRoom]
     * @return [UserRooms] object
     * */
    fun getCurrentLeftUserRoom(roomId: Int): UserRooms? {
        val userRooms = UserRooms().query {
            equalTo("roomId", roomId)
            equalTo("current_room", false)
        }
        if (userRooms.count() > 0) {
            return userRooms[0]
        }
        return null
    }


}