package com.phdlabs.sungwon.a8chat_android.structure.channel.mychannel

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.Socket
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
import com.phdlabs.sungwon.a8chat_android.utility.camera.CameraControl
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream

/**
 * Created by SungWon on 12/20/2017.
 * Updated by JPAM on 03/05/2018
 */
class MyChannelController(val mView: ChannelContract.MyChannel.View) : ChannelContract.MyChannel.Controller {

    private var TAG = "MyChannelController"

    /*Properties*/
    private var mSocket: Socket

    /*Current Room*/
    private var mRoomId: Int = 0
    private var mUserRoom: UserRooms? = null

    /*Messages*/
    private var mMessages = mutableListOf<Message>()

    /*Flags*/
    private var mKeepSocketConnection: Boolean = false
    private var mIsSocketConnected: Boolean = false

    /*Initialize*/
    init {
        mView.controller = this
        mSocket = mView.get8Application.getSocket()
    }

    /*LifeCycle*/
    override fun onCreate() {
        /*Room Alert*/
        mRoomId = mView.getRoomId
        //Message History (API)
        retrieveChatHistory()
    }

    /*LifeCycle*/
    override fun start() {
    }

    override fun resume() {
        //Socket io ON
        socketOn()
        //Api -> Enter Room
        RoomManager.instance.enterRoom(mRoomId, { userRooms ->
            userRooms?.let {
                mUserRoom = it
            }
        })
        //Emmit socket room connectivity
        UserManager.instance.getCurrentUser { success, user, _ ->
            if (success) {
                mSocket.emit("connect-rooms", user?.id, "channel")
            }
        }
    }


    override fun pause() {
        //Socket io OFF
        socketOff()
        //Api -> Leave Room
        RoomManager.instance.leaveRoom(mRoomId, { userRooms ->
            userRooms?.let {
                mUserRoom = it
            }
        })
    }

    override fun stop() {
    }

    override fun destroy() {

    }

    /*Messages*/
    override fun sendMessage() {
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

    override fun sendPost() {
    }

    override fun sendMedia() {
    }

    /*SOCKETS*/
    private fun socketOn() {
        //Socket
        if (!mIsSocketConnected) {
            mSocket.on(Constants.SocketKeys.UPDATE_ROOM, onUpdateRoom)
            mSocket.on(Constants.SocketKeys.UPDATE_CHAT_STRING, onNewMessage)
            mSocket.on(Constants.SocketKeys.UPDATE_CHAT_CHANNEL, onNewMessage)
            mSocket.on(Constants.SocketKeys.UPDATE_CHAT_CONTACT, onNewMessage)
            mSocket.on(Constants.SocketKeys.UPDATE_CHAT_LOCATION, onNewMessage)
            mSocket.on(Constants.SocketKeys.UPDATE_CHAT_MEDIA, onNewMessage)
            mSocket.on(Constants.SocketKeys.UPDATE_CHAT_POST, onNewMessage)
            mSocket.on(Constants.SocketKeys.ON_ERROR, onError)
            mIsSocketConnected = true
        }
    }

    private fun socketOff() {
        if (!mKeepSocketConnection) {
            mSocket.off(Constants.SocketKeys.UPDATE_ROOM)
            mSocket.off(Constants.SocketKeys.UPDATE_CHAT_STRING)
            mSocket.off(Constants.SocketKeys.UPDATE_CHAT_CHANNEL)
            mSocket.off(Constants.SocketKeys.UPDATE_CHAT_CONTACT)
            mSocket.off(Constants.SocketKeys.UPDATE_CHAT_LOCATION)
            mSocket.off(Constants.SocketKeys.UPDATE_CHAT_MEDIA)
            mSocket.off(Constants.SocketKeys.UPDATE_CHAT_POST)
            mSocket.off(Constants.SocketKeys.ON_ERROR)
            mIsSocketConnected = false
        }
    }

    override fun keepSocketConnection(keepConnection: Boolean) {
        mKeepSocketConnection = keepConnection
    }

    private val onUpdateRoom = Emitter.Listener { args ->
        mView.getActivity.runOnUiThread({
            val data: JSONObject = args[0] as JSONObject
            var userAdded: String? = null
            var userLeaving: String? = null
            try {
//                mRoomId = data.getInt("roomId")
                userAdded = data.getString("userAdded")
                userLeaving = data.getString("userLeaving")
            } catch (e: JSONException) {
                Log.e(TAG, e.message)
            }
            //retrieveChatHistory()
            if (userAdded != null) {

            }
            if (userLeaving != null) {

            }
        })
    }

    private val onNewMessage = Emitter.Listener { args ->
        mView.getActivity.runOnUiThread({
            val data: JSONObject = args[0] as JSONObject
            val message = GsonHolder.Companion.instance.get()!!.fromJson(data.toString(), Message::class.java)
            if (message.roomId == mRoomId) {
                if (mMessages.count() > 0) {
                    if (mMessages[0].id != message.id) {
                        mMessages.add(0, message)
                    }
                }
            }
            mView.updateContentRecycler()
        })
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
                                        RequestBody.create(MediaType.parse("image/png"), bitmapData))
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
    override fun getFollowedChannels(): MutableList<Channel>? =
            ChannelsManager.instance.getAllFollowedChannels()?.toMutableList()

    /*Messages*/
    override fun retrieveChatHistory() {
        mView.showProgress()
        ChannelsManager.instance.getChannelPosts(mRoomId, null, { channelPosts ->
            channelPosts.second?.let {
                mView.showError(it)
                mView.hideProgress()
            } ?: run {
                channelPosts.first?.let {
                    if (it.count() > 0) {
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
                                                RequestBody.create(MediaType.parse("image/png"), bitmapData))
                                    }
                                }
                                val formBody = formBodyBuilder.build()
                                val call = Rest.getInstance().getmCallerRx().postChannelMediaPost(token.token!!, formBody, true)
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
                                //Create String-Message
                                val call = Rest.getInstance().getmCallerRx().postChannelMessagePost(
                                        token.token!!,
                                        SendMessageStringData(user.id!!, message, mView.getRoomId),
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
                            }
                        }
                    }
                }
            }
        }

    }
}