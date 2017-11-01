package com.phdlabs.sungwon.a8chat_android.structure.chat

import android.util.Log
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.Socket
import com.phdlabs.sungwon.a8chat_android.api.data.PrivateChatCreateData
import com.phdlabs.sungwon.a8chat_android.api.event.PrivateChatCreateEvent
import com.phdlabs.sungwon.a8chat_android.api.response.RoomResponse
import com.phdlabs.sungwon.a8chat_android.api.rest.Caller
import com.phdlabs.sungwon.a8chat_android.api.rest.Rest
import com.phdlabs.sungwon.a8chat_android.api.utility.Callback8
import com.phdlabs.sungwon.a8chat_android.db.EventBusManager
import com.phdlabs.sungwon.a8chat_android.db.UserManager
import com.phdlabs.sungwon.a8chat_android.model.Channel
import com.phdlabs.sungwon.a8chat_android.model.Message
import com.phdlabs.sungwon.a8chat_android.model.User
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import com.phdlabs.sungwon.a8chat_android.utility.Preferences
import org.greenrobot.eventbus.EventBus
import org.json.JSONException
import org.json.JSONObject


/**
 * Created by SungWon on 10/18/2017.
 */
class ChatController(val mView: ChatContract.View): ChatContract.Controller {

    private val TAG = "ChatController"

    private var mSocket: Socket

    private var mRoomId: Int = 0
    private lateinit var mRoomName: String
    private lateinit var mRoomType: String

    private val mMessages = mutableListOf<Message>()

    private lateinit var mCaller: Caller
    private lateinit var mEventBus: EventBus

    private var isConnected: Boolean = true

    init {
        mView.controller = this
        mSocket = mView.get8Application.getSocket()
    }

    //lc
    override fun start() {
        mCaller = Rest.getInstance().caller
        mEventBus = EventBusManager.instance().mDataEventBus
        mSocket.connect()
        // TODO: requires dynamic room name
        mRoomName = "testRoom1"
        mRoomType = "privateChat"

        mSocket.on(Constants.SocketKeys.CONNECT, onConnect)
        mSocket.on(Constants.SocketKeys.UPDATE_ROOM, onUpdateRoom)
        mSocket.on(Constants.SocketKeys.UPDATE_CHAT_STRING, onNewMessage)
        mSocket.on(Constants.SocketKeys.UPDATE_CHAT_CHANNEL, onNewMessage)
        mSocket.on(Constants.SocketKeys.UPDATE_CHAT_CONTACT, onNewMessage)
        mSocket.on(Constants.SocketKeys.UPDATE_CHAT_LOCATION, onNewMessage)
        mSocket.on(Constants.SocketKeys.UPDATE_CHAT_MEDIA, onNewMessage)
        mSocket.on(Constants.SocketKeys.ON_ERROR, onNewMessage)
    }

    override fun resume() {
    }

    override fun pause() {
    }

    override fun stop() {
    }

    override fun destroy() {
        mSocket.disconnect()
    }

    override fun createPrivateChatRoom() {
        val call = mCaller.createPrivateChatRoom(
                Preferences(mView.getContext()!!).getPreferenceString(Constants.PrefKeys.TOKEN_KEY),
                PrivateChatCreateData(mutableListOf(UserManager.instance().user!!.id, mView.getChatParticipant)))
        call.enqueue(object: Callback8<RoomResponse, PrivateChatCreateEvent>(mEventBus){
            override fun onSuccess(data: RoomResponse?) {
                mEventBus.post(PrivateChatCreateEvent())
                mRoomId = data!!.room!!.id
            }
        })
    }

    override val getMessages: MutableList<Message>
        get() = mMessages

    override val getUserId: Int
        get() = UserManager.instance().user!!.id

    //listeners
    private val onConnect = Emitter.Listener{ args ->
        mView.getActivity.runOnUiThread({
            if (!isConnected) {
                Log.d(TAG, "Socket Connected")
                mSocket.emit("check-rooms-user-participates", UserManager.instance().user!!.id, mRoomType)
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
                mRoomId = data.getInt("roomId")
                userAdded = data.getString("userAdded")
                userLeaving = data.getString("userLeaving")
            } catch (e: JSONException){
                Log.e(TAG, e.message)
            }
            if(userAdded != null){

            }
            if(userLeaving != null){

            }
        })
    }

//    private val onDisconnect = Emitter.Listener { args ->
//        mView.getActivity.runOnUiThread({
//            Log.d(TAG, "Socket Disconnected")
//            isConnected = false
//        })
//    }
//
//    private val onConnectError = Emitter.Listener { args ->
//        mView.getActivity.runOnUiThread({
//            Log.e(TAG, "Error Connecting")
//        })
//    }

    private val onNewMessage = Emitter.Listener { args ->
        mView.getActivity.runOnUiThread({
            val data : JSONObject = args[0] as JSONObject
            var message : String? = null
            var roomId : String? = null
            var userId : String? = null
            var type : String? = null
            try {
                message = data.getString("message")
                roomId = data.getString("roomId")
                userId = data.getString("userId")
                type = data.getString("type")
            } catch (e: JSONException){
                Log.e(TAG, e.message)
            }
            if(type == null){
                Log.e(TAG, "Message's type is null")
                return@runOnUiThread
            }
            val builder = Message.Builder(type, userId!!, roomId!!)
            when (type){
                Message.TYPE_STRING -> {
                    mMessages.add(builder.message(message!!).build())
                    mView.updateRecycler()
                }
                Message.TYPE_CHANNEL -> {
                    val channelInfo : JSONObject
                    var id : Int? = null
                    var name : String? = null
                    var unique_id : String? = null
                    var description : String? = null
                    var color : String? = null
                    var background : String? = null
                    var add_to_profile : String? = null
                    var user_creator_id : String? = null
                    var room_id : String? = null
                    var channel : Channel? = null
                    try {
                        channelInfo = data.getJSONObject("channelInfo")
                        id = channelInfo.getInt("id")
                        name = channelInfo.getString("name")
                        unique_id = channelInfo.getString("unique_id")
                        description = channelInfo.getString("description")
                        color = channelInfo.getString("color")
                        background = channelInfo.getString("background")
                        add_to_profile = channelInfo.getString("add_to_profile")
                        user_creator_id = channelInfo.getString("user_creator_id")
                        room_id = channelInfo.getString("room_id")
                        channel = Channel(id, name, unique_id, room_id)
                        channel.description = description
                        channel.color = color
                        channel.background = background
                        channel.add_to_profile = add_to_profile
                        channel.user_creator_id = user_creator_id
                    } catch (e: JSONException){
                        Log.e(TAG, e.message)
                        return@runOnUiThread
                    }
                    mMessages.add(builder.message(message).channel(channel).build())
                    mView.updateRecycler()
                }
                Message.TYPE_CONTACT ->{
                    val contactInfo : JSONObject
                    var id : Int? = null
                    var first_name : String? = null
                    var last_name : String? = null
                    var phone : String? = null
                    var country_code : String? = null
                    var email : String? = null
                    val language_spoken = mutableListOf<String>()
                    var country : String? = null
                    var profile_picture_string : String? = null
                    var avatar : String? = null
//                    var position : JSONObject
                    var verified : Boolean? = null
                    var socket_id : String? = null
                    var contact : User? = null
                    try {
                        contactInfo = data.getJSONObject("contactInfo")
                        id = contactInfo.getInt("id")
                        first_name = contactInfo.getString("first_name")
                        last_name = contactInfo.getString("last_name")
                        phone = contactInfo.getString("phone")
                        country_code = contactInfo.getString("country_code")
                        email = contactInfo.getString("email")
                        val contacts = contactInfo.getJSONArray("language_spoken")
                        for(i in 0..(contacts.length() - 1)){
                            language_spoken.add(contacts.get(i) as String)
                        }
                        country = contactInfo.getString("country")
                        profile_picture_string = contactInfo.getString("profile_picture_string")
                        avatar = contactInfo.getString("avatar")
//                        position = contactInfo.getJSONObject("room_id")
                        verified = contactInfo.getBoolean("verified")
                        socket_id = contactInfo.getString("socket_id")
                        contact = User(id, first_name, last_name)
                        contact.phone = phone
                        contact.country_code = country_code
                        contact.email = email
                        contact.country = country
                        contact.profile_picture_string = profile_picture_string
                        contact.avatar = avatar
                        contact.verified = verified
                        contact.socket_id = socket_id
                    } catch (e: JSONException){
                        Log.e(TAG, e.message)
                        return@runOnUiThread
                    }
                    mMessages.add(builder.message(message).contact(contact).build())
                    mView.updateRecycler()
                }

            }
            //TODO: update adapter
        })
    }


}