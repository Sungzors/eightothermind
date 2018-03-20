package com.phdlabs.sungwon.a8chat_android.structure.channel.postshow

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.model.channel.Comment
import com.phdlabs.sungwon.a8chat_android.model.media.Media
import com.phdlabs.sungwon.a8chat_android.model.message.Message
import com.phdlabs.sungwon.a8chat_android.structure.application.Application
import com.phdlabs.sungwon.a8chat_android.structure.channel.ChannelContract
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import com.phdlabs.sungwon.a8chat_android.utility.adapter.BaseRecyclerAdapter
import com.phdlabs.sungwon.a8chat_android.utility.adapter.BaseViewHolder
import com.phdlabs.sungwon.a8chat_android.utility.camera.CircleTransform
import com.squareup.picasso.Picasso
import com.vicpin.krealmextensions.queryFirst
import com.vicpin.krealmextensions.save
import cz.intik.overflowindicator.SimpleSnapHelper
import kotlinx.android.synthetic.main.activity_channel_post_show.*

/**
 * Created by SungWon on 12/4/2017.
 * Updated by JPAM 12/27/2017
 */
class ChannelPostShowActivity : CoreActivity(), ChannelContract.PostShow.View {

    /*Controller*/
    override lateinit var controller: ChannelContract.PostShow.Controller

    /*Layout*/
    override fun layoutId(): Int = R.layout.activity_channel_post_show

    override fun contentContainerId(): Int = 0

    /*Properties*/
    private var mChannelName = ""
    private var mMessageId: Int = 0
    private var mMessage: Message? = Message()
    private var mIncludesMedia = false

    /*Computed Properties*/
    override val get8Application: Application
        get() = application as Application
    override val getActivity: ChannelPostShowActivity = this

    /*Adapters*/
    private var mMediaRecyclerAdapter: BaseRecyclerAdapter<Media, BaseViewHolder>? = null
    private var mCommentRecyclerAdapter: BaseRecyclerAdapter<Comment, BaseViewHolder>? = null

    /*LifeCycle*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Controller
        ChannelPostShowController(this)
        //Post Info
        getIntentInfo()
        //UI
        showBackArrow(R.drawable.ic_back)
        setupMediaRecycler()
        setupCommentsRecycler()
        setUpClickers()
    }

    override fun onStart() {
        super.onStart()
        controller.start()
    }

    override fun onResume() {
        super.onResume()
        controller.resume()
    }

    override fun onPause() {
        super.onPause()
        controller.pause()
        mMessage?.save()
    }

    override fun onStop() {
        super.onStop()
        controller.stop()
        setResult(Activity.RESULT_OK)
    }

    /*Intent Information*/
    private fun getIntentInfo() {
        //Retrieve Message
        mMessageId = intent.getIntExtra(Constants.IntentKeys.MESSAGE_ID, 0)
        if (mMessageId != 0) {
            mMessage = Message().queryFirst { equalTo("id", mMessageId) }
            controller.pullPostComments(mMessageId)
        }
        mChannelName = intent.getStringExtra(Constants.IntentKeys.CHANNEL_NAME) ?: ""
        //Includes Media or Only Message?
        mIncludesMedia = intent.getBooleanExtra(Constants.IntentKeys.INCLUDES_MEDIA, false)
        setToolbarTitle(mChannelName)
    }

    /*UI*/
    private fun setupMediaRecycler() {
        //Post Media
        mMediaRecyclerAdapter = object : BaseRecyclerAdapter<Media, BaseViewHolder>() {
            override fun onBindItemViewHolder(viewHolder: BaseViewHolder?, data: Media?, position: Int, type: Int) {
                val imageView = viewHolder?.get<ImageView>(R.id.vmfpi_imageView)
                Picasso.with(context)
                        .load(data?.media_file)
                        .placeholder(R.drawable.addphoto)
                        .resize(250, 250)
                        .centerInside()
                        .into(imageView)

            }

            override fun viewHolder(inflater: LayoutInflater?, parent: ViewGroup?, type: Int): BaseViewHolder =
                    BaseViewHolder(R.layout.view_multiple_media_fullpost_item, inflater!!, parent)

        }
        mMediaRecyclerAdapter?.setItems(mMessage?.mediaArray)
        acps_post_media_recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        acps_post_media_recyclerView.adapter = mMediaRecyclerAdapter
        //Pager Indicator
        acps_post_media_recyclerView.onFlingListener = null
        acps_view_pager_indicator.attachToRecyclerView(acps_post_media_recyclerView)
        val simpleSnapHelper = SimpleSnapHelper(acps_view_pager_indicator)
        simpleSnapHelper.attachToRecyclerView(acps_post_media_recyclerView)
        //Post Message
        acps_post_text.text = mMessage?.message ?: ""
        acps_message.text = mMessage?.message ?: ""
        //Post Count
        acps_comment_count.text = mMessage?.comments.toString()
        //Comment Count
        acps_like_count.text = mMessage?.likes.toString()
        //UI Control -> Post with || without media
        mMessage?.mediaArray?.let {
            if (it.count() > 0) {
                //Media
                acps_message.visibility = View.GONE
                acps_post_media_recyclerView.visibility = View.VISIBLE
                acps_view_pager_indicator.visibility = View.VISIBLE
                acps_post_text.visibility = View.VISIBLE
            } else {
                //Only message
                acps_message.visibility = View.VISIBLE
                acps_post_media_recyclerView.visibility = View.GONE
                acps_view_pager_indicator.visibility = View.GONE
                acps_post_text.visibility = View.GONE
            }
        }

    }

    private fun setupCommentsRecycler() {
        mCommentRecyclerAdapter = object : BaseRecyclerAdapter<Comment, BaseViewHolder>() {
            override fun onBindItemViewHolder(viewHolder: BaseViewHolder?, data: Comment?, position: Int, type: Int) {
                val profileImage = viewHolder?.get<ImageView>(R.id.vpc_friend_profile_picture)
                val profileName = viewHolder?.get<TextView>(R.id.vpc_friend_name)
                val comment = viewHolder?.get<TextView>(R.id.vpc_commment)
                //Load Commenter Information
                data?.user?.let {
                    Picasso.with(context)
                            .load(it.avatar)
                            .placeholder(R.drawable.addphoto)
                            .resize(45, 45)
                            .centerCrop()
                            .placeholder(R.drawable.addphoto)
                            .transform(CircleTransform())
                            .into(profileImage)
                    it.hasFullName().let {
                        if (it.first) {
                            it.second?.let {
                                profileName?.text = it
                            }
                        } else {
                            profileName?.text = data.user!!.first_name ?: ""
                        }
                    }
                }
                //Load Comment
                data?.comment?.let {
                    comment?.text = it
                }
            }

            override fun viewHolder(inflater: LayoutInflater?, parent: ViewGroup?, type: Int): BaseViewHolder =
                    BaseViewHolder(R.layout.view_post_comment, inflater!!, parent)
        }
        mCommentRecyclerAdapter?.setItems(controller.getPostComments())
        acps_comment_recycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)
        acps_comment_recycler.adapter = mCommentRecyclerAdapter
    }

    override fun updateCommentsRecycler() {
        mCommentRecyclerAdapter?.clear()
        mCommentRecyclerAdapter?.setItems(controller.getPostComments())
        mCommentRecyclerAdapter?.notifyDataSetChanged()
        acps_comment_count.text = controller.getPostComments().count().toString()
    }

    private fun setUpClickers() {

        /*Like*/
        var liked = false
        mMessage?.userLiked?.let {
            if (it) {
                //Liked
                acps_like_button.background = getDrawable(R.drawable.ic_like)
                liked = true
            } else {
                //Not Liked
                acps_like_button.background = getDrawable(R.drawable.like_empty)
                liked = false
            }
        }
        //Like Post
        acps_like_button.setOnClickListener {
            if (liked) {
                controller.likePost(mMessage?.id!!, true)
                acps_like_button.background = getDrawable(R.drawable.like_empty)
                mMessage?.likes?.let {
                    acps_like_count.text = (acps_like_count.text.toString().toInt() - 1).toString()
                    mMessage?.likes = acps_like_count.text.toString().toInt()
                }
                liked = false
                mMessage?.userLiked = liked
                mMessage?.save()
            } else {
                controller.likePost(mMessage?.id!!, false)
                acps_like_count.text = (acps_like_count.text.toString().toInt() + 1).toString()
                acps_like_button.background = getDrawable(R.drawable.ic_like)
                mMessage?.likes = acps_like_count.text.toString().toInt()
                liked = true
                mMessage?.userLiked = liked
                mMessage?.save()

            }
            mMessage?.save()
        }


        /*Comment*/
        acps_comment_button.setOnClickListener {
            //Validate post comment
            isValidPost { comment ->
                if (comment.isBlank()) {
                    Toast.makeText(context, "Empty comment", Toast.LENGTH_SHORT).show()
                } else {
                    //Create comment
                    controller.commentPost(mMessageId.toString(), comment)
                    //Post Count
                    var commentCount = mMessage?.comments.toString().toInt()
                    commentCount += 1
                    acps_comment_count.text = commentCount.toString()
                }
            }
        }
    }

    /**
     * [isValidPost]
     * Validates & Returns the comment message to be posted
     * @callback Comment on Post
     * */
    private fun isValidPost(callback: (String) -> Unit) {
        val alertDialog = AlertDialog.Builder(context)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.view_dialog_comment, null)
        alertDialog.setView(dialogLayout)
        val commentEditText = dialogLayout.findViewById<EditText>(R.id.vdc_comment)

        commentEditText.minLines = 2
        commentEditText.setPadding(10, 0, 10, 0)
        alertDialog.setTitle("Comment")
        alertDialog.setPositiveButton("publish") { p0, p1 ->
            val comment = commentEditText.text.toString()
            callback(comment)
        }
        alertDialog.setNegativeButton("cancel") { p0, p1 ->
            callback("")
        }
        alertDialog.show()
    }

    override val getChannelId: Int?
        get() = 0
    //        get() = mChannelId
    override val getMessageId: Int?
        get() = mMessageId
}