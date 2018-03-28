package com.phdlabs.sungwon.a8chat_android.db.room

import com.phdlabs.sungwon.a8chat_android.api.rest.Rest
import com.phdlabs.sungwon.a8chat_android.db.user.UserManager
import com.phdlabs.sungwon.a8chat_android.model.files.File
import com.phdlabs.sungwon.a8chat_android.model.message.Message
import com.phdlabs.sungwon.a8chat_android.model.room.Room
import com.phdlabs.sungwon.a8chat_android.model.user.UserRooms
import com.vicpin.krealmextensions.query
import com.vicpin.krealmextensions.save
import com.vicpin.krealmextensions.saveAll
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by JPAM on 2/28/18.
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

    /**
     * [getRoomInfo]
     * - Used to pull room information
     * */
    fun getRoomInfo(roomId: Int, callback: (Pair<Room?, String?>) -> Unit) {
        UserManager.instance.getCurrentUser { success, user, token ->
            if (success) {
                user?.let {
                    token?.token?.let {
                        val call = Rest.getInstance().getmCallerRx().getRoomById(it, roomId)
                        call.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({ response ->
                                    if (response.isSuccess) {
                                        response?.room?.let {
                                            callback(Pair(it, null))
                                        }
                                    } else if (response.isError) {
                                        callback(Pair(null, "Room not found"))
                                    }
                                }, { throwable ->
                                    callback(Pair(null, throwable.localizedMessage))
                                })
                    }
                }
            }
        }
    }

    /**
     * [getRoomMessageHistory]
     * - Used to pull messages from a Room -> Currently used for Private Chats
     * */
    fun getRoomMessageHistory(roomId: Int, callback: (Pair<List<Message>?, String?>) -> Unit) {
        UserManager.instance.getCurrentUser { success, user, token ->
            if (success) {
                user?.let {
                    token?.token?.let {
                        val call = Rest.getInstance().getmCallerRx().getChatHistory(it, roomId, user.id!!)
                        call.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({ response ->
                                    if (response.isSuccess) {
                                        response?.messages?.allMessages?.let {
                                            it.saveAll()
                                            callback(Pair(it.toList(), null))
                                        }
                                    } else if (response.isError) {
                                        callback(Pair(null, "Could not retrieve Chat history"))
                                    }
                                }, { throwable ->
                                    callback(Pair(null, throwable.localizedMessage))
                                })
                    }
                }
            }
        }
    }

    fun getRoomMessageHistory(roomId: Int, callback: (Pair<List<Message>?, String?>) -> Unit, msgId: Int) {
        UserManager.instance.getCurrentUser { success, user, token ->
            if (success) {
                user?.let {
                    token?.token?.let {
                        val call = Rest.getInstance().getmCallerRx().getChatHistory(it, roomId, user.id!!, msgId)
                        call.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({ response ->
                                    if (response.isSuccess) {
                                        response?.messages?.allMessages?.let {
                                            it.saveAll()
                                            callback(Pair(it.toList(), null))
                                        }
                                    } else if (response.isError) {
                                        callback(Pair(null, "Could not retrieve Chat history"))
                                    }
                                }, { throwable ->
                                    callback(Pair(null, throwable.localizedMessage))
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
    private fun getCurrentEnteredUserRoom(roomId: Int): UserRooms? {
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
    private fun getCurrentLeftUserRoom(roomId: Int): UserRooms? {
        val userRooms = UserRooms().query {
            equalTo("roomId", roomId)
            equalTo("current_room", false)
        }
        if (userRooms.count() > 0) {
            return userRooms[0]
        }
        return null
    }

    /**
     * [getFilesFromPrivateChat]
     * Retrieves a File List from all messages cached in a Private Chat thread
     * */
    fun getFilesFromPrivateChat(roomId: Int): List<File>? {
        var fileList = mutableListOf<File>()
        var messages = Message().query {
            equalTo("roomId", roomId)
        }
        for (message in messages) {
            message.files?.let {
                if (it.count() > 0) {
                    fileList.addAll(it)
                }
            }
        }
        return fileList
    }
}