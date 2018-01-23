package com.phdlabs.sungwon.a8chat_android.structure.channel.postshow

import android.os.Bundle
import android.widget.Toast
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.db.TemporaryManager
import com.phdlabs.sungwon.a8chat_android.db.UserManager
import com.phdlabs.sungwon.a8chat_android.model.Channel
import com.phdlabs.sungwon.a8chat_android.model.Comment
import com.phdlabs.sungwon.a8chat_android.model.MediaDetailNest
import com.phdlabs.sungwon.a8chat_android.model.Message
import com.phdlabs.sungwon.a8chat_android.model.user.User
import com.phdlabs.sungwon.a8chat_android.structure.channel.ChannelContract
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_channel_post_show.*

/**
 * Created by SungWon on 12/4/2017.
 * Updated by JPAM 12/27/2017
 */
class ChannelPostShowActivity: CoreActivity(), ChannelContract.PostShow.View{
    override lateinit var controller: ChannelContract.PostShow.Controller

    override fun layoutId(): Int = R.layout.activity_channel_post_show

    override fun contentContainerId(): Int = 0

    private lateinit var mChannelId: String
    private var mChannel: Channel? = null
    private var mMessageId: String = "11"
    private var mMessage: Message? = Message.Builder("11", "media", "1", "4").build()
    private var mComments = mutableListOf<Comment>()
    private var mUser: User? = null

    init {
        mMessage!!.mediaArray.add(MediaDetailNest("https://s3.amazonaws.com/eight-testing123/1512695725084.PNG", "https://s3.amazonaws.com/eight-testing123/1512695725084.PNG"))
        mMessage!!.message = "duck1"
        mMessage!!.likes = 2
        mMessage!!.comments = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*Get current user*/
        UserManager.instance.getCurrentUser { success, user, _ ->
            if(success) {
                user?.let {
                    mUser = it
                }
            }else {
                //TODO: Error handling -> Send user to LogIn screen
            }
        }
    }

    override fun onStart() {
        super.onStart()
        mMessageId = intent.getStringExtra(Constants.IntentKeys.MESSAGE_ID)
        if(TemporaryManager.instance.getMessage(mMessageId)!= null){
            mMessage = TemporaryManager.instance.getMessage(mMessageId)
        }
//        mMessageId = "11"
//        mMessage = Message.Builder("11", "media", "1", "4").build()
//        mMessage!!.mediaArray.add(MediaDetailNest("https://s3.amazonaws.com/eight-testing123/1512695725084.PNG", "https://s3.amazonaws.com/eight-testing123/1512695725084.PNG"))
//        mMessage!!.message = "duck1"
//        mMessage!!.likes = 2
//        mMessage!!.comments = 0
        ChannelPostShowController(this)
        controller.start()
        showBackArrow(R.drawable.ic_back)
//        if(mChannel != null){
            setUpViews()
            setUpClickers()
//        } else {
//            Toast.makeText(this, "Channel Post cannot be found", Toast.LENGTH_SHORT).show()
//        }
    }

    override fun onResume() {
        super.onResume()
        controller.resume()
    }

    override fun onPause() {
        super.onPause()
        controller.pause()
    }

    override fun onStop() {
        super.onStop()
        controller.stop()
    }

    private fun setUpViews(){
        Picasso.with(this).load(mMessage!!.mediaArray[0].media_file).into(acps_post_pic)
        acps_post_text.text = mMessage!!.message
        acps_like_count.text = mMessage!!.likes.toString()
        acps_comment_count.text = mMessage!!.comments.toString()
        setToolbarTitle("TestChannel") //TODO: set to channel name
    }

    private fun setUpClickers(){
        acps_like_button.setOnClickListener {
            /*User safe type for liking post*/
            mUser?.let {
                controller.likePost(mMessageId, it.id.toString())
            }
        }
        acps_comment_button.setOnClickListener {
            controller.commentPost("")
            Toast.makeText(this, "To be implemented", Toast.LENGTH_SHORT).show()
        }
    }

    override fun setUpRecycler(){

    }

    override fun onLike(){
        mMessage!!.likes = mMessage!!.likes!! + 1
        acps_like_count.text = mMessage!!.likes!!.toString()
        acps_like_button.isEnabled = false
    }

    override val getChannelId: String
    get() = ""
//        get() = mChannelId
    override val getMessageId: String
        get() = mMessageId
}