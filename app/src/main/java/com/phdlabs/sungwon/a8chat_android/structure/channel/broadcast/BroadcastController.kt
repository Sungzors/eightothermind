package com.phdlabs.sungwon.a8chat_android.structure.channel.broadcast

import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.Socket
import com.phdlabs.sungwon.a8chat_android.api.utility.GsonHolder
import com.phdlabs.sungwon.a8chat_android.db.channels.ChannelsManager
import com.phdlabs.sungwon.a8chat_android.db.room.RoomManager
import com.phdlabs.sungwon.a8chat_android.db.user.UserManager
import com.phdlabs.sungwon.a8chat_android.model.channel.Comment
import com.phdlabs.sungwon.a8chat_android.model.user.UserRooms
import com.phdlabs.sungwon.a8chat_android.structure.channel.ChannelContract
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import org.json.JSONException
import org.json.JSONObject
import org.slf4j.LoggerFactory

/**
 * Created by JPAM on 4/13/18.
 * Broadcast controller used to manage Socket.io live comments & likes
 * which trigger UI animations & content
 */
class BroadcastController(val mView: ChannelContract.Broadcast.View) : ChannelContract.Broadcast.Controller {

    /*Properties*/
    private var mSocket: Socket
    private var mUserId: Int? = null
    private var mUserRoom: UserRooms? = null
    private val log = LoggerFactory.getLogger(BroadcastController::class.java)


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
    }

    override fun start() {
        RoomManager.instance.enterRoom(mView.getRoomId, { userRooms ->
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

    override fun resume() {
        //TODO
    }

    override fun pause() {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun stop() {
        //Socket io OFF
        socketOff()
        //Api -> Leave Room
        RoomManager.instance.leaveRoom(mView.getRoomId, { userRooms ->
            userRooms?.let {
                mUserRoom = it
            }
        })
    }

    /**
     * Socket.io
     * */
    fun socketOn() {
        //Room
        mSocket.on(Constants.SocketKeys.UPDATE_ROOM, onUpdateRoom)
        //Live Comments
        mSocket.on(Constants.SocketKeys.COMMENT, onNewComment)
        //Live Likes
        mSocket.on(Constants.SocketKeys.LIKE, onNewLike)
        //Deleted Message
        //Error
        mSocket.on(Constants.SocketKeys.ON_ERROR, onError)
    }

    fun socketOff() {
        //TODO: Socket off for comments
    }

    /**
     * Socket.io callback functions
     * */
    /*Update Room*/
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

    /*On new comment*/
    private val onNewComment = Emitter.Listener { args ->
        mView.getActivity.runOnUiThread({
            val data: JSONObject = args[0] as JSONObject
            val comment = GsonHolder.Companion.instance.get()!!.fromJson(data.toString(), Comment::class.java)
            //TODO: Show comment on feed FOR BROADCASTER
        })
    }

    /*On new Like*/
    private val onNewLike = Emitter.Listener { args ->
        mView.getActivity.runOnUiThread({
            val data: JSONObject = args[0] as JSONObject
            log.debug("Like Object received in Broadcast: $data")
            //TODO: Start like animations FOR BROADCASTER
        })
    }

    /*On Error*/
    private val onError = Emitter.Listener { args ->
        mView.getActivity.runOnUiThread {
            //Show Error if it's not empty
            if (!args[0].toString().equals("{}")) {
                mView.showError(args[0].toString())
            }
        }
    }

    /*Like Post for Audience in Broadcast*/
    override fun likePost(messageId: Int, unlike: Boolean) {
        ChannelsManager.instance.likeUnlikePost(messageId, true)

    }

    /*Comment Post for Audience in Broadcast*/
    override fun commentPost(messageId: String, comment: String) {
        ChannelsManager.instance.commentOnChannelsPost(messageId, comment, { response ->
            response.second?.let {
                //Error
                mView.showError(it)
            }
        })
    }

}