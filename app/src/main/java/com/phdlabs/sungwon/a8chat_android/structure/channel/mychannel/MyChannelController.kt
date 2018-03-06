package com.phdlabs.sungwon.a8chat_android.structure.channel.mychannel

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.Socket
import com.phdlabs.sungwon.a8chat_android.api.data.SendMessageStringData
import com.phdlabs.sungwon.a8chat_android.api.event.MediaEvent
import com.phdlabs.sungwon.a8chat_android.api.event.MessageSentEvent
import com.phdlabs.sungwon.a8chat_android.api.event.RoomHistoryEvent
import com.phdlabs.sungwon.a8chat_android.api.response.ErrorResponse
import com.phdlabs.sungwon.a8chat_android.api.response.RoomHistoryResponse
import com.phdlabs.sungwon.a8chat_android.api.rest.Caller
import com.phdlabs.sungwon.a8chat_android.api.rest.Rest
import com.phdlabs.sungwon.a8chat_android.api.utility.Callback8
import com.phdlabs.sungwon.a8chat_android.api.utility.GsonHolder
import com.phdlabs.sungwon.a8chat_android.db.EventBusManager
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
import org.greenrobot.eventbus.EventBus
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response
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

    /*Network*/
    private lateinit var mCaller: Caller
    private lateinit var mEventBus: EventBus

    /*Initialize*/
    init {
        mView.controller = this
        mSocket = mView.get8Application.getSocket()
    }

    /*LifeCycle*/
    override fun onCreate() {
        //Callers
        mCaller = Rest.getInstance().caller
        mEventBus = EventBusManager.instance().mDataEventBus
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
                val call = mCaller.sendMessageString(
                        token?.token,
                        SendMessageStringData(user?.id!!, mView.getMessageET, mRoomId)
                )
                call.enqueue(object : Callback8<ErrorResponse, MessageSentEvent>(mEventBus) {
                    override fun onSuccess(data: ErrorResponse?) {
                        mView.getMessageETObject.setText("")
                    }
                })
            }
        }
    }

    override fun sendPost() {
    }

    override fun sendMedia() {
    }

    //Follow A channel
//    override fun createChannelRoom() {
//        UserManager.instance.getCurrentUser { success, user, token ->
//            if (success) {
//                val call = mCaller.followChannel(
//                        token?.token,
//                        mView.getChannelId,
//                        FollowUserData(arrayOf(user?.id!!))
//                )
//                call.enqueue(object : Callback8<RoomResponse, ChannelFollowEvent>(mEventBus) {
//                    override fun onSuccess(data: RoomResponse?) {
//                        mEventBus.post(ChannelFollowEvent())
//                        mRoomId = data!!.room!!.id!!
//                    }
//                })
//            }
//        }
//    }

    /*SOCKETS*/
    private fun socketOn() {
        //Socket
        mSocket.on(Constants.SocketKeys.UPDATE_ROOM, onUpdateRoom)
        mSocket.on(Constants.SocketKeys.UPDATE_CHAT_STRING, onNewMessage)
        mSocket.on(Constants.SocketKeys.UPDATE_CHAT_CHANNEL, onNewMessage)
        mSocket.on(Constants.SocketKeys.UPDATE_CHAT_CONTACT, onNewMessage)
        mSocket.on(Constants.SocketKeys.UPDATE_CHAT_LOCATION, onNewMessage)
        mSocket.on(Constants.SocketKeys.UPDATE_CHAT_MEDIA, onNewMessage)
        mSocket.on(Constants.SocketKeys.UPDATE_CHAT_POST, onNewMessage)
        mSocket.on(Constants.SocketKeys.ON_ERROR, onError)
    }

    private fun socketOff() {
        mSocket.off(Constants.SocketKeys.UPDATE_ROOM)
        mSocket.off(Constants.SocketKeys.UPDATE_CHAT_STRING)
        mSocket.off(Constants.SocketKeys.UPDATE_CHAT_CHANNEL)
        mSocket.off(Constants.SocketKeys.UPDATE_CHAT_CONTACT)
        mSocket.off(Constants.SocketKeys.UPDATE_CHAT_LOCATION)
        mSocket.off(Constants.SocketKeys.UPDATE_CHAT_MEDIA)
        mSocket.off(Constants.SocketKeys.UPDATE_CHAT_POST)
        mSocket.off(Constants.SocketKeys.ON_ERROR)
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
            retrieveChatHistory()
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
                mMessages.add(0, message)
                mView.updateContentRecycler()
            }
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
            mView.showProgress()
            //Set image in UI
            val imageUrl = CameraControl.instance.getImagePathFromResult(mView.getActivity, requestCode, resultCode, data)
            //Upload Image
            val imageBitmap = CameraControl.instance.getImageFromResult(mView.getActivity, requestCode, resultCode, data)
            imageBitmap.let {
                UserManager.instance.getCurrentUser { success, user, token ->
                    if (success) {
                        val bos = ByteArrayOutputStream()
                        it!!.compress(Bitmap.CompressFormat.PNG, 0, bos)
                        val bitmapData = bos.toByteArray()
                        val formBody = MultipartBody.Builder().setType(MultipartBody.FORM)
                                .addFormDataPart("userId", user?.id.toString())
                                .addFormDataPart("roomId", mRoomId.toString())
                                .addFormDataPart("file", "8_" + System.currentTimeMillis(), RequestBody.create(MediaType.parse("image/png"), bitmapData))
                                .build()
                        val call = Rest.getInstance().caller.sendMessageMedia(token?.token, formBody)
                        call.enqueue(object : Callback8<ErrorResponse, MediaEvent>(EventBusManager.instance().mDataEventBus) {
                            override fun onSuccess(data: ErrorResponse?) {
                                mView.hideProgress()
                                Toast.makeText(mView.getContext(), "Picture uploaded", Toast.LENGTH_SHORT).show()
                            }
                        })
                    }
                }
            }
            mView.hideProgress()
        }
    }

    override fun onPicturePostResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_CANCELED) {
            mView.showProgress()
            //Set image in UI
            val imageUrl = CameraControl.instance.getImagePathFromResult(mView.getActivity, requestCode, resultCode, data)
            //Upload Image
            val imageBitmap = CameraControl.instance.getImageFromResult(mView.getActivity, requestCode, resultCode, data)
            imageBitmap.let {
                UserManager.instance.getCurrentUser { success, user, token ->
                    if (success) {
                        val bos = ByteArrayOutputStream()
                        it!!.compress(Bitmap.CompressFormat.PNG, 0, bos)
                        val bitmapData = bos.toByteArray()
                        val formBody = MultipartBody.Builder().setType(MultipartBody.FORM)
                                .addFormDataPart("userId", user?.id.toString())
                                .addFormDataPart("roomId", mRoomId.toString())
                                .addFormDataPart("file", "8_" + System.currentTimeMillis(), RequestBody.create(MediaType.parse("image/png"), bitmapData))
                                .addFormDataPart("message", mView.getMessageET)
                                .build()
                        val call = Rest.getInstance().caller.sendMessageMedia(token?.token, formBody)
                        call.enqueue(object : Callback8<ErrorResponse, MediaEvent>(EventBusManager.instance().mDataEventBus) {
                            override fun onSuccess(data: ErrorResponse?) {
                                mView.hideProgress()
                                Toast.makeText(mView.getContext(), "Picture uploaded", Toast.LENGTH_SHORT).show()
                            }
                        })
                    }
                }
            }
            mView.hideProgress()
        }
    }

    /*Channels*/
    override fun getFollowedChannels(): MutableList<Channel>? =
            ChannelsManager.instance.getAllFollowedChannels()?.toMutableList()

    /*Messages*/
    override fun retrieveChatHistory() {
        UserManager.instance.getCurrentUser { success, user, token ->
            if (success) {
                val call = mCaller.getChannelPosts(
                        token?.token,
                        mRoomId,
                        user?.id!!,
                        null
                )
                call.enqueue(object : Callback8<RoomHistoryResponse, RoomHistoryEvent>(mEventBus) {
                    override fun onSuccess(data: RoomHistoryResponse?) {
                        mMessages.clear()
                        //TODO: need to add read first then unread always, but check for unread null instead.
                        for (item in data!!.messages!!.allMessages!!) {
                            if (item.roomId == mRoomId) {
                                mMessages.add(0, item)
                            }
                        }
                        mView.updateContentRecycler()
                        mView.hideProgress()
                    }

                    override fun onError(response: Response<RoomHistoryResponse>?) {
                        super.onError(response)
                        Log.e(TAG, response!!.message())
                        mView.hideProgress()
                    }
                })
            }
        }
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
                                .addFormDataPart("message", message)
                                .addFormDataPart("userId", user.id!!.toString())
                                .addFormDataPart("roomId", mView.getRoomId.toString())
                        //Create Post BodyPart
                        filePaths?.let {
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
                        }
                        val formBody = formBodyBuilder.build()
                        val call = Rest.getInstance().getmCallerRx().postChannelPost(it, formBody, true)
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