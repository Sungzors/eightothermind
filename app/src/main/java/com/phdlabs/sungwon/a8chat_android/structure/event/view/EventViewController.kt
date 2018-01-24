package com.phdlabs.sungwon.a8chat_android.structure.event.view

import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.Socket
import com.google.gson.Gson
import com.phdlabs.sungwon.a8chat_android.api.data.SendMessageGeneralData
import com.phdlabs.sungwon.a8chat_android.api.event.MessageLocationSentEvent
import com.phdlabs.sungwon.a8chat_android.api.event.RoomHistoryEvent
import com.phdlabs.sungwon.a8chat_android.api.response.ErrorResponse
import com.phdlabs.sungwon.a8chat_android.api.response.RoomHistoryResponse
import com.phdlabs.sungwon.a8chat_android.api.rest.Caller
import com.phdlabs.sungwon.a8chat_android.api.rest.Rest
import com.phdlabs.sungwon.a8chat_android.api.utility.Callback8
import com.phdlabs.sungwon.a8chat_android.db.EventBusManager
import com.phdlabs.sungwon.a8chat_android.db.UserManager
import com.phdlabs.sungwon.a8chat_android.model.Channel
import com.phdlabs.sungwon.a8chat_android.model.Message
import com.phdlabs.sungwon.a8chat_android.model.user.User
import com.phdlabs.sungwon.a8chat_android.structure.event.EventContract
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import com.phdlabs.sungwon.a8chat_android.utility.Preferences
import org.greenrobot.eventbus.EventBus
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by SungWon on 1/8/2018.
 */
class EventViewController(val mView: EventContract.ViewDetail.View): EventContract.ViewDetail.Controller{

    private val TAG = "EventViewController"

    private val mSocket: Socket
    private val mCaller: Caller
    private val mEventBus: EventBus

    private var mMessages = mutableListOf<Message>()

    private var mRoomId: Int = 0
    private var mRoomType: String = "event"

    private var isConnected: Boolean = false

    init {
        mView.controller = this
        mSocket = mView.get8Application.getSocket()
        mCaller = Rest.getInstance().caller
        mEventBus = EventBusManager.instance().mDataEventBus
    }

    override fun start() {
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
    }

    private val onConnect = Emitter.Listener { args ->
        mView.getActivity.runOnUiThread({
            val a = isConnected
            if (!isConnected) {
                getUserId { id ->
                    Log.d(TAG, "Socket Connected")
                    mSocket.emit("connect-rooms", id, mRoomType)
                    isConnected = true
                }
            }
        })
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
            val gson = Gson()
            val data: JSONObject = args[0] as JSONObject
            val gsonData = gson.fromJson(args[0] as String, Channel::class.java)
            var id: String? = null
            var message: String? = null
            var roomId: String? = null
            var userId: String? = null
            var type: String? = null
            try {
                message = data.getString("message")
            } catch (e: JSONException) {
                Log.e(TAG, e.message)
            }
            try {
                id = data.getString("id")
                roomId = data.getString("roomId")
                userId = data.getString("userId")
                type = data.getString("type")
            } catch (e: JSONException) {
                Log.e(TAG, e.message)
            }
            if (type == null) {
                Log.e(TAG, "Message's type is null")
                return@runOnUiThread
            }
            val builder = Message.Builder(id!!, type, userId!!, roomId!!)
            when (type) {
                Message.TYPE_STRING -> {
                    val message = builder.message(message!!).build()
                    var name : String? = null
                    var userAvatar : String? = null
                    var createdAt : Date? = null
//                    var updatedAt : Date? = null
                    var original_message_id: String? = null
                    try {
                        name = data.getString("name")
                        userAvatar = data.getString("userAvatar")
                        val createdAtString = data.getString("createdAt")
                        val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                        createdAt = df.parse(createdAtString)
//                        val updatedAtString = data.getString("updatedAt")
//                        updatedAt = df.parse(updatedAtString)
                        original_message_id = data.getString("original_message_id")
                        message.name = name
                        message.userAvatar = userAvatar
                        message.createdAt = createdAt
//                        message.updatedAt = updatedAt
                        message.original_message_id = original_message_id
                    } catch (e: JSONException) {
                        Log.e(TAG, e.message)
                    }
                    message.timeDisplayed = mView.lastTimeDisplayed(message)
                    mMessages!!.add(message)
                    mView.updateRecycler()
                }
                Message.TYPE_CHANNEL -> {
                    val channelInfo: JSONObject
                    var id: Int? = null
                    var name: String? = null
                    var unique_id: String? = null
                    var description: String? = null
                    var color: String? = null
                    var background: String? = null
                    var add_to_profile: Boolean? = null
                    var user_creator_id: String? = null
                    var room_id: String? = null
                    var channel: Channel? = null
                    try {
                        channelInfo = data.getJSONObject("channelInfo")
                        id = channelInfo.getInt("id")
                        name = channelInfo.getString("name")
                        unique_id = channelInfo.getString("unique_id")
                        description = channelInfo.getString("description")
                        color = channelInfo.getString("color")
                        background = channelInfo.getString("background")
                        add_to_profile = channelInfo.getBoolean("add_to_profile")
                        user_creator_id = channelInfo.getString("user_creator_id")
                        room_id = channelInfo.getString("room_id")
                        channel = Channel(id, name, unique_id, room_id)
                        channel.description = description
                        channel.color = color
                        channel.background = background
                        channel.add_to_profile = add_to_profile
                        channel.user_creator_id = user_creator_id
                    } catch (e: JSONException) {
                        Log.e(TAG, e.message)
                        return@runOnUiThread
                    }
                    mMessages!!.add(builder.message(message).channel(channel).build())
                    mView.updateRecycler()
                }
                Message.TYPE_CONTACT -> {
                    val contactInfo: JSONObject
                    var id: Int? = null
                    var first_name: String? = null
                    var last_name: String? = null
                    var phone: String? = null
                    var country_code: String? = null
                    var email: String? = null
                    val language_spoken = mutableListOf<String>()
                    var country: String? = null
                    var profile_picture_string: String? = null
                    var avatar: String? = null
//                    var position : JSONObject
                    var verified: Boolean? = null
                    var socket_id: String? = null
                    var contact: User? = null
                    try {
                        contactInfo = data.getJSONObject("contactInfo")
                        id = contactInfo.getInt("id")
                        first_name = contactInfo.getString("first_name")
                        last_name = contactInfo.getString("last_name")
                        phone = contactInfo.getString("phone")
                        country_code = contactInfo.getString("country_code")
                        email = contactInfo.getString("email")
                        val contacts = contactInfo.getJSONArray("language_spoken")
                        for (i in 0..(contacts.length() - 1)) {
                            language_spoken.add(contacts.get(i) as String)
                        }
                        country = contactInfo.getString("country")
                        profile_picture_string = contactInfo.getString("profile_picture_string")
                        avatar = contactInfo.getString("avatar")
//                        position = contactInfo.getJSONObject("room_id")
                        verified = contactInfo.getBoolean("verified")
                        socket_id = contactInfo.getString("socket_id")
                        contact = User()
                        contact.id = id
                        contact.first_name = first_name
                        contact.last_name = last_name
                        contact.phone = phone
                        contact.country_code = country_code
                        contact.email = email
                        contact.country = country
                        contact.profile_picture_string = profile_picture_string
                        contact.avatar = avatar
                        contact.verified = verified
                        contact.socket_id = socket_id
                    } catch (e: JSONException) {
                        Log.e(TAG, e.message)
                        return@runOnUiThread
                    }
                    mMessages.add(builder.message(message).contact(contact).build())
                    mView.updateRecycler()
                }
                Message.TYPE_LOCATION -> {
                    val message = builder.message(message!!).build()
                    var name : String? = null
                    var userAvatar : String? = null
                    var createdAt : Date? = null
//                    var updatedAt : Date? = null
                    var original_message_id: String? = null
                    try {
                        name = data.getString("name")
                        userAvatar = data.getString("userAvatar")
                        val createdAtString = data.getString("createdAt")
                        val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                        createdAt = df.parse(createdAtString)
//                        val updatedAtString = data.getString("updatedAt")
//                        updatedAt = df.parse(updatedAtString)
                        original_message_id = data.getString("original_message_id")
                        message.name = name
                        message.userAvatar = userAvatar
                        message.createdAt = createdAt
//                        message.updatedAt = updatedAt
                        message.original_message_id = original_message_id
                    } catch (e: JSONException) {
                        Log.e(TAG, e.message)
                    }
                    message.timeDisplayed = mView.lastTimeDisplayed(message)
                    mMessages.add(message)
                    mView.updateRecycler()
                }

            }
        })
    }

    private val onError = Emitter.Listener { args ->
        mView.getActivity.runOnUiThread {
            val message = args[0] as String //args[0] as JSONObject
            Toast.makeText(mView.getContext(),message, Toast.LENGTH_SHORT).show()
            //Log.e(TAG, message.getString("message"))

        }
    }

    override fun sendMessage() {
    }

    override fun sendChannel(channelId: Int) {
    }

    override fun sendContact() {
    }

    override fun sendFile() {
    }

    override fun sendLocation() {
        getUserId { id ->
            id?.let {
                val call = mCaller.sendMessageLocation(
                        Preferences(mView.getContext()!!).getPreferenceString(Constants.PrefKeys.TOKEN_KEY),
                        SendMessageGeneralData(id.toString(), mRoomId.toString())
                )
                call.enqueue(object : Callback8<ErrorResponse, MessageLocationSentEvent>(mEventBus) {
                    override fun onSuccess(data: ErrorResponse?) {
                        mEventBus.post(MessageLocationSentEvent())
                    }
                })
            }
        }
    }

    override fun sendMedia() {
    }

    override fun onDestroy() {
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

    override fun setMessageObject(position: Int, message: Message) {
        mMessages.set(position, message)
    }

    override fun retrieveChatHistory() {getUserId { id ->
        id?.let {
            val call = mCaller.getEventHistory(
                    Preferences(mView.getContext()!!).getPreferenceString(Constants.PrefKeys.TOKEN_KEY),
                    mRoomId,
                    id
            )
            call.enqueue(object : Callback8<RoomHistoryResponse, RoomHistoryEvent>(mEventBus) {
                override fun onSuccess(data: RoomHistoryResponse?) {
                    mMessages.clear()
                    //TODO: need to add read first then unread always, but check for unread null instead.
                    for (item in data!!.messages!!.allMessages!!) {
                        if (item.roomId == mRoomId.toString()) {
                            mMessages.add(item)
                        }
                    }
                    var i = 0
                    for (item in mMessages!!) {
                        item.timeDisplayed = mView.lastTimeDisplayed(i)
                        setMessageObject(i, item)
                        i++
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
    }
    }

    override fun getMessages(): MutableList<Message> = mMessages

    override fun getUserId(callback: (Int?) -> Unit) {
        UserManager.instance.getCurrentUser { success, user, _ ->
            if (success) {
                user?.id.let {
                    callback(it)
                }
            }
        }
    }

    override fun onPictureResult(requestCode: Int, resultCode: Int, data: Intent?) {
    }
}