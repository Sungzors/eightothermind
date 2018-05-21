package com.phdlabs.sungwon.a8chat_android.db.room

import com.phdlabs.sungwon.a8chat_android.api.rest.Rest
import com.phdlabs.sungwon.a8chat_android.db.user.UserManager
import com.phdlabs.sungwon.a8chat_android.model.files.File
import com.phdlabs.sungwon.a8chat_android.model.message.Message
import com.phdlabs.sungwon.a8chat_android.model.room.Room
import com.phdlabs.sungwon.a8chat_android.model.user.UserRooms
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import com.vicpin.krealmextensions.query
import com.vicpin.krealmextensions.queryFirst
import com.vicpin.krealmextensions.save
import com.vicpin.krealmextensions.saveAll
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.realm.RealmQuery

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


    /*Properties*/
    private var mUserManager: UserManager
    val disposables: MutableList<Disposable> = mutableListOf()

    init {
        mUserManager = UserManager.instance
    }

    /**
     * [clearDisposables]
     * Release API RX Call resources for memory management
     * */
    fun clearDisposables() {
        for (disposable in disposables) {
            if (!disposable.isDisposed) {
                disposable.dispose()
            }
        }
        disposables.clear()
    }

    /**
     * [enterRoom]
     * Alerts API the room the user entered
     * */
    fun enterRoom(roomId: Int, callback: (UserRooms?) -> Unit) {
        mUserManager.getCurrentUser { isSuccess, user, token ->
            if (isSuccess) {
                user?.let {
                    token?.token?.let {
                        val call = Rest.getInstance().getmCallerRx().enterRoom(it, user.id!!, roomId)
                        disposables.add(call.subscribeOn(Schedulers.io())
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
                                }))
                    }
                }
            }
        }
        mUserManager.clearDisposables()
    }

    /**
     * [leaveRoom]
     * Alerts API the room the user left
     * */
    fun leaveRoom(roomId: Int, callback: (UserRooms?) -> Unit) {
        mUserManager.getCurrentUser { isSuccess, user, token ->
            if (isSuccess) {
                user?.let {
                    token?.token?.let {
                        val call = Rest.getInstance().getmCallerRx().leaveRoom(it, user.id!!, roomId)
                        disposables.add(call.subscribeOn(Schedulers.io())
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
        mUserManager.clearDisposables()
    }

    fun toggleNotification(roomId: Int, notif: Boolean, callback: (String?) -> Unit) {
        mUserManager.getCurrentUser { success, user, token ->
            if (success) {
                user?.let {
                    token?.token?.let {
                        val call = Rest.getInstance().getmCallerRx().toggleNotification(it, roomId, user.id!!, notif)
                        disposables.add(call.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({ response ->
                                    if (response.isSuccess) {
                                        callback(null)
                                    } else if (response.isError) {
                                        callback("Notification Toggle Unsuccessful")
                                    }
                                }, {
                                    callback(it.localizedMessage)
                                }))
                    }
                }
            }
        }
        mUserManager.clearDisposables()
    }

    /**
     * [getRoomInfo]
     * - Used to pull room information
     * */
    fun getRoomInfo(roomId: Int, callback: (Pair<Room?, String?>) -> Unit) {
        mUserManager.getCurrentUser { success, user, token ->
            if (success) {
                user?.let {
                    token?.token?.let {
                        val call = Rest.getInstance().getmCallerRx().getRoomById(it, roomId)
                        disposables.add(call.subscribeOn(Schedulers.io())
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
                                }))
                    }
                }
            }
        }
        mUserManager.clearDisposables()
    }

    /**
     * [getRealmRoom]
     * - Pull Room from Realm
     */
    fun getRealmRoom(roomId: Int, callback: (Room?) -> Unit) {
        Room().queryFirst { equalTo("id", roomId) }?.let {
            callback(it)
        }
    }

    /**
     * [getRoomMessageHistory]
     * - Used to pull messages from a Room -> Currently used for Private Chats
     * */
    fun getRoomMessageHistory(roomId: Int, callback: (Pair<List<Message>?, String?>) -> Unit) {
        mUserManager.getCurrentUser { success, user, token ->
            if (success) {
                user?.let {
                    token?.token?.let {
                        val call = Rest.getInstance().getmCallerRx().getChatHistory(it, roomId, user.id!!)
                        disposables.add(call.subscribeOn(Schedulers.io())
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
                                }))
                    }
                }
            }
        }
        mUserManager.clearDisposables()
    }

    /**
     * [getPrivateAndGroupChats]
     * Retrieve private chats information & GroupChats information
     * Used for notifications access to designated Activity
     * */
    fun getPrivateAndGroupChats(refresh: Boolean, callback: (Pair<List<Room>?, String?>) -> Unit) {
        mUserManager.getCurrentUser { success, user, token ->
            if (success) {
                user?.let {
                    token?.token?.let {
                        if (refresh) {
                            val call = Rest.getInstance().getmCallerRx().getPrivateAndGroupChats(it, user.id!!)
                            disposables.add(call.subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({ response ->
                                        if (response.isSuccess) {
                                            response.chats?.let {
                                                if (it.count() > 0) {
                                                    callback(Pair(it.toList(), null))
                                                    it.saveAll()
                                                }
                                            }
                                        } else if (response.isError) {
                                            callback(Pair(null, "Could not download private & group chats"))
                                        }
                                    }, { throwable ->
                                        callback(Pair(null, throwable.localizedMessage))
                                    }))
                        } else {
                            callback(Pair(getPrivateAndGroupChats(), null))
                        }
                    }
                }
            }
        }
        mUserManager.clearDisposables()
    }

    /**
     * [getPrivateChats]
     * Retrieve & Cache active private chats information
     * @callback Pair(Rooms, ErrorMessage)
     * WARNING -> NOT CURRENTLY USED - IT DOESN'T CONFORM TO [Room] Model
     * */
    fun getPrivateChats(refresh: Boolean, callback: (Pair<List<Room>?, String?>) -> Unit) {
        mUserManager.getCurrentUser { success, user, token ->
            if (success) {
                user?.let {
                    token?.token?.let {
                        if (refresh) {
                            val call = Rest.getInstance().getmCallerRx().getPrivateChats(it, user.id!!)
                            disposables.add(call.subscribeOn(Schedulers.io())
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
                                            //Cache chats
                                            rooms.saveAll()
                                            //Callback
                                            callback(Pair(rooms, null))
                                        } else if (response.isError) {
                                            callback(Pair(null, "Could not download private chats"))
                                        }

                                    }, { throwable ->
                                        callback(Pair(null, throwable.localizedMessage))
                                    }))
                        } else {
                            callback(Pair(getPrivateAndGroupChats(), null))
                        }
                    }
                }
            }
        }
        mUserManager.clearDisposables()
    }


    /*Queries*/


    /**
     * [getPrivateAndGroupChats]
     * @return List of [Room]
     * */
    private fun getPrivateAndGroupChats(): List<Room>? {
        val rooms = mutableListOf<Room>()
        rooms.addAll(Room().query {
            equalTo("chatType", Constants.ChatTypes.PRIVATE)
        })
        rooms.addAll(Room().query {
            equalTo("chatType", Constants.ChatTypes.GROUP)
        })
        return rooms
    }

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