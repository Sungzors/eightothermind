package com.phdlabs.sungwon.a8chat_android.structure.channel.mychannel

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.Toast
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.Socket
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.api.data.FavoriteData
import com.phdlabs.sungwon.a8chat_android.api.data.SendMessageStringData
import com.phdlabs.sungwon.a8chat_android.api.rest.Rest
import com.phdlabs.sungwon.a8chat_android.api.utility.GsonHolder
import com.phdlabs.sungwon.a8chat_android.db.channels.ChannelsManager
import com.phdlabs.sungwon.a8chat_android.db.room.RoomManager
import com.phdlabs.sungwon.a8chat_android.db.user.UserManager
import com.phdlabs.sungwon.a8chat_android.model.channel.Channel
import com.phdlabs.sungwon.a8chat_android.model.message.Message
import com.phdlabs.sungwon.a8chat_android.model.user.UserRooms
import com.phdlabs.sungwon.a8chat_android.structure.channel.ChannelContract
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import com.phdlabs.sungwon.a8chat_android.utility.SuffixDetector
import com.phdlabs.sungwon.a8chat_android.utility.camera.CameraControl
import com.vicpin.krealmextensions.delete
import com.vicpin.krealmextensions.save
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import org.slf4j.LoggerFactory
import java.io.ByteArrayOutputStream
import java.io.File

/**
 * Created by SungWon on 12/20/2017.
 * Updated by JPAM on 03/05/2018
 */
class MyChannelController(val mView: ChannelContract.MyChannel.View) : ChannelContract.MyChannel.Controller {

    /*Properties*/
    private var mSocket: Socket
    private var mUserId: Int? = null
    private val log = LoggerFactory.getLogger(MyChannelController::class.java)

    /*Current Room*/
    private var mRoomId: Int = 0
    private var mUserRoom: UserRooms? = null

    /*Messages*/
    private var mMessages = mutableListOf<Message>()
    private var mBroadcastMessage: Message? = null

    /*Flags*/
    private var mKeepSocketConnection: Boolean = false
    private var mIsSocketConnected: Boolean = false

    /*Initialize*/
    init {

        mView.controller = this

        mSocket = mView.get8Application.getSocket()

        UserManager.instance.getCurrentUser { success, user, _ ->
            if (success) {
                user?.id?.let {
                    mUserId = it
                }
            }
        }
    }

    /*LifeCycle*/
    override fun onCreate() {
        /*Room Alert*/
        mRoomId = mView.getRoomId
    }

    /*LifeCycle*/
    override fun start() {
        //Channel required permissions
        checkSelfPermissions()
    }

    override fun resume() {
        retrieveChatHistory(true)
        //Api -> Enter Room
        RoomManager.instance.enterRoom(mRoomId, { userRooms ->
            userRooms?.let {
                mUserRoom = it
            }
        })

        //Emmit socket room connectivity
        mUserId?.let {
            mSocket.emit(Constants.SocketKeys.CONNECT_ROOMS, it, Constants.SocketTypes.CHANNEL)
            //Socket io ON
            socketOn()
        }

    }


    override fun pause() {

    }

    override fun stop() {
    }

    override fun destroy() {
        //Socket io OFF
        socketOff()
        //Api -> Leave Room
        RoomManager.instance.leaveRoom(mRoomId, { userRooms ->
            userRooms?.let {
                mUserRoom = it
            }
        })
    }

    /*Channel Required permissions*/
    override fun checkSelfPermissions(): Boolean {
        return checkSelfPermission(Constants.AppPermissions.RECORD_AUDIO, Constants.PermissionsReqCode.RECORD_AUDIO_REQ_CODE) &&
                checkSelfPermission(Constants.AppPermissions.CAMERA, Constants.PermissionsReqCode.CAMERA_REQ_CODE) &&
                checkSelfPermission(Constants.AppPermissions.WRITE_EXTERNAL, Constants.PermissionsReqCode.WRITE_EXTERNAL_REQ_CODE)
    }

    fun checkSelfPermission(permission: String, requestCode: Int): Boolean {
        if (ContextCompat.checkSelfPermission(mView.getActivity,
                        permission) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(mView.getActivity,
                    arrayOf(permission),
                    requestCode)
            return false
        }
        //Video broadcasting ready with camera permission
        if (Constants.AppPermissions.CAMERA == permission) {
             mView.get8Application.initWorkerThread() //TODO: Uncomment to test Video Broadcasting (Only works on device)
        }
        return true
    }

    override fun requestReadingExternalStorage() {
        //Required permissions for file sharing
        val whatPermissions = arrayOf(Constants.AppPermissions.READ_EXTERNAL)
        mView.getActivity.context?.let {
            //Request Permissions
            if (ContextCompat.checkSelfPermission(it, whatPermissions.get(0)) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(mView.getActivity, whatPermissions, Constants.PermissionsReqCode.READ_EXTERNAL_STORAGE)
            }
        }
    }

    override fun permissionResults(requestCode: Int, permissions: Array<out String>, grantResults: IntArray): Boolean {

        when (requestCode) {
        /*Audio*/
            Constants.PermissionsReqCode.RECORD_AUDIO_REQ_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkSelfPermission(Constants.AppPermissions.CAMERA, Constants.PermissionsReqCode.CAMERA_REQ_CODE)
                } else {
                    mView.getActivity.finish()
                }
                return true
            }
        /*Camera*/
            Constants.PermissionsReqCode.CAMERA_REQ_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkSelfPermission(Constants.AppPermissions.WRITE_EXTERNAL, Constants.PermissionsReqCode.WRITE_EXTERNAL_REQ_CODE)
                    mView.get8Application.initWorkerThread()//TODO: Uncomment when testing broadcast on device
                } else {
                    mView.getActivity.finish()
                }
                return true
            }
        /*Write External Storage*/
            Constants.PermissionsReqCode.WRITE_EXTERNAL_REQ_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkSelfPermission(Constants.AppPermissions.READ_EXTERNAL, Constants.PermissionsReqCode.READ_EXTERNAL_STORAGE)
                } else {
                    mView.getActivity.finish()
                }
                return true
            }
        /*Read External Storage*/
            Constants.PermissionsReqCode.READ_EXTERNAL_STORAGE -> {
                if (grantResults.size != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    mView.showError(mView.getActivity.getString(R.string.request_read_external_permission))
                }
                return true
            }
        }
        return false
    }

    /*String Messages posted from Drawer*/
    override fun sendMessage() {
        //TODO: Make a Manager for sending message
        UserManager.instance.getCurrentUser { success, user, token ->
            if (success) {
                user?.let {
                    token?.token?.let {
                        if (!mView.getMessageET.isBlank()) {
                            //Create String-Post without media
                            val call = Rest.getInstance().getmCallerRx().postChannelMessagePost(
                                    it,
                                    SendMessageStringData(user.id!!, mView.getMessageET, mView.getRoomId),
                                    false
                            )
                            call.subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({ response ->
                                        if (response.isSuccess) {
                                            println("Message: " + response.messageInfo?.message)
                                        } else if (response.isError) {
                                            mView.showError("Could not post")
                                        }
                                    }, { throwable ->
                                        mView.showError(throwable.localizedMessage)
                                    })
                        } else {
                            mView.showError("Try typing something")
                        }
                    }
                }
            }
        }
    }

    /*Send File*/
    override fun sendFile(file: File, path: String) {
        //TODO: Make a manager for sending files
        UserManager.instance.getCurrentUser { success, user, token ->
            if (success) {
                user?.let {
                    token?.token?.let {
                        //Single file upload only supported
                        val formBodyBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)
                                .addFormDataPart("userId", user.id!!.toString())
                                .addFormDataPart("roomId", mView.getRoomId.toString())
                                .addFormDataPart("file[0]",
                                        "8_" + System.currentTimeMillis() + SuffixDetector.instance.getFileSuffix(path),
                                        RequestBody.create(MediaType.parse("application/*"), file))
                                .build()

                        val call = Rest.getInstance().getmCallerRx().shareFile(
                                it, formBodyBuilder, null, false, null, null)
                        call.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({ response ->
                                    if (response.isSuccess) {
                                        response?.newlyCreatedMsg?.let {
                                            println("File Sent: " + it)
                                        }
                                    } else {
                                        mView.showError("Can't upload file at this time")
                                    }
                                }, { throwable ->
                                    mView.showError(throwable.localizedMessage)
                                })
                    }
                }
            }
        }
    }

    /*SOCKETS*/
    private fun socketOn() {
        //Socket
        if (!mIsSocketConnected) {
            //Room
            mSocket.on(Constants.SocketKeys.UPDATE_ROOM, onUpdateRoom)
            //Messaging
            mSocket.on(Constants.SocketKeys.UPDATE_CHAT_STRING, onNewMessage)
            mSocket.on(Constants.SocketKeys.UPDATE_CHAT_CHANNEL, onNewMessage)
            mSocket.on(Constants.SocketKeys.UPDATE_CHAT_CONTACT, onNewMessage)
            mSocket.on(Constants.SocketKeys.UPDATE_CHAT_MEDIA, onNewMessage)
            mSocket.on(Constants.SocketKeys.UPDATE_CHAT_FILE, onNewMessage)
            mSocket.on(Constants.SocketKeys.UPDATE_CHAT_BROADCAST, onNewMessage)
            mSocket.on(Constants.SocketKeys.COMMENT, onNewMessage)
            //Deleted Message
            mSocket.on(Constants.SocketKeys.DELETE_MESSAGE, onDeleteMessage)
            //Error
            mSocket.on(Constants.SocketKeys.ON_ERROR, onError)
            //Socket connectivity
            mIsSocketConnected = true
        }
    }

    private fun socketOff() {
        if (mKeepSocketConnection) {
            //Room
            mSocket.off(Constants.SocketKeys.UPDATE_ROOM)
            //Messaging
            mSocket.off(Constants.SocketKeys.UPDATE_CHAT_STRING)
            mSocket.off(Constants.SocketKeys.UPDATE_CHAT_CHANNEL)
            mSocket.off(Constants.SocketKeys.UPDATE_CHAT_CONTACT)
            mSocket.off(Constants.SocketKeys.UPDATE_CHAT_MEDIA)
            mSocket.off(Constants.SocketKeys.UPDATE_CHAT_FILE)
            mSocket.off(Constants.SocketKeys.UPDATE_CHAT_BROADCAST)
            mSocket.off(Constants.SocketKeys.COMMENT)
            //Deleted Message
            mSocket.off(Constants.SocketKeys.DELETE_MESSAGE)
            //Error
            mSocket.off(Constants.SocketKeys.ON_ERROR)
            //Socket connectivity
            mIsSocketConnected = false
        }
    }

    override fun keepSocketConnection(keepConnection: Boolean) {
        mKeepSocketConnection = keepConnection
    }

    //TODO: Refine users leaving and entering the room -> It gets hit about 9 times... ask Tomer
    private val onUpdateRoom = Emitter.Listener { args ->
        mView.getActivity.runOnUiThread({
            val data: JSONObject = args[0] as JSONObject
            var userAdded: String? = null
            var userLeaving: String? = null
            try {
                userAdded = data.getString("userAdded")
                userLeaving = data.getString("userLeaving")
            } catch (e: JSONException) {
                //e.printStackTrace()
                log.debug("onUpdateRoom exception ${e.localizedMessage}")
            }
            if (userAdded != null) {
                //TODO: Action required on user added
            }
            if (userLeaving != null) {
                //TODO: Action required on user leaving
            }
        })
    }

    private val onNewMessage = Emitter.Listener { args ->
        mView.getActivity.runOnUiThread({
            val data: JSONObject = args[0] as JSONObject
            val message = GsonHolder.Companion.instance.get()!!.fromJson(data.toString(), Message::class.java)
            //Verify Room
            if (message.roomId == mRoomId) {
                if (mMessages.count() > 0) {
                    //Check for duplicates
                    if (mMessages[0].id != message.id) {
                        //Add message to Channel Feed
                        mMessages.add(0, message)
                        //Current Broadcast message INSTANCE
                        if (message.type == Constants.MessageTypes.BROADCAST) {
                            mBroadcastMessage = message
                        }
                    }
                }
            }
            mView.updateContentRecycler()
        })
    }

    private val onDeleteMessage = Emitter.Listener { args ->
        mView.getActivity.runOnUiThread({
            val data: JSONObject = args[0] as JSONObject
            val message = GsonHolder.Companion.instance.get()!!.fromJson(data.toString(), Message::class.java)
            //Verify Room
            if (message.roomId == mRoomId) {
                //Check for duplicates
                if (mMessages[0].id == message.id) {
                    //Add message to channel feed
                    mMessages.removeAt(0)
                    //Current Broadcast message INSTANCE deletion & Realm deletion
                    Message().delete { equalTo("id", mBroadcastMessage?.id) }
                    mBroadcastMessage = null
                    mView.updateContentRecycler()
                }
                //TODO: Else remove the message with selected ID
            }
        })
    }

    override fun deleteMessage(messageId: Int) {
        UserManager.instance.getCurrentUser { success, user, token ->
            if (success) {
                user?.let {
                    token?.token?.let {
                        val call = Rest.getInstance().getmCallerRx().deleteMessagio(it, messageId, user.id!!)
                        call.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({ response ->
                                    if (response.isSuccess) {
                                        log.debug("Message successfully deleted in channel API, messageId: $messageId")
                                    } else if (response.isError) {
                                        log.debug("Error deleting message in channel API, messageId: $messageId")
                                    }
                                }, { throwable ->
                                    log.debug("Error deleting message from API, messageId: $messageId")
                                    mView.showError(throwable.localizedMessage)
                                })
                    }
                }
            }
        }
    }

    private val onError = Emitter.Listener { args ->
        mView.getActivity.runOnUiThread {
            //Show Error if it's not empty
            if (!args[0].toString().equals("{}")) {
                mView.showError(args[0].toString())
            }
        }
    }

    /*Media*/
    override fun onPictureOnlyResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_CANCELED) {
            //TODO: Make a manager for sending media
            UserManager.instance.getCurrentUser { isSuccess, user, token ->
                if (isSuccess) {
                    user?.let {
                        token?.token?.let {
                            val formBodyBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)
                                    .addFormDataPart("userId", user.id!!.toString())
                                    .addFormDataPart("roomId", mView.getRoomId.toString())
                            //Create Media-Post BodyPart & Call
                            val imageBitmap = CameraControl.instance.getImageFromResult(mView.getActivity, requestCode, resultCode, data)
                            imageBitmap?.let {
                                val bos = ByteArrayOutputStream()
                                it.compress(Bitmap.CompressFormat.PNG, 0, bos)
                                val bitmapData = bos.toByteArray()
                                formBodyBuilder.addFormDataPart("file[0]",
                                        "8_" + System.currentTimeMillis(),
                                        RequestBody.create(MediaType.parse("image/*"), bitmapData))
                            }
                            val formBody = formBodyBuilder.build()
                            val call = Rest.getInstance().getmCallerRx().postChannelMediaPost(token.token!!, formBody, false)
                            call.subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({ response ->
                                        if (response.isSuccess) {
                                            println("Message: " + response.messageInfo?.message)
                                        } else if (response.isError) {
                                            mView.showError("Could not post")
                                        }
                                    }, { throwable ->
                                        mView.showError(throwable.localizedMessage)
                                    })

                        }
                    }
                }
            }
        }
    }

    /*Channels*/
    override fun getFollowedChannels(): MutableList<Channel>? {
        val channels = ChannelsManager.instance.queryFollowedChannels()?.toMutableList()
        channels?.let {
            //Filter the current channel from the favorites shown at the top Recycler View
            channels
                    .filter { it.id == mView.getChannelId }
                    .forEach { channels.remove(it) }
        }
        return channels
    }

    /*Chat History*/
    override fun retrieveChatHistory(refresh: Boolean) {
        mView.showProgress()
        ChannelsManager.instance.getChannelPosts(refresh, mRoomId, null, { channelPosts ->
            channelPosts.second?.let {
                mView.showError(it)
                mView.hideProgress()
            } ?: run {
                channelPosts.first?.let {
                    if (it.count() > 0) {
                        mMessages.clear()
                        it.filter { it.roomId == mRoomId }.forEach { mMessages.add(0, it) }
                        mView.updateContentRecycler()
                    }
                }
                mView.hideProgress()
            }
        })
    }

    override fun getMessages(): MutableList<Message> = mMessages

    /*POST*/
    /*Create Post*/
    //TODO: Remove post from channel controller
    override fun createPost(message: String?, filePaths: ArrayList<String>?) {
        UserManager.instance.getCurrentUser { isSuccess, user, token ->
            if (isSuccess) {
                user?.let {
                    token?.token?.let {
                        val formBodyBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)
                                .addFormDataPart("message", message ?: "")
                                .addFormDataPart("userId", user.id!!.toString())
                                .addFormDataPart("roomId", mView.getRoomId.toString())
                        //Create Media-Post BodyPart & Call
                        filePaths?.let {
                            if (it.count() > 0) {
                                for (mediaFile in it) {
                                    val imageBitmap = MediaStore.Images.Media.getBitmap(mView.getActivity.contentResolver, Uri.parse(mediaFile))
                                    imageBitmap?.let {
                                        val bos = ByteArrayOutputStream()
                                        it.compress(Bitmap.CompressFormat.PNG, 0, bos)
                                        val bitmapData = bos.toByteArray()
                                        formBodyBuilder.addFormDataPart("file[${filePaths.indexOf(mediaFile)}]",
                                                "8_" + System.currentTimeMillis(),
                                                RequestBody.create(MediaType.parse("image/*"), bitmapData))
                                    }
                                }
                                val formBody = formBodyBuilder.build()
                                val call = Rest.getInstance().getmCallerRx().postChannelMediaPost(token.token!!, formBody, true)
                                call.subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe({ response ->
                                            if (response.isSuccess) {
                                                println("Message: " + response.messageInfo?.message)
                                                retrieveChatHistory(true)
                                            } else if (response.isError) {
                                                mView.showError("Could not post")
                                            }
                                        }, { throwable ->
                                            mView.showError(throwable.localizedMessage)
                                        })
                            } else {
                                //Create String-Message Post
                                val call = Rest.getInstance().getmCallerRx().postChannelMessagePost(
                                        token.token!!,
                                        SendMessageStringData(user.id!!, message, mView.getRoomId),
                                        true
                                )
                                call.subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe({ response ->
                                            if (response.isSuccess) {
                                                println("Message: " + response.messageInfo?.message)
                                                retrieveChatHistory(true)
                                            } else if (response.isError) {
                                                mView.showError("Could not post")
                                            }
                                        }, { throwable ->
                                            mView.showError(throwable.localizedMessage)
                                        })
                            }
                        }
                    }
                }
            }
        }
    }

    //Like
    override fun likePost(messageId: Int, unlike: Boolean) {
        ChannelsManager.instance.likeUnlikePost(messageId, unlike)
    }

    override fun favoriteMessage(message: Message, userId: Int, position: Int) {

        UserManager.instance.getCurrentUser{ success, _, token ->
            if (success) {
                val call = Rest.getInstance().getmCallerRx().favoriteMessagio(token?.token!!, message.id!!, FavoriteData(userId), message.isFavorited)
                call.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                {response ->
                                    if(response.isSuccess){
                                        message.isFavorited = true
                                        mMessages.set(position, message)
                                        Toast.makeText(mView.getContext(), "Message Favorited!", Toast.LENGTH_SHORT).show()
                                    } else if (response.isError){
                                        mView.hideProgress()
                                        mView.showError("Unable to favorite, try again later")
                                    }

                                }, { throwable ->
                                    mView.hideProgress()
                                    println("Error creating channel: " + throwable.message)
                                    mView.showError("Unable to favorite, try again later")
                                })
            }
        }
    }
    //Broadcast Owner
    override fun startBroadcast(roomId: Int) {
        ChannelsManager.instance.startBroadcast(roomId, {
            it.second?.let {
                //Error
                mView.showError(it)
            } ?: run {
                it.first?.let {
                    log.debug("Start Broadcast with messageId: ${it.id}")
                    it.save() //Save to realm
                    mView.goToBroadcastActivity(it.id, io.agora.rtc.Constants.CLIENT_ROLE_BROADCASTER)
                    //TODO: Test Disposables
                }
            }
        })
    }

    override fun endBroadcast(roomId: Int, messageId: Int) {
        ChannelsManager.instance.finishBroadcast(roomId, messageId, {
            it.second?.let {
                //Error
                mView.showError(it)
            } ?: run {
                it.first?.let {
                    println("Finish Broadcast MessageId: ${it.id}")
                    /**
                     * Delete message [deleteMessage] from api so [onDeleteMessage]
                     * gets called & deletes the broadcast access from the feed
                     * */
                    deleteMessage(messageId)
                    //TODO: Test Disposables
                }
            }
        })
    }

    override var getBroadcastMessage: Message? = mBroadcastMessage
    override var getUserId: Int? = mUserId

}