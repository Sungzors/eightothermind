package com.phdlabs.sungwon.a8chat_android.db.room

import com.phdlabs.sungwon.a8chat_android.api.rest.Rest
import com.phdlabs.sungwon.a8chat_android.db.user.UserManager
import com.phdlabs.sungwon.a8chat_android.model.files.File
import com.phdlabs.sungwon.a8chat_android.model.message.Message
import com.phdlabs.sungwon.a8chat_android.model.room.Room
import com.phdlabs.sungwon.a8chat_android.model.user.UserRooms
import com.vicpin.krealmextensions.query
import com.vicpin.krealmextensions.queryFirst
import com.vicpin.krealmextensions.save
import com.vicpin.krealmextensions.saveAll
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
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

    private val disposable = CompositeDisposable()


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
                        disposable.add(call.subscribeOn(Schedulers.io())
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
                                    disposable.clear()
                                }, { throwable ->
                                    println(throwable.localizedMessage)
                                    println(throwable.stackTrace)
                                    callback(null)
                                }))
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
                        disposable.add(call.subscribeOn(Schedulers.io())
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
                                }))
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
                        disposable.add(call.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({ response ->
                                    if (response.isSuccess) {
                                        response?.room?.let {
                                            callback(Pair(it, null))
                                        }
                                    } else if (response.isError) {
                                        callback(Pair(null, "Room not found"))
                                    }
                                    disposable.clear()
                                }, { throwable ->
                                    callback(Pair(null, throwable.localizedMessage))
                                }))
                    }
                }
            }
        }
    }

    /**
     * [getRealmRoom]
     * - Pull Room from Realm
     */
    fun getRealmRoom(roomId: Int, callback: (Room?) -> Unit){
        Room().queryFirst { equalTo("id", roomId) }?.let {
            callback(it)
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
                        disposable.add(call.subscribeOn(Schedulers.io())
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
                                    disposable.clear()
                                }, { throwable ->
                                    callback(Pair(null, throwable.localizedMessage))
                                }))
                    }
                }
            }
        }
    }

    /**
     * [getPrivateAndGroupChats]
     * Retrieve private chats information & GroupChats information
     * Used for notifications access to designated Activity
     * */
    fun getPrivateAndGroupChats(callback: (Pair<List<Room>?, String?>) -> Unit) {
        UserManager.instance.getCurrentUser { success, user, token ->
            if (success) {
                user?.let {
                    token?.token?.let {
                        val call = Rest.getInstance().getmCallerRx().getPrivateAndGroupChats(it, user.id!!)
                        disposable.add(call.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({ response ->
                                    if (response.isSuccess) {
                                        response.chats?.let {
                                            if (it.count() > 0) {
                                                callback(Pair(it.toList(), null))
                                            }
                                        }
                                    } else if (response.isError) {
                                        callback(Pair(null, "Could not download private & group chats"))
                                    }
                                    disposable.clear()
                                }, { throwable ->
                                    callback(Pair(null, throwable.localizedMessage))
                                }))
                    }
                }
            }
        }
    }

    /**
     * [getPrivateChats]
     * Retrieve & Cache active private chats information
     * @callback Pair(Rooms, ErrorMessage)
     * WARNING -> NOT CURRENTLY USED - IT DOESN'T CONFORM TO [Room] Model
     * */
    fun getPrivateChats(callback: (Pair<List<Room>?, String?>) -> Unit) {
        UserManager.instance.getCurrentUser { success, user, token ->
            if (success) {
                user?.let {
                    token?.token?.let {
                        val call = Rest.getInstance().getmCallerRx().getPrivateChats(it, user.id!!)
                        disposable.add(call.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({ response ->
                                    if (response.isSuccess) {
                                        var rooms: MutableList<Room> = mutableListOf()
                                        //Unread & Favorites
                                        response.privateChats?.unreadAndFavorite?.let {
                                            rooms.addAll(it)
                                        }
                                        //Favorites
                                        response.privateChats?.favorite?.let {
                                            rooms.addAll(it)
                                        }
                                        //Unread
                                        response.privateChats?.unread?.let {
                                            rooms.addAll(it)
                                        }
                                        //Read
                                        response.privateChats?.read?.let {
                                            rooms.addAll(it)
                                        }
                                        callback(Pair(rooms, null))
                                    } else if (response.isError) {
                                        callback(Pair(null, "Could not download private chats"))
                                    }

                                    disposable.clear()
                                }, { throwable ->
                                    callback(Pair(null, throwable.localizedMessage))
                                }))
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

    /**
     * Find Private Chat [Room] based on participants
     * @param roomType
     * @param userId
     * @param contactId
     * @callback [Room]
     * */
    fun getRoomWithPrticipantsIds(userId: Int, contactId: Int, callback: (Room?) -> Unit) {
        val roomParticipants = mutableListOf<Int>()
        val room: Room? = null
        Room().query {
            equalTo("chatType", "private")
        }.let {
            //Get participants of each room
            for (room in it) {
                room.participantsId?.let {
                    for (participantId in it) {
                        participantId?.intValue?.let {
                            roomParticipants.add(it)
                        }
                    }
                }
                //Condition
                val privateChatUsers = listOf(userId, contactId)
                if (roomParticipants == privateChatUsers) {
                    //Return
                    callback(room)
                    continue
                }
            }
            callback(null)
        }
    }
}