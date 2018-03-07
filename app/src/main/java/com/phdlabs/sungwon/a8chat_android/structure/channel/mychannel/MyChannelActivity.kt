package com.phdlabs.sungwon.a8chat_android.structure.channel.mychannel

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.db.TemporaryManager
import com.phdlabs.sungwon.a8chat_android.db.user.UserManager
import com.phdlabs.sungwon.a8chat_android.model.channel.Channel
import com.phdlabs.sungwon.a8chat_android.model.media.Media
import com.phdlabs.sungwon.a8chat_android.model.message.Message
import com.phdlabs.sungwon.a8chat_android.structure.application.Application
import com.phdlabs.sungwon.a8chat_android.structure.channel.ChannelContract
import com.phdlabs.sungwon.a8chat_android.structure.channel.createPost.CreatePostActivity
import com.phdlabs.sungwon.a8chat_android.structure.channel.postshow.ChannelPostShowActivity
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import com.phdlabs.sungwon.a8chat_android.utility.adapter.BaseRecyclerAdapter
import com.phdlabs.sungwon.a8chat_android.utility.adapter.BaseViewHolder
import com.phdlabs.sungwon.a8chat_android.utility.adapter.ViewMap
import com.phdlabs.sungwon.a8chat_android.utility.camera.CameraControl
import com.phdlabs.sungwon.a8chat_android.utility.camera.CircleTransform
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.squareup.picasso.Picasso
import cz.intik.overflowindicator.OverflowPagerIndicator
import cz.intik.overflowindicator.SimpleSnapHelper
import io.realm.RealmList
import kotlinx.android.synthetic.main.activity_channel_my.*
import yogesh.firzen.filelister.FileListerDialog
import java.text.SimpleDateFormat

/**
 * Created by SungWon on 12/20/2017.
 * Updated by JPAM on 03/01/2018
 */

class MyChannelActivity : CoreActivity(), ChannelContract.MyChannel.View {

    /*Controller*/
    override lateinit var controller: ChannelContract.MyChannel.Controller

    /*Layout*/
    override fun layoutId(): Int = R.layout.activity_channel_my

    override fun contentContainerId(): Int = 0

    /*Properties*/
    private var mChannelId: Int = 0
    private lateinit var mChannelName: String
    private var mRoomId: Int = 0
    private var mOwnerId = 0

    /*Followed channels adapter*/
    private lateinit var mFollowedChanellsAdapter: BaseRecyclerAdapter<Channel, BaseViewHolder>
    /*Content Adapter*/
    private lateinit var mContentAdapter: BaseRecyclerAdapter<Message, BaseViewHolder>
    /*Post Media Adapter*/
    private lateinit var mPostMediaAdapter: BaseRecyclerAdapter<Media, BaseViewHolder>
    /*Files*/
    lateinit var fileListerDialog: FileListerDialog

    /*LifeCycle*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Controller
        MyChannelController(this)
        //Intent
        mChannelId = intent.getIntExtra(Constants.IntentKeys.CHANNEL_ID, 0)
        mChannelName = intent.getStringExtra(Constants.IntentKeys.CHANNEL_NAME)
        mRoomId = intent.getIntExtra(Constants.IntentKeys.ROOM_ID, 0)
        mOwnerId = intent.getIntExtra(Constants.IntentKeys.OWNER_ID, 0)
        //UI
        showBackArrow(R.drawable.ic_back)
        setToolbarTitle(mChannelName)
        setupFollowedChannelsRecycler()
        setupContentRecycler()
        //Files
        fileListerDialog = FileListerDialog.createFileListerDialog(this)
        fileListerDialog.setOnFileSelectedListener { file, path ->
            controller.sendFile()
        }
        //Controller
        controller.onCreate()
    }

    override fun onStart() {
        super.onStart()

        /*Show || Hide -> Bottom Drawer*/
        UserManager.instance.getCurrentUser { success, user, _ ->
            if (success) {
                user?.id.let {
                    if (it != mOwnerId) {
                        acm_the_drawer.visibility = LinearLayout.INVISIBLE
                    }
                }
            }
        }
        setUpDrawer()
        setUpClickers()
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

    override fun onDestroy() {
        super.onDestroy()
        controller.destroy()
    }

    /*FOLLOWED CHANNELS*/
    private fun setupFollowedChannelsRecycler() {
        mFollowedChanellsAdapter = object : BaseRecyclerAdapter<Channel, BaseViewHolder>() {
            override fun onBindItemViewHolder(viewHolder: BaseViewHolder?, data: Channel?, position: Int, type: Int) {
                val profilePic = viewHolder?.get<ImageView>(R.id.cvlc_picture_profile)
                val channelName = viewHolder?.get<TextView>(R.id.cvlc_name_channel)
                val unreadChannelIndicator = viewHolder?.get<ImageView>(R.id.cvlc_background_unread)
                //Unread indicator
                data?.unread_messages?.let {
                    if (it) {
                        unreadChannelIndicator?.visibility = View.VISIBLE
                    } else {
                        unreadChannelIndicator?.visibility = View.GONE
                    }
                }
                Picasso.with(context).load(data?.avatar).placeholder(R.drawable.addphoto).transform(CircleTransform()).into(profilePic)
                channelName?.text = data?.name
            }

            override fun viewHolder(inflater: LayoutInflater?, parent: ViewGroup?, type: Int): BaseViewHolder {
                return object : BaseViewHolder(R.layout.card_view_lobby_follow_channel, inflater!!, parent) {
                    override fun addClicks(views: ViewMap?) {
                        views?.click {
                            val channel = getItem(adapterPosition)
                            val intent = Intent(context, MyChannelActivity::class.java)
                            intent.putExtra(Constants.IntentKeys.CHANNEL_ID, channel?.id.toString())
                            intent.putExtra(Constants.IntentKeys.CHANNEL_NAME, channel?.name)
                            intent.putExtra(Constants.IntentKeys.ROOM_ID, channel?.room_id?.toInt())
                            intent.putExtra(Constants.IntentKeys.OWNER_ID, channel?.user_creator_id?.toInt())
                            startActivity(intent)
                        }
                        super.addClicks(views)
                    }
                }
            }
        }
        mFollowedChanellsAdapter.setItems(controller.getFollowedChannels())
        acm_fav_channel_recycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        acm_fav_channel_recycler.adapter = mFollowedChanellsAdapter
    }

    /*POSTS RECYCLER*/
    private fun setupContentRecycler() {
        mContentAdapter = object : BaseRecyclerAdapter<Message, BaseViewHolder>() {
            override fun onBindItemViewHolder(viewHolder: BaseViewHolder?, data: Message?, position: Int, type: Int) {
                when (type) {
                /*Message*/
                    0 -> {
                        bindMessageViewHolder(viewHolder!!, data!!)
                    }
                /*Media*/
                    1 -> {
                        bindMediaViewHolder(viewHolder!!, data!!)
                    }
                /*Post*/
                    2 -> {
                        bindPostViewHolder(viewHolder!!, data!!)
                    }
                }
            }

            override fun getItemType(t: Message?): Int {
                if (t!!.mediaArray == null) {
                    return 0 //Message
                } else if (t.message == null) {
                    return 1 //Media
                } else {
                    if (t.mediaArray?.size!! > 0) {
                        return 2 //Post
                    } else {
                        return 0
                    }
                }
            }

            override fun viewHolder(inflater: LayoutInflater?, parent: ViewGroup?, type: Int): BaseViewHolder {
                when (type) {
                /*Message*/
                    0 -> {
                        return object : BaseViewHolder(R.layout.card_view_post_string, inflater!!, parent) {

                        }
                    }
                /*Media*/
                    1 -> {
                        return object : BaseViewHolder(R.layout.card_view_post_photo, inflater!!, parent) {

                        }
                    }
                /*Post*/
                    2 -> {
                        return object : BaseViewHolder(R.layout.card_view_post_media, inflater!!, parent) {

                        }
                    }
                }
                return object : BaseViewHolder(R.layout.card_view_post_media, inflater!!, parent) {

                }
            }
        }
        mContentAdapter.setItems(controller.getMessages())
        val layoutManager = LinearLayoutManager(this)
        acm_post_recycler.layoutManager = layoutManager
        acm_post_recycler.adapter = mContentAdapter
    }

    /*Message*/
    private fun bindMessageViewHolder(viewHolder: BaseViewHolder, data: Message) {
        val pic = viewHolder.get<ImageView>(R.id.cvps_poster_pic)
        val text = viewHolder.get<TextView>(R.id.cvps_post_text)
        val posterName = viewHolder.get<TextView>(R.id.cvps_poster_name)
        val postDate = viewHolder.get<TextView>(R.id.cvps_post_date)

        Picasso.with(this).load(data.user!!.avatar).transform(CircleTransform()).into(pic)
        text.text = data.message
        posterName.text = data.getUserName()
        val formatter = SimpleDateFormat("EEE - h:mm aaa")
        postDate.text = formatter.format(data.createdAt)
    }

    /*Media*/
    private fun bindMediaViewHolder(viewHolder: BaseViewHolder, data: Message) {
        val pic = viewHolder.get<ImageView>(R.id.cvpp_poster_pic)
        val postPic = viewHolder.get<ImageView>(R.id.cvpp_post_pic)
        val posterName = viewHolder.get<TextView>(R.id.cvpp_poster_name)
        val postDate = viewHolder.get<TextView>(R.id.cvpp_post_date)

        Picasso.with(this).load(data.user!!.avatar).transform(CircleTransform()).into(pic)
        data.mediaArray?.let {
            Picasso.with(this).load(it[0]?.media_file).into(postPic)
        }
        posterName.text = data.getUserName()
        val formatter = SimpleDateFormat("EEE - h:mm aaa")
        postDate.text = formatter.format(data.createdAt)
    }

    /*Post*/
    private fun bindPostViewHolder(viewHolder: BaseViewHolder, data: Message) {
        //Post Owner
        val picasso = Picasso.with(this)
        val posterPic = viewHolder.get<ImageView>(R.id.cvpm_poster_pic)
        val posterName = viewHolder.get<TextView>(R.id.cvpm_poster_name)
        val postDate = viewHolder.get<TextView>(R.id.cvpm_post_date)
        //Media Recycler
        val postPicRecycler = viewHolder.get<RecyclerView>(R.id.cvpm_post_pic_recycler)
        //Page Indicator
        val pageIndicator = viewHolder.get<OverflowPagerIndicator>(R.id.cvpm_view_pager_indicator)
        setupMediaRecycler(data.mediaArray, postPicRecycler, data, pageIndicator)
        //Actions
        val likeButton = viewHolder.get<ImageView>(R.id.cvpm_like_button)
        val commentButton = viewHolder.get<ImageView>(R.id.cvpm_comment_button)
        val postText = viewHolder.get<TextView>(R.id.cvpm_post_text)
        val likeCount = viewHolder.get<TextView>(R.id.cvpm_like_count)
        val commentCount = viewHolder.get<TextView>(R.id.cvpm_comment_count)
        //Load info
        picasso.load(data.user!!.avatar).transform(CircleTransform()).into(posterPic)
        posterName.text = data.getUserName()
        //Date
        val formatter = SimpleDateFormat("EEE - h:mm aaa")
        postDate.text = formatter.format(data.createdAt)

        //Like Post
        likeButton.setOnClickListener {
            //            controller.likePost(data.id!!)
            //TODO
        }
        //Comment on post
        commentButton.setOnClickListener {
            //            controller.commentPost(data.id!!)
            //TODO
        }
        postText.text = data.message
        likeCount.text = data.likes.toString()
        commentCount.text = data.comments.toString()
    }

    //Post Media Recycler (Multiple Images)
    private fun setupMediaRecycler(mediaArray: RealmList<Media>?, recyclerView: RecyclerView, message: Message, pageIndicator: OverflowPagerIndicator) {
        mPostMediaAdapter = object : BaseRecyclerAdapter<Media, BaseViewHolder>() {
            override fun onBindItemViewHolder(viewHolder: BaseViewHolder?, data: Media?, position: Int, type: Int) {
                val imageView = viewHolder?.get<ImageView>(R.id.vmpi_imageView)
                data?.media_file?.let {
                    Picasso.with(context)
                            .load(it)
                            .resize(400, 400)
                            .centerInside()
                            .into(imageView)
                }
            }

            override fun viewHolder(inflater: LayoutInflater?, parent: ViewGroup?, type: Int): BaseViewHolder {
                return object : BaseViewHolder(R.layout.view_multiple_media_post_item, inflater!!, parent) {
                    override fun addClicks(views: ViewMap?) {
                        views?.click {
                            controller.keepSocketConnection(true)
                            TemporaryManager.instance.mMessageList.clear()
                            TemporaryManager.instance.mMessageList.add(message)
                            val intent = Intent(context, ChannelPostShowActivity::class.java)
                            intent.putExtra(Constants.IntentKeys.MESSAGE_ID, message.id)
                            intent.putExtra(Constants.IntentKeys.CHANNEL_NAME, mChannelName)
                            startActivityForResult(intent, Constants.ChannelRequestCodes.VIEW_POST_REQ_CODE)
                        }
                        super.addClicks(views)
                    }
                }
            }
        }
        mPostMediaAdapter.setItems(mediaArray)
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = mPostMediaAdapter
        //Pager indicator
        recyclerView.onFlingListener = null
        pageIndicator.attachToRecyclerView(recyclerView)
        val simpleSnapHelper = SimpleSnapHelper(pageIndicator)
        simpleSnapHelper.attachToRecyclerView(recyclerView)
    }

    /*Update*/
    override fun updateContentRecycler() {
        mContentAdapter.clear()
        mContentAdapter.setItems(controller.getMessages())
        mContentAdapter.notifyDataSetChanged()
        acm_post_recycler.smoothScrollToPosition(0)
    }

    override fun updateFollowedChannelsRecycler() {
        mFollowedChanellsAdapter.clear()
        mFollowedChanellsAdapter.setItems(controller.getFollowedChannels())
        mFollowedChanellsAdapter.notifyDataSetChanged()
        acm_fav_channel_recycler.smoothScrollToPosition(0)
    }


    /*Drawer*/
    private fun setUpDrawer() {
        acm_the_daddy_drawer.isClipPanel = false
        acm_the_daddy_drawer.panelHeight = 150
        acm_the_daddy_drawer.coveredFadeColor = ContextCompat.getColor(this, R.color.transparent)
    }

    /*On Click*/
    private fun setUpClickers() {
        /*Send Message*/
        acm_emitting_button_of_green_arrow.setOnClickListener({
            controller.sendMessage()
        })

        acm_conjuring_conduit_of_messages.setOnClickListener {
            val layoutManager = acm_post_recycler.layoutManager as LinearLayoutManager
            acm_conjuring_conduit_of_messages.isFocusableInTouchMode = true

            acm_conjuring_conduit_of_messages.post {
                acm_conjuring_conduit_of_messages.requestFocus()
                val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(acm_conjuring_conduit_of_messages, InputMethodManager.SHOW_IMPLICIT)
            }
        }

        acm_conjuring_conduit_of_messages.setOnFocusChangeListener { view, b ->
            if (!b) {
                acm_conjuring_conduit_of_messages.isFocusableInTouchMode = false
                val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(acm_conjuring_conduit_of_messages.windowToken, 0)
            }
        }
        acm_drawer_summoner.setOnClickListener {
            if (acm_the_daddy_drawer.panelState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                acm_the_daddy_drawer.panelState = SlidingUpPanelLayout.PanelState.EXPANDED
            } else {
                acm_the_daddy_drawer.panelState == SlidingUpPanelLayout.PanelState.COLLAPSED
            }
        }
        acm_drawer_broadcast.setOnClickListener {

        }
        /*File*/
        acm_drawer_file.setOnClickListener {
            fileListerDialog.show()
        }
        /*Media*/
        acm_drawer_media.setOnClickListener {
            controller.keepSocketConnection(true)
            CameraControl.instance.pickImage(this,
                    "Choose a media",
                    CameraControl.instance.requestCode(),
                    false)
        }
        /*Post*/
        acm_drawer_post.setOnClickListener {
            controller.keepSocketConnection(true)
            val intent = Intent(this, CreatePostActivity::class.java)
            intent.putExtra(Constants.IntentKeys.ROOM_ID, mRoomId)
            startActivityForResult(intent,
                    Constants.ChannelRequestCodes.CREATE_NEW_POST_REQ_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //Close Drawer
        if (acm_the_daddy_drawer.panelState == SlidingUpPanelLayout.PanelState.EXPANDED) {
            acm_the_daddy_drawer.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
        }
        //Picture
        if (requestCode == CameraControl.instance.requestCode()) {
            controller.onPictureOnlyResult(requestCode, resultCode, data)
            controller.keepSocketConnection(false)
        }
        //Back from creating a post
        else if (requestCode == Constants.ChannelRequestCodes.CREATE_NEW_POST_REQ_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                //Push new post after socket is connected
                val filepathArrayList = data?.extras?.getStringArrayList(Constants.IntentKeys.MEDIA_POST)
                val postMessage = data?.extras?.getString(Constants.IntentKeys.MEDIA_POST_MESSAGE)
                controller.createPost(postMessage, filepathArrayList)
                controller.keepSocketConnection(false)
            }
        }
        //Back from viewing a post
        else if (requestCode == Constants.ChannelRequestCodes.VIEW_POST_REQ_CODE) {
            if (resultCode == Activity.RESULT_OK || resultCode == Activity.RESULT_CANCELED) {
                controller.keepSocketConnection(false)
            }
        }
    }

    override val get8Application: Application
        get() = application as Application

    override val getActivity: MyChannelActivity
        get() = this

    override val getMessageET: String
        get() = acm_conjuring_conduit_of_messages.text.toString()

    override val getMessageETObject: EditText
        get() = acm_conjuring_conduit_of_messages

    override val getRoomId: Int
        get() = mRoomId

    override val getChannelId: Int
        get() = mChannelId
}