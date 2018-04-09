package com.phdlabs.sungwon.a8chat_android.structure.channel.mychannel

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.webkit.MimeTypeMap
import android.widget.*
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.db.channels.ChannelsManager
import com.phdlabs.sungwon.a8chat_android.db.user.UserManager
import com.phdlabs.sungwon.a8chat_android.model.channel.Channel
import com.phdlabs.sungwon.a8chat_android.model.media.Media
import com.phdlabs.sungwon.a8chat_android.model.message.Message
import com.phdlabs.sungwon.a8chat_android.structure.application.Application
import com.phdlabs.sungwon.a8chat_android.structure.channel.ChannelContract
import com.phdlabs.sungwon.a8chat_android.structure.channel.createPost.CreatePostActivity
import com.phdlabs.sungwon.a8chat_android.structure.channel.postshow.ChannelPostShowActivity
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import com.phdlabs.sungwon.a8chat_android.structure.setting.channel.ChannelSettingsActivity
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import com.phdlabs.sungwon.a8chat_android.utility.SuffixDetector
import com.phdlabs.sungwon.a8chat_android.utility.adapter.BaseRecyclerAdapter
import com.phdlabs.sungwon.a8chat_android.utility.adapter.BaseViewHolder
import com.phdlabs.sungwon.a8chat_android.utility.adapter.ViewMap
import com.phdlabs.sungwon.a8chat_android.utility.camera.CameraControl
import com.phdlabs.sungwon.a8chat_android.utility.camera.CircleTransform
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.squareup.picasso.Picasso
import com.vicpin.krealmextensions.queryFirst
import com.vicpin.krealmextensions.save
import cz.intik.overflowindicator.OverflowPagerIndicator
import cz.intik.overflowindicator.SimpleSnapHelper
import io.realm.RealmList
import kotlinx.android.synthetic.main.activity_channel_my.*
import kotlinx.android.synthetic.main.toolbar.*
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
    private lateinit var mFollowedChannelsAdapter: BaseRecyclerAdapter<Channel, BaseViewHolder>
    /*Content Adapter*/
    private lateinit var mContentAdapter: BaseRecyclerAdapter<Message, BaseViewHolder>
    /*Post Media Adapter*/
    private lateinit var mPostMediaAdapter: BaseRecyclerAdapter<Media, BaseViewHolder>
    /*Files*/
    private lateinit var fileListerDialog: FileListerDialog

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
        setupToolbar()
        setupFollowedChannelsRecycler()
        setupContentRecycler()
        //File sharing
        fileListerDialog = FileListerDialog.createFileListerDialog(this)
        fileListerDialog.setOnFileSelectedListener { file, path ->
            //File Permissions
            if (ContextCompat.checkSelfPermission(this, Constants.AppPermissions.READ_EXTERNAL) ==
                    PackageManager.PERMISSION_GRANTED) {
                controller.sendFile(file, path)
            } else {
                controller.requestReadingExternalStorage()
            }
        }
        //Controller
        controller.onCreate()
    }

    private fun setupToolbar() {
        showBackArrow(R.drawable.ic_back)
        setToolbarTitle(mChannelName)
        toolbar_right_picture.visibility = View.VISIBLE
        //Channel Picture -> Access to settings
        Picasso.with(this)
                .load(ChannelsManager.instance.querySingleChannel(mChannelId)?.avatar)
                .placeholder(R.drawable.addphoto)
                .transform(CircleTransform())
                .into(toolbar_right_picture)


        //Access to Channel Settings
        toolbar_right_picture.setOnClickListener {
            //Chat Name
            val intent = Intent(context, ChannelSettingsActivity::class.java)
            intent.putExtra(Constants.IntentKeys.CHANNEL_NAME, mChannelName)
            intent.putExtra(Constants.IntentKeys.CHANNEL_ID, mChannelId)
            intent.putExtra(Constants.IntentKeys.ROOM_ID, mRoomId)
            intent.putExtra(Constants.IntentKeys.OWNER_ID, mOwnerId)
            startActivityForResult(intent, Constants.RequestCodes.CHANNEL_SETTINGS)
        }
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
        controller.start()
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
        overridePendingTransition(0, 0)
    }

    /*FOLLOWED CHANNELS*/
    private fun setupFollowedChannelsRecycler() {
        mFollowedChannelsAdapter = object : BaseRecyclerAdapter<Channel, BaseViewHolder>() {
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
                            intent.putExtra(Constants.IntentKeys.CHANNEL_ID, channel?.id)
                            intent.putExtra(Constants.IntentKeys.CHANNEL_NAME, channel?.name)
                            intent.putExtra(Constants.IntentKeys.ROOM_ID, channel?.room_id?.toInt())
                            intent.putExtra(Constants.IntentKeys.OWNER_ID, channel?.user_creator_id?.toInt())
                            startActivity(intent)
                            overridePendingTransition(0, 0)
                            this@MyChannelActivity.finish()
                        }
                        super.addClicks(views)
                    }
                }
            }
        }
        mFollowedChannelsAdapter.setItems(controller.getFollowedChannels())
        acm_fav_channel_recycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        acm_fav_channel_recycler.adapter = mFollowedChannelsAdapter
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
                /*Message Post*/
                    3 -> {
                        bindMessagePostViewHolder(viewHolder!!, data!!)
                    }
                /*Sharing Files*/
                    4 -> {
                        bindSharedFiledViewHolder(viewHolder!!, data!!)
                    }
                }
            }

            override fun getItemType(t: Message?): Int {

                //Check for files
                t?.files?.let {
                    if (it.count() > 0) {
                        return 4
                    }
                }

                //Check for media
                t?.mediaArray?.let {
                    if (it.count() > 0) {
                        //Media Post || Media
                        return if (t.post!!) {
                            //Media Post
                            2
                        } else {
                            //Media Only
                            1
                        }
                    } else {
                        t.let {
                            return if (t.post!!) {
                                //Message Post
                                3
                            } else {
                                //Message
                                0
                            }
                        }
                    }
                } ?: run {
                    t?.let {
                        t.post?.let {
                            return if (it) {
                                3
                            } else {
                                0
                            }
                        }
                    }
                }
                return 0 //Message as default
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
                /*Message Post*/
                    3 -> {
                        return object : BaseViewHolder(R.layout.card_view_post_message_no_media, inflater!!, parent) {

                        }
                    }
                /*Files Post*/
                    4 -> {
                        return object : BaseViewHolder(R.layout.card_view_files, inflater!!, parent) {

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

    /*Post W/Media*/
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
        val commentCount = viewHolder.get<TextView>(R.id.cvpmnm_comment_count)
        //Load info
        picasso.load(data.user!!.avatar).transform(CircleTransform()).into(posterPic)
        posterName.text = data.getUserName()
        //Date
        val formatter = SimpleDateFormat("EEE - h:mm aaa")
        postDate.text = formatter.format(data.createdAt)
        var liked = false
        data.userLiked?.let {
            if (it) {
                //Liked
                likeButton.background = getDrawable(R.drawable.ic_like)
                liked = true
            } else {
                //Not Liked
                likeButton.background = getDrawable(R.drawable.like_empty)
                liked = false
            }
        }
        //Like Post
        likeButton.setOnClickListener {
            if (liked) {
                controller.likePost(data.id!!, true)
                likeButton.background = getDrawable(R.drawable.like_empty)
                data.likes?.let {
                    if (it > 0) {
                        likeCount.text = (likeCount.text.toString().toInt() - 1).toString()
                        data.likes = likeCount.text.toString().toInt()
                    }
                }
                liked = false
                data.userLiked = liked
                data.save()
            } else {
                controller.likePost(data.id!!, false)
                likeCount.text = (likeCount.text.toString().toInt() + 1).toString()
                data.likes = likeCount.text.toString().toInt()
                likeButton.background = getDrawable(R.drawable.ic_like)
                liked = true
                data.userLiked = liked
                data.save()
            }
        }
        //Transition to Full Post View to see the comments
        commentButton.setOnClickListener {
            data.save()
            //Transition to Post Show Activity
            val intent = Intent(context, ChannelPostShowActivity::class.java)
            intent.putExtra(Constants.IntentKeys.MESSAGE_ID, data.id)
            intent.putExtra(Constants.IntentKeys.CHANNEL_NAME, mChannelName)
            intent.putExtra(Constants.IntentKeys.INCLUDES_MEDIA, true)
            startActivityForResult(intent, Constants.RequestCodes.VIEW_POST_REQ_CODE)
        }
        postText.text = data.message
        likeCount.text = data.likes.toString()
        commentCount.text = data.comments.toString()
    }

    /*Post W/Message*/
    private fun bindMessagePostViewHolder(viewHolder: BaseViewHolder, data: Message) {
        //Post Owner
        val picasso = Picasso.with(this)
        val posterPic = viewHolder.get<ImageView>(R.id.cvpmnm_poster_pic)
        val posterName = viewHolder.get<TextView>(R.id.cvpmnm_poster_name)
        val postDate = viewHolder.get<TextView>(R.id.cvpmnm_post_date)
        //Content
        val postText = viewHolder.get<TextView>(R.id.cvpmnm_post_text)
        //Actions
        val likeButton = viewHolder.get<ImageView>(R.id.cvpmnm_like_button)
        val commentButton = viewHolder.get<ImageView>(R.id.cvpmnm_comment_button)
        val likeCount = viewHolder.get<TextView>(R.id.cvpmnm_like_count)
        val commentCount = viewHolder.get<TextView>(R.id.cvpmnm_comment_count)

        //Load Info
        data.user?.avatar?.let {
            picasso.load(it).transform(CircleTransform()).into(posterPic)
        }
        posterName.text = data.getUserName()

        //Date
        val formatter = SimpleDateFormat("EEE - h:mm aaa")
        postDate.text = formatter.format(data.createdAt)

        var liked = false
        data.userLiked?.let {
            if (it) {
                //Liked
                likeButton.background = getDrawable(R.drawable.ic_like)
                liked = true
            } else {
                //Not Liked
                likeButton.background = getDrawable(R.drawable.like_empty)
                liked = false
            }
        }
        //Like Post
        likeButton.setOnClickListener {
            if (liked) {
                controller.likePost(data.id!!, true)
                likeButton.background = getDrawable(R.drawable.like_empty)
                data.likes?.let {
                    if (it > 0) {
                        likeCount.text = (likeCount.text.toString().toInt() - 1).toString()
                        data.likes = likeCount.text.toString().toInt()
                    }
                }
                liked = false
                data.userLiked = liked
                data.save()
            } else {
                controller.likePost(data.id!!, false)
                likeCount.text = (likeCount.text.toString().toInt() + 1).toString()
                likeButton.background = getDrawable(R.drawable.ic_like)
                data.likes = likeCount.text.toString().toInt()
                liked = true
                data.userLiked = liked
                data.save()
            }
        }

        //Transition to Full Post View to see the comments
        commentButton.setOnClickListener {
            data.save()
            //Transition to Post Show Activity
            val intent = Intent(context, ChannelPostShowActivity::class.java)
            intent.putExtra(Constants.IntentKeys.MESSAGE_ID, data.id)
            intent.putExtra(Constants.IntentKeys.CHANNEL_NAME, mChannelName)
            intent.putExtra(Constants.IntentKeys.INCLUDES_MEDIA, false)
            startActivityForResult(intent, Constants.RequestCodes.VIEW_POST_REQ_CODE)
        }
        //Post
        postText.text = data.message
        data.likes?.let {
            likeCount.text = it.toString()
        } ?: run {
            likeCount.text = "0"
        }
        data.comments?.let {
            commentCount.text = it.toString()
        } ?: run {
            commentCount.text = "0"
        }
    }

    /*File Sharing*/
    fun bindSharedFiledViewHolder(viewHolder: BaseViewHolder, data: Message) {
        //Post Owner
        val picasso = Picasso.with(this)
        val posterPic = viewHolder.get<ImageView>(R.id.cvf_poster_pic)
        val posterName = viewHolder.get<TextView>(R.id.cvf_poster_name)
        val postDate = viewHolder.get<TextView>(R.id.cvf_post_date)
        val fileName = viewHolder.get<TextView>(R.id.cvf_file_name)
        val container = viewHolder.get<LinearLayout>(R.id.cvf_file_container)
        //Load Info
        data.user?.avatar?.let {
            picasso.load(it).transform(CircleTransform()).into(posterPic)
        }
        posterName.text = data.getUserName()

        //Date
        val formatter = SimpleDateFormat("EEE - h:mm aaa")
        postDate.text = formatter.format(data.createdAt)
        //File
        data.files?.let {
            if (it.count() > 0) {
                fileName.text = it[0]?.file_string ?: "n/a"
            }
        }
        //Open File
        container.setOnClickListener {
            println("TAPPED FILE: " + fileName.text)
            val mime = MimeTypeMap.getSingleton()
            val intent = Intent(Intent.ACTION_VIEW)
            val mimeType = mime.getMimeTypeFromExtension(SuffixDetector.instance.getFileSuffix(data.files?.get(0)?.file_string.toString()))
            intent.setDataAndType(Uri.parse(data.files?.get(0)?.s3_url), mimeType)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            try {
                context?.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(this, "Can't open this type of File", Toast.LENGTH_SHORT).show()
            }
        }
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
                            //Save || Update @see Realm
                            val messageQuery = Message().queryFirst { equalTo("id", message.id) }
                            if (messageQuery == null) {
                                message.save()
                            }
                            //Transition to Post Show Activity
                            val intent = Intent(context, ChannelPostShowActivity::class.java)
                            intent.putExtra(Constants.IntentKeys.MESSAGE_ID, message.id)
                            intent.putExtra(Constants.IntentKeys.CHANNEL_NAME, mChannelName)
                            intent.putExtra(Constants.IntentKeys.INCLUDES_MEDIA, true)
                            startActivityForResult(intent, Constants.RequestCodes.VIEW_POST_REQ_CODE)
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
        mFollowedChannelsAdapter.clear()
        mFollowedChannelsAdapter.setItems(controller.getFollowedChannels())
        mFollowedChannelsAdapter.notifyDataSetChanged()
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
            if (controller.checkSelfPermissions()) {
                //Not a contact
                val alertDialog = AlertDialog.Builder(this)
                alertDialog.setTitle("Would you like to start a live broadcast on $mChannelName?")
                alertDialog.setPositiveButton("OK") { _, _ ->
                    //TODO: create broadcast post
                    //TODO: Transition to video broadcasting
                }
                alertDialog.setNegativeButton("Cancel") { _, _ -> /*Dismiss*/ }
                alertDialog.show()
            }
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
                    Constants.RequestCodes.CREATE_NEW_POST_REQ_CODE)
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
        else if (requestCode == Constants.RequestCodes.CREATE_NEW_POST_REQ_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                //Push new post after socket is connected
                val filepathArrayList = data?.extras?.getStringArrayList(Constants.IntentKeys.MEDIA_POST)
                val postMessage = data?.extras?.getString(Constants.IntentKeys.MEDIA_POST_MESSAGE)
                controller.createPost(postMessage, filepathArrayList)
                controller.keepSocketConnection(false)
            }
        }

        //Back from viewing a post
        else if (requestCode == Constants.RequestCodes.VIEW_POST_REQ_CODE) {
            if (resultCode == Activity.RESULT_OK || resultCode == Activity.RESULT_CANCELED) {
                controller.keepSocketConnection(false)
                controller.retrieveChatHistory(true)
            }
        }

        //Back from Channel Settings
        else if (requestCode == Constants.RequestCodes.CHANNEL_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                data?.getBooleanExtra(Constants.IntentKeys.CHANNEL_DELETED, false)?.let {
                    if (it) {
                        onBackPressed()
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (!controller.permissionResults(requestCode, permissions, grantResults)) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
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