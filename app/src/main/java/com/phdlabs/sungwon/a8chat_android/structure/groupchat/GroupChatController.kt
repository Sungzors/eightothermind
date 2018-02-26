package com.phdlabs.sungwon.a8chat_android.structure.groupchat

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.Toast
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.Socket
import com.phdlabs.sungwon.a8chat_android.api.data.SendMessageChannelData
import com.phdlabs.sungwon.a8chat_android.api.data.SendMessageGeneralData
import com.phdlabs.sungwon.a8chat_android.api.data.SendMessageStringData
import com.phdlabs.sungwon.a8chat_android.api.event.MessageLocationSentEvent
import com.phdlabs.sungwon.a8chat_android.api.event.MessageSentEvent
import com.phdlabs.sungwon.a8chat_android.api.event.RoomHistoryEvent
import com.phdlabs.sungwon.a8chat_android.api.response.ErrorResponse
import com.phdlabs.sungwon.a8chat_android.api.response.RoomHistoryResponse
import com.phdlabs.sungwon.a8chat_android.api.rest.Caller
import com.phdlabs.sungwon.a8chat_android.api.rest.Rest
import com.phdlabs.sungwon.a8chat_android.api.utility.Callback8
import com.phdlabs.sungwon.a8chat_android.api.utility.GsonHolder
import com.phdlabs.sungwon.a8chat_android.db.EventBusManager
import com.phdlabs.sungwon.a8chat_android.db.user.UserManager
import com.phdlabs.sungwon.a8chat_android.model.message.Message
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import org.greenrobot.eventbus.EventBus
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response

/**
 * Created by SungWon on 2/19/2018.
 */
class GroupChatController(val mView: GroupChatContract.View): GroupChatContract.Controller {

    private val TAG = "GroupChatController"

    private var mSocket: Socket

    private var mRoomId: Int = 0
    private lateinit var mRoomName: String
    private val mRoomType = "groupChat"

    private var mMessages = mutableListOf<Message>()

    private val mCaller: Caller
    private val mEventBus: EventBus

    private var isConnected: Boolean = false

    private val mLocationManager: LocationManager
    private var mLocation: Pair<String?, String?>? = null

    init {
        mView.controller = this
        mSocket = mView.get8Application.getSocket()
        mCaller = Rest.getInstance().caller
        mEventBus = EventBusManager.instance().mDataEventBus
        mLocationManager = mView.getContext()?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    override fun start() {
        getUserId { id ->
            Log.d(TAG, "Socket Connected")
            mSocket.emit("connect-rooms", id, mRoomType)
            isConnected = true
        }
        mRoomId = mView.getRoomId
        mView.getContext()?.let {
            /*Location permissions*/
            if (ActivityCompat.checkSelfPermission(
                            it, Constants.AppPermissions.FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(
                            it, Constants.AppPermissions.COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
            ) {
                mView.hideProgress()
                requestLocationPermissions()
                return
            }
        }

        try{
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener)
        } catch (ex: SecurityException){
            println("No location available: " + ex.message)
        }
        retrieveChatHistory()
    }

    override fun resume() {
        mSocket.on(Constants.SocketKeys.UPDATE_ROOM, onUpdateRoom)
        mSocket.on(Constants.SocketKeys.UPDATE_CHAT_STRING, onNewMessage)
        mSocket.on(Constants.SocketKeys.UPDATE_CHAT_CHANNEL, onNewMessage)
        mSocket.on(Constants.SocketKeys.UPDATE_CHAT_CONTACT, onNewMessage)
        mSocket.on(Constants.SocketKeys.UPDATE_CHAT_LOCATION, onNewMessage)
        mSocket.on(Constants.SocketKeys.UPDATE_CHAT_MEDIA, onNewMessage)
        mSocket.on(Constants.SocketKeys.ON_ERROR, onError)
    }

    override fun pause() {
        mSocket.off(Constants.SocketKeys.UPDATE_ROOM)
        mSocket.off(Constants.SocketKeys.UPDATE_CHAT_STRING)
        mSocket.off(Constants.SocketKeys.UPDATE_CHAT_CHANNEL)
        mSocket.off(Constants.SocketKeys.UPDATE_CHAT_CONTACT)
        mSocket.off(Constants.SocketKeys.UPDATE_CHAT_LOCATION)
        mSocket.off(Constants.SocketKeys.UPDATE_CHAT_MEDIA)
        mSocket.off(Constants.SocketKeys.ON_ERROR)
    }

    override fun stop() {
    }

    override fun destroy() {
    }

    override fun setMessageObject(position: Int, message: Message) {
        mMessages.set(position, message)
    }

    override fun getRoomId() = mRoomId

    override fun retrieveChatHistory() {
        getUserId { id ->
            id?.let {
                UserManager.instance.getCurrentUser { success, _, token ->
                    if (success) {
                        val call = mCaller.getGCMessageHistory(
                                token?.token,
                                mRoomId,
                                id
                        )
                        call.enqueue(object : Callback8<RoomHistoryResponse, RoomHistoryEvent>(mEventBus) {
                            override fun onSuccess(data: RoomHistoryResponse?) {
                                mMessages.clear()
                                //TODO: need to add read first then unread always, but check for unread null instead.
                                for (item in data!!.messages!!.allMessages!!) {
                                    if (item.roomId == mRoomId) {
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
        }
    }

    override fun sendChannel(channelId: Int) {
        getUserId { id ->
            id?.let {
                UserManager.instance.getCurrentUser { success, _, token ->
                    if (success) {
                        val call = mCaller.sendMessageChannel(
                                token?.token,
                                SendMessageChannelData(id.toString(), mRoomId.toString(), channelId.toString())
                        )
                        call.enqueue(object : Callback8<ErrorResponse, MessageSentEvent>(mEventBus) {
                            override fun onSuccess(data: ErrorResponse?) {
                                mEventBus.post(MessageSentEvent())
                                mView.getMessageETObject.setText("")
                            }
                        })
                    }
                }
            }
        }
    }

    override fun sendMessage() {
        getUserId { id ->
            id?.let {
                UserManager.instance.getCurrentUser { success, _, token ->
                    if (success) {
                        val call = mCaller.sendMessageString(
                                token?.token,
                                SendMessageStringData(id, mView.getMessageET, mRoomId)
                        )
                        call.enqueue(object : Callback8<ErrorResponse, MessageSentEvent>(mEventBus) {
                            override fun onSuccess(data: ErrorResponse?) {
                                mEventBus.post(MessageSentEvent())
                                mView.getMessageETObject.setText("")
                            }
                        })
                    }
                }
            }
        }
    }

    override fun sendLocation() {
//        if (ActivityCompat.checkSelfPermission(mRoot.getContext()!!, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mRoot.getContext()!!, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return
//        }
//        mFusedLocationClient.lastLocation.addOnSuccessListener({ location ->
//            if(location != null){
//
//            }
//        })
        if(mLocation == null){
            Toast.makeText(mView.getContext()!!, "Failed to get location (Try enabling location permissions)", Toast.LENGTH_SHORT).show()
        } else {
            getUserId { id ->
                mView.showProgress()
                id?.let {
                    UserManager.instance.getCurrentUser { success, _, token ->
                        if (success) {
                            val call = mCaller.sendMessageLocation(
                                    token?.token,
                                    SendMessageGeneralData(id.toString(), mRoomId.toString(),mLocation!!.first!!, mLocation!!.second!!)
                            )
                            call.enqueue(object : Callback8<ErrorResponse, MessageLocationSentEvent>(mEventBus) {
                                override fun onSuccess(data: ErrorResponse?) {
                                    mEventBus.post(MessageLocationSentEvent())
                                    mView.hideProgress()
                                    mView.hideDrawer()
                                }
                            })
                        }
                    }
                }
            }
        }

    }

    override fun sendMedia() {
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

    //listeners
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
            val data: JSONObject = args[0] as JSONObject
            val message = GsonHolder.Companion.instance.get()!!.fromJson(data.toString(), Message::class.java)
            message.timeDisplayed = mView.lastTimeDisplayed(message)
            mMessages.add(message)
            mView.updateRecycler(mMessages.size)

        })
    }

    private val onError = Emitter.Listener { args ->
        mView.getActivity.runOnUiThread {
            val message = args[0] as String //args[0] as JSONObject
            Toast.makeText(mView.getContext(), message, Toast.LENGTH_SHORT).show()
            //Log.e(TAG, message.getString("message"))

        }
    }

    private fun requestLocationPermissions() {
        val whatPermissions = arrayOf(Constants.AppPermissions.FINE_LOCATION,
                Constants.AppPermissions.COARSE_LOCATION)
        mView.getContext()?.let {
            //Request Permissions
            if (ContextCompat.checkSelfPermission(it, whatPermissions.get(0)) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(it, whatPermissions.get(1)) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(mView.getActivity, whatPermissions, Constants.PermissionsReqCode.LOCATION_REQ_CODE)
            }
        }
    }

    private val locationListener: LocationListener = object : LocationListener {
        /*Current Location*/
        override fun onLocationChanged(location: Location) {
            mLocation = (Pair(
                    location.longitude.toString().trim(),
                    location.latitude.toString().trim())
                    )
        }

        /*Not necessary to handle on single location update*/
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}

        /*Not necessary to handle on single location update*/
        override fun onProviderEnabled(provider: String) {}

        /*Not necessary to handle on single location update*/
        override fun onProviderDisabled(provider: String) {}
    }

    override fun onPictureResult(requestCode: Int, resultCode: Int, data: Intent?) {
    }
}