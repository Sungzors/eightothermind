package com.phdlabs.sungwon.a8chat_android.structure.channel.mychannel

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.Socket
import com.phdlabs.sungwon.a8chat_android.api.data.FollowUserData
import com.phdlabs.sungwon.a8chat_android.api.data.SendMessageStringData
import com.phdlabs.sungwon.a8chat_android.api.event.ChannelFollowEvent
import com.phdlabs.sungwon.a8chat_android.api.event.MediaEvent
import com.phdlabs.sungwon.a8chat_android.api.event.MessageSentEvent
import com.phdlabs.sungwon.a8chat_android.api.event.RoomHistoryEvent
import com.phdlabs.sungwon.a8chat_android.api.response.ErrorResponse
import com.phdlabs.sungwon.a8chat_android.api.response.RoomHistoryResponse
import com.phdlabs.sungwon.a8chat_android.api.response.RoomResponse
import com.phdlabs.sungwon.a8chat_android.api.rest.Caller
import com.phdlabs.sungwon.a8chat_android.api.rest.Rest
import com.phdlabs.sungwon.a8chat_android.api.utility.Callback8
import com.phdlabs.sungwon.a8chat_android.api.utility.GsonHolder
import com.phdlabs.sungwon.a8chat_android.db.EventBusManager
import com.phdlabs.sungwon.a8chat_android.model.Message
import com.phdlabs.sungwon.a8chat_android.structure.channel.ChannelContract
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import com.phdlabs.sungwon.a8chat_android.utility.Preferences
import com.phdlabs.sungwon.a8chat_android.utility.camera.CameraControl
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
 */
class MyChannelController(val mView: ChannelContract.MyChannel.View): ChannelContract.MyChannel.Controller{

    private val TAG = "MyChannelController"

    private var mSocket: Socket

    private var mRoomId: Int = 0

    private var mMessages = mutableListOf<Message>()

    private lateinit var mCaller: Caller
    private lateinit var mEventBus: EventBus

    private var isConnected: Boolean = false

    init {
        mView.controller = this
        mSocket = mView.get8Application.getSocket()
    }

    override fun start() {
        mCaller = Rest.getInstance().caller
        mEventBus = EventBusManager.instance().mDataEventBus
        mRoomId = mView.getRoomId

        mSocket.on(Constants.SocketKeys.CONNECT, onConnect)
        mSocket.on(Constants.SocketKeys.UPDATE_ROOM, onUpdateRoom)
        mSocket.on(Constants.SocketKeys.UPDATE_CHAT_STRING, onNewMessage)
        mSocket.on(Constants.SocketKeys.UPDATE_CHAT_CHANNEL, onNewMessage)
        mSocket.on(Constants.SocketKeys.UPDATE_CHAT_CONTACT, onNewMessage)
        mSocket.on(Constants.SocketKeys.UPDATE_CHAT_LOCATION, onNewMessage)
        mSocket.on(Constants.SocketKeys.UPDATE_CHAT_MEDIA, onNewMessage)
        mSocket.on(Constants.SocketKeys.UPDATE_CHAT_POST, onNewMessage)
        mSocket.on(Constants.SocketKeys.ON_ERROR, onError)
        mSocket.connect()
    }

    override fun resume() {
    }

    override fun pause() {
    }

    override fun stop() {
        mSocket.off(Constants.SocketKeys.CONNECT)
        mSocket.off(Constants.SocketKeys.UPDATE_ROOM)
        mSocket.off(Constants.SocketKeys.UPDATE_CHAT_STRING)
        mSocket.off(Constants.SocketKeys.UPDATE_CHAT_CHANNEL)
        mSocket.off(Constants.SocketKeys.UPDATE_CHAT_CONTACT)
        mSocket.off(Constants.SocketKeys.UPDATE_CHAT_LOCATION)
        mSocket.off(Constants.SocketKeys.UPDATE_CHAT_MEDIA)
        mSocket.off(Constants.SocketKeys.UPDATE_CHAT_POST)
        mSocket.off(Constants.SocketKeys.ON_ERROR)
    }

    override fun destroy() {
        mSocket.disconnect()
    }

    override fun sendMessage() {
        val call = mCaller.sendMessageString(
                Preferences(mView.getContext()!!).getPreferenceString(Constants.PrefKeys.TOKEN_KEY),
                SendMessageStringData(Preferences(mView.getContext()!!).getPreferenceInt(Constants.PrefKeys.USER_ID, 0).toString(), mView.getMessageET, mRoomId.toString())
        )
        call.enqueue(object : Callback8<ErrorResponse, MessageSentEvent>(mEventBus){
            override fun onSuccess(data: ErrorResponse?) {
                mView.getMessageETObject.setText("")
            }
        })
    }

    override fun sendPost() {
    }

    override fun sendMedia() {
    }

    override fun createChannelRoom() {
        val call = mCaller.followChannel(
                Preferences(mView.getContext()!!).getPreferenceString(Constants.PrefKeys.TOKEN_KEY),
                mView.getChannelId,
                FollowUserData(arrayOf(Preferences(mView.getContext()!!).getPreferenceInt(Constants.PrefKeys.USER_ID).toString()))
        )
        call.enqueue(object : Callback8<RoomResponse, ChannelFollowEvent>(mEventBus) {
            override fun onSuccess(data: RoomResponse?) {
                mEventBus.post(ChannelFollowEvent())
                mRoomId = data!!.room!!.id
            }
        })
    }

    override fun retrieveChatHistory() {
        val call = mCaller.getChannelPosts(
                Preferences(mView.getContext()!!).getPreferenceString(Constants.PrefKeys.TOKEN_KEY),
                mRoomId,
                Preferences(mView.getContext()!!).getPreferenceInt(Constants.PrefKeys.USER_ID, 0),
                null
        )
        call.enqueue(object: Callback8<RoomHistoryResponse, RoomHistoryEvent>(mEventBus){
            override fun onSuccess(data: RoomHistoryResponse?) {
                mMessages.clear()
                //TODO: need to add read first then unread always, but check for unread null instead.
                for(item in data!!.messages!!.allMessages!!){
                    if(item.roomId == mRoomId.toString()){
                        mMessages.add(0, item)
                    }
                }
                mView.updateRecycler()
                mView.hideProgress()
            }

            override fun onError(response: Response<RoomHistoryResponse>?) {
                super.onError(response)
                Log.e(TAG, response!!.message())
                mView.hideProgress()
            }
        })
    }

    private val onConnect = Emitter.Listener{ args ->
        mView.getActivity.runOnUiThread({
            val a = isConnected
            if (!isConnected) {
                Log.d(TAG, "Socket Connected")
                mSocket.emit("connect-rooms", Preferences(mView.getContext()!!).getPreferenceInt(Constants.PrefKeys.USER_ID, 0), "channel")
                isConnected = true
            }
        })
    }

    private val onUpdateRoom = Emitter.Listener { args ->
        mView.getActivity.runOnUiThread({
            val data : JSONObject = args[0] as JSONObject
            var userAdded : String? = null
            var userLeaving : String? = null
            try {
//                mRoomId = data.getInt("roomId")
                userAdded = data.getString("userAdded")
                userLeaving = data.getString("userLeaving")
            } catch (e: JSONException){
                Log.e(TAG, e.message)
            }
            retrieveChatHistory()
            if(userAdded != null){

            }
            if(userLeaving != null){

            }
        })
    }

    private val onNewMessage = Emitter.Listener { args ->
        mView.getActivity.runOnUiThread({
            val data : JSONObject = args[0] as JSONObject
            val message = GsonHolder.Companion.instance.get()!!.fromJson(data.toString(), Message::class.java)
            mMessages.add(0, message)
            mView.updateRecycler()
//            var id : String? = null
//            var message : String? = null
//            var roomId : String? = null
//            var userId : String? = null
//            var type : String? = null
//            try {
//                message = data.getString("message")
//            } catch (e:JSONException){
//                Log.e(TAG, e.message)
//            }
//            try {
//                id = data.getString("id")
//                roomId = data.getString("roomId")
//                userId = data.getString("userId")
//                type = data.getString("type")
//            } catch (e: JSONException){
//                Log.e(TAG, e.message)
//            }
//            if(type == null){
//                Log.e(TAG, "Message's type is null")
//                return@runOnUiThread
//            }
//            val builder = Message.Builder(id!!, type, userId!!, roomId!!)
//            when (type){
//                Message.TYPE_STRING -> {
//                    val message = builder.message(message!!).build()
//                    var name : String? = null
//                    var userAvatar : String? = null
//                    var createdAt = Date()
////                    var updatedAt : Date? = null
//                    var original_message_id : String? = null
//                    try {
//                        name = data.getString("name")
////                        userAvatar = data.getString("userAvatar")
//                        val createdAtString = data.getString("createdAt")
//                        val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
//                        createdAt = df.parse(createdAtString)
////                        val updatedAtString = data.getString("updatedAt")
////                        updatedAt = df.parse(updatedAtString)
//                        original_message_id = data.getString("original_message_id")
//                        message.name = name
////                        message.userAvatar = userAvatar
//                        message.createdAt = createdAt
////                        message.updatedAt = updatedAt
//                        message.original_message_id = original_message_id
//                    } catch (e: JSONException){
//                        Log.e(TAG, e.message)
//                    }
//                    mMessages.add(message)
//                    mView.updateRecycler()
//                }
//                Message.TYPE_CHANNEL -> {
//                    val channelInfo : JSONObject
//                    var id : Int? = null
//                    var name : String? = null
//                    var unique_id : String? = null
//                    var description : String? = null
//                    var color : String? = null
//                    var background : String? = null
//                    var add_to_profile : Boolean? = null
//                    var user_creator_id : String? = null
//                    var room_id : String? = null
//                    var channel : Channel? = null
//                    try {
//                        channelInfo = data.getJSONObject("channelInfo")
//                        id = channelInfo.getInt("id")
//                        name = channelInfo.getString("name")
//                        unique_id = channelInfo.getString("unique_id")
//                        description = channelInfo.getString("description")
//                        color = channelInfo.getString("color")
//                        background = channelInfo.getString("background")
//                        add_to_profile = channelInfo.getBoolean("add_to_profile")
//                        user_creator_id = channelInfo.getString("user_creator_id")
//                        room_id = channelInfo.getString("room_id")
//                        channel = Channel(id, name, unique_id, room_id)
//                        channel.description = description
//                        channel.color = color
//                        channel.background = background
//                        channel.add_to_profile = add_to_profile
//                        channel.user_creator_id = user_creator_id
//                    } catch (e: JSONException){
//                        Log.e(TAG, e.message)
//                        return@runOnUiThread
//                    }
//                    mMessages.add(builder.message(message).channel(channel).build())
//                    mView.updateRecycler()
//                }
//                Message.TYPE_MEDIA ->{
//                    val mediaArray : JSONObject
//                    val media_file_string : String
//                    try {
//                        mediaArray = data.getJSONObject("mediaArray")
//                        media_file_string = mediaArray.getString("media_file_string")
//                    } catch (e: JSONException){
//                        Log.e(TAG, e.message)
//                        return@runOnUiThread
//                    }
//                    mMessages.add(builder.message(message).media(media_file_string).build())
//                    mView.updateRecycler()
//                }
//                Message.TYPE_POST ->{
//                    val mediaArray : JSONObject
//                    val media_file_string : String
//                    try {
//                        mediaArray = data.getJSONObject("mediaArray")
//                        media_file_string = mediaArray.getString("media_file_string")
//                    } catch (e: JSONException){
//                        Log.e(TAG, e.message)
//                        return@runOnUiThread
//                    }
//                    mMessages.add(builder.message(message).media(media_file_string).build())
//                    mView.updateRecycler()
//                }
//            }
        })
    }

    private val onError = Emitter.Listener { args ->
        mView.getActivity.runOnUiThread {
            val message = args[0] as JSONObject
            Log.e(TAG, message.getString("message"))
        }
    }

    override fun onPictureOnlyResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_CANCELED) {
            mView.showProgress()
            //Set image in UI
            val imageUrl = CameraControl.instance.getImagePathFromResult(mView.getActivity, requestCode, resultCode, data)
            //Upload Image
            val imageBitmap = CameraControl.instance.getImageFromResult(mView.getActivity, requestCode, resultCode, data)
            imageBitmap.let {
                val bos = ByteArrayOutputStream()
                it!!.compress(Bitmap.CompressFormat.PNG, 0, bos)
                val bitmapData = bos.toByteArray()
                val pref = Preferences(mView.getContext()!!)
                val formBody = MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("userId", pref.getPreferenceInt(Constants.PrefKeys.USER_ID, -1).toString())
                        .addFormDataPart("roomId", mRoomId.toString())
                        .addFormDataPart("file", "8chat" + System.currentTimeMillis(), RequestBody.create(MediaType.parse("image/png"), bitmapData))
                        .build()
                val call = Rest.getInstance().caller.sendMessageMedia(pref.getPreferenceString(Constants.PrefKeys.TOKEN_KEY), formBody)
                call.enqueue(object : Callback8<ErrorResponse, MediaEvent>(EventBusManager.instance().mDataEventBus) {
                    override fun onSuccess(data: ErrorResponse?) {
                        mView.hideProgress()
                        Toast.makeText(mView.getContext(), "Picture uploaded", Toast.LENGTH_SHORT).show()
                    }
                })

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
                val bos = ByteArrayOutputStream()
                it!!.compress(Bitmap.CompressFormat.PNG, 0, bos)
                val bitmapData = bos.toByteArray()
                val pref = Preferences(mView.getContext()!!)
                val formBody = MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("userId", pref.getPreferenceInt(Constants.PrefKeys.USER_ID, -1).toString())
                        .addFormDataPart("roomId", mRoomId.toString())
                        .addFormDataPart("file", "8chat" + System.currentTimeMillis(), RequestBody.create(MediaType.parse("image/png"), bitmapData))
                        .addFormDataPart("message", mView.getMessageET)
                        .build()
                val call = Rest.getInstance().caller.sendMessageMedia(pref.getPreferenceString(Constants.PrefKeys.TOKEN_KEY), formBody)
                call.enqueue(object : Callback8<ErrorResponse, MediaEvent>(EventBusManager.instance().mDataEventBus) {
                    override fun onSuccess(data: ErrorResponse?) {
                        mView.hideProgress()
                        Toast.makeText(mView.getContext(), "Picture uploaded", Toast.LENGTH_SHORT).show()
                    }
                })

            }
            mView.hideProgress()
        }
    }

    override fun getMessages() : MutableList<Message> = mMessages
}