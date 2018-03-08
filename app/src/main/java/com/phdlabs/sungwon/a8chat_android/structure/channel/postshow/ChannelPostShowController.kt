package com.phdlabs.sungwon.a8chat_android.structure.channel.postshow

import android.widget.Toast
import com.phdlabs.sungwon.a8chat_android.db.channels.ChannelsManager
import com.phdlabs.sungwon.a8chat_android.model.channel.Comment
import com.phdlabs.sungwon.a8chat_android.structure.channel.ChannelContract

/**
 * Created by SungWon on 12/4/2017.
 * Updated by JPAM on 03/07/2018
 */
class ChannelPostShowController(val mView: ChannelContract.PostShow.View) : ChannelContract.PostShow.Controller {

    /*Properties*/
    private var mComments: ArrayList<Comment> = arrayListOf()

    /*Init*/
    init {
        mView.controller = this
    }

    /*LifeCycle*/
    override fun start() {
    }

    override fun resume() {
    }

    override fun pause() {
    }

    override fun stop() {
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

    /*Like the Post*/
    override fun likePost(messageId: Int?) {
//        UserManager.instance.getCurrentUser { success, user, token ->
//            if (success) {
//                val call = mCaller.likePost(token?.token, mView.getMessageId!!, user?.id!!)
//                call.enqueue(object : Callback8<ErrorResponse, PostLikeEvent>(mEventBus) {
//                    override fun onSuccess(data: ErrorResponse?) {
//                        mView.onLike()
//                        Toast.makeText(mView.getContext()!!, "Post Liked!", Toast.LENGTH_SHORT).show()
//                    }
//
//                    override fun onError(response: Response<ErrorResponse>?) {
//                        Toast.makeText(mView.getContext()!!, "You have already liked this post!", Toast.LENGTH_SHORT).show()
//                    }
//                })
//            }
//        }
    }

    override fun commentPost(messageId: String) {
        Toast.makeText(mView.getContext()!!, "To be implemented", Toast.LENGTH_SHORT).show()
    }

}