package com.phdlabs.sungwon.a8chat_android.structure.channel.postshow

import android.widget.Toast
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.Socket
import com.phdlabs.sungwon.a8chat_android.api.data.CommentPostData
import com.phdlabs.sungwon.a8chat_android.api.utility.GsonHolder
import com.phdlabs.sungwon.a8chat_android.db.channels.ChannelsManager
import com.phdlabs.sungwon.a8chat_android.db.user.UserManager
import com.phdlabs.sungwon.a8chat_android.model.channel.Comment
import com.phdlabs.sungwon.a8chat_android.model.message.Message
import com.phdlabs.sungwon.a8chat_android.structure.channel.ChannelContract
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import org.json.JSONObject

/**
 * Created by SungWon on 12/4/2017.
 * Updated by JPAM on 03/07/2018
 */
class ChannelPostShowController(val mView: ChannelContract.PostShow.View) : ChannelContract.PostShow.Controller {

    /*Properties*/
    private var mSocket: Socket
    private var mComments: ArrayList<Comment> = arrayListOf()

    /*Init*/
    init {
        mView.controller = this
        mSocket = mView.get8Application.getSocket()
    }

    /*LifeCycle*/
    override fun start() {
        //Emmit socket room connectivity
        UserManager.instance.getCurrentUser { success, user, _ ->
            if (success) {
                mSocket.emit("connect-rooms", user?.id, "channel")
            }
        }
    }

    override fun resume() {
        //Sockets ON
        mSocket.on(Constants.SocketKeys.COMMENT, onCommentUpdate)
        mSocket.on(Constants.SocketKeys.EDIT_COMMENT, onCommentUpdate)
    }

    override fun pause() {
        //Sockets Off
        mSocket.off(Constants.SocketKeys.COMMENT)
        mSocket.off(Constants.SocketKeys.EDIT_COMMENT)
    }

    override fun stop() {
    }

    private val onCommentUpdate = Emitter.Listener { args ->
        mView.getActivity.runOnUiThread({
            val data: JSONObject = args[0] as JSONObject
            val comment = GsonHolder.Companion.instance.get()!!.fromJson(data.toString(), Comment::class.java)
            if (mComments.count() > 0) {
                if (mComments[0].id != comment.id) {
                    mComments.add(0, comment)
                }
            }
            mView.updateCommentsRecycler()
            println("Received: " + data)
        })
    }

    /*Post Comments*/
    override fun pullPostComments(messageId: Int) {
        mView.showProgress()
        ChannelsManager.instance.getPostComments(messageId, { response ->
            response.second?.let {
                // Error
                mView.hideProgress()
                mView.showError(it)
            } ?: run {
                mView.hideProgress()
                response.first?.let {
                    mComments.clear()
                    it.forEach { element -> mComments.add(element) }
                    mView.updateCommentsRecycler()
                }
            }
        })
    }

    override fun getPostComments(): ArrayList<Comment> = mComments

    override fun commentPost(messageId: String, comment: String) {
        ChannelsManager.instance.commentOnChannelsPost(messageId, comment, { response ->
            response.second?.let {
                //Error
                mView.showError(it)
            }
        })
    }

    /*Like Post*/
    override fun likePost(messageId: Int, unlike: Boolean) {
        ChannelsManager.instance.likeUnlikePost(messageId, unlike)
    }


}