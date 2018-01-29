package com.phdlabs.sungwon.a8chat_android.structure.channel.mychannel

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.db.TemporaryManager
import com.phdlabs.sungwon.a8chat_android.model.Message
import com.phdlabs.sungwon.a8chat_android.structure.application.Application
import com.phdlabs.sungwon.a8chat_android.structure.channel.ChannelContract
import com.phdlabs.sungwon.a8chat_android.structure.channel.postshow.ChannelPostShowActivity
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import com.phdlabs.sungwon.a8chat_android.utility.adapter.BaseRecyclerAdapter
import com.phdlabs.sungwon.a8chat_android.utility.adapter.BaseViewHolder
import com.phdlabs.sungwon.a8chat_android.utility.camera.CameraControl
import com.phdlabs.sungwon.a8chat_android.utility.camera.CircleTransform
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_channel_my.*
import java.text.SimpleDateFormat

/**
 * Created by SungWon on 12/20/2017.
 */

//TODO: Refactor Channel & Room data
class MyChannelActivity: CoreActivity(), ChannelContract.MyChannel.View{

    override lateinit var controller: ChannelContract.MyChannel.Controller

    override fun layoutId(): Int = R.layout.activity_channel_my

    override fun contentContainerId(): Int = 0

    private lateinit var mChannelId: String
    private lateinit var mChannelName: String
    private var mRoomId: Int = 0

    private lateinit var mAdapter: BaseRecyclerAdapter<Message, BaseViewHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MyChannelController(this)
        mChannelId = intent.getStringExtra(Constants.IntentKeys.CHANNEL_ID)
        mChannelName = intent.getStringExtra(Constants.IntentKeys.CHANNEL_NAME)
        mRoomId = intent.getIntExtra(Constants.IntentKeys.ROOM_ID, 0)
        showBackArrow(R.drawable.ic_back)
        setToolbarTitle(mChannelName)
        setUpRecycler()
        controller.start()
//        controller.createChannelRoom()

    }

    override fun onStart() {
        super.onStart()
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

    private fun setUpRecycler() {
        mAdapter = object : BaseRecyclerAdapter<Message, BaseViewHolder>(){
            override fun onBindItemViewHolder(viewHolder: BaseViewHolder?, data: Message?, position: Int, type: Int) {
                when(type){
                    0 -> {
                        bindMessageViewHolder(viewHolder!!, data!!)
                    }
                    1 -> {
                        bindMediaViewHolder(viewHolder!!, data!!)
                    }
                    2 -> {
                        bindPostViewHolder(viewHolder!!, data!!)
                    }
                }
            }

            override fun getItemType(t: Message?): Int {
                if(t!!.mediaArray == null){
                    return 0
                } else if(t.message == null){
                    return 1
                } else {
                    return 2
                }
            }

            override fun viewHolder(inflater: LayoutInflater?, parent: ViewGroup?, type: Int): BaseViewHolder {
                when(type){
                    0 -> {
                        return object : BaseViewHolder(R.layout.card_view_post_string, inflater!!, parent){

                        }
                    }
                    1 -> {
                        return object : BaseViewHolder(R.layout.card_view_post_photo, inflater!!, parent){

                        }
                    }
                    2 -> {
                        return object : BaseViewHolder(R.layout.card_view_post_media, inflater!!, parent){

                        }
                    }
                }
                return object :BaseViewHolder(R.layout.card_view_post_media, inflater!!, parent){

                }
            }
        }
        mAdapter.setItems(controller.getMessages())
        val layoutManager = LinearLayoutManager(this)
        acm_post_recycler.layoutManager = layoutManager
        acm_post_recycler.adapter = mAdapter
    }
    private fun bindMessageViewHolder(viewHolder: BaseViewHolder, data: Message){
        val pic = viewHolder.get<ImageView>(R.id.cvps_poster_pic)
        val text = viewHolder.get<TextView>(R.id.cvps_post_text)
        val posterName = viewHolder.get<TextView>(R.id.cvps_poster_name)
        val postDate = viewHolder.get<TextView>(R.id.cvps_post_date)

        Picasso.with(this).load(data.user!!.avatar).transform(CircleTransform()).into(pic)
        text.text = data.message
        posterName.text = data.name
        val formatter = SimpleDateFormat("EEE - h:mm aaa")
        postDate.text = formatter.format(data.createdAt)
    }

    private fun bindMediaViewHolder(viewHolder: BaseViewHolder, data: Message){
        val pic = viewHolder.get<ImageView>(R.id.cvpp_poster_pic)
        val postPic = viewHolder.get<ImageView>(R.id.cvpp_post_pic)
        val posterName = viewHolder.get<TextView>(R.id.cvpp_poster_name)
        val postDate = viewHolder.get<TextView>(R.id.cvpp_post_date)

        Picasso.with(this).load(data.user!!.avatar).transform(CircleTransform()).into(pic)
        Picasso.with(this).load(data.mediaArray[0].media_file).into(postPic)
        posterName.text = data.name
        val formatter = SimpleDateFormat("EEE - h:mm aaa")
        postDate.text = formatter.format(data.createdAt)
    }

    private fun bindPostViewHolder(viewHolder: BaseViewHolder, data: Message){
        val picasso = Picasso.with(this)
        val posterPic = viewHolder.get<ImageView>(R.id.cvpm_poster_pic)
        val posterName = viewHolder.get<TextView>(R.id.cvpm_poster_name)
        val postDate = viewHolder.get<TextView>(R.id.cvpm_post_date)
        val postPic = viewHolder.get<ImageView>(R.id.cvpm_post_pic)
        val likeButton = viewHolder.get<ImageView>(R.id.cvpm_like_button)
        val commentButton = viewHolder.get<ImageView>(R.id.cvpm_comment_button)
        val postText = viewHolder.get<TextView>(R.id.cvpm_post_text)
        val likeCount = viewHolder.get<TextView>(R.id.cvpm_like_count)
        val commentCount = viewHolder.get<TextView>(R.id.cvpm_comment_count)

        picasso.load(data.user!!.avatar).transform(CircleTransform()).into(posterPic)
        posterName.text = data.name
        picasso.load(data.mediaArray[0].media_file).into(postPic)
        val formatter = SimpleDateFormat("EEE - h:mm aaa")
        postDate.text = formatter.format(data.createdAt)
        postPic.setOnClickListener {
            TemporaryManager.instance.mMessageList.clear()
            TemporaryManager.instance.mMessageList.add(data)
            val intent = Intent(this, ChannelPostShowActivity::class.java)
            intent.putExtra(Constants.IntentKeys.MESSAGE_ID, data.id)
            startActivity(intent)
        }
        likeButton.setOnClickListener {
//            controller.likePost(data.id!!)
        }
        commentButton.setOnClickListener {
//            controller.commentPost(data.id!!)
        }
        postText.text = data.message
        likeCount.text = data.likes.toString()
        commentCount.text = data.comments.toString()
    }

    override fun updateRecycler() {
        mAdapter.clear()
        mAdapter.setItems(controller.getMessages())
        mAdapter.notifyDataSetChanged()
    }

    private fun setUpDrawer(){
        acm_the_daddy_drawer.isClipPanel = false
        acm_the_daddy_drawer.panelHeight = 150
        acm_the_daddy_drawer.coveredFadeColor = ContextCompat.getColor(this, R.color.transparent)
    }

    private fun setUpClickers(){
        acm_emitting_button_of_green_arrow.setOnClickListener({
            controller.sendMessage()
        })
        acm_conjuring_conduit_of_messages.setOnClickListener {
            val layoutManager = acm_post_recycler.layoutManager as LinearLayoutManager
            layoutManager.scrollToPositionWithOffset(controller.getMessages().size -1, 0)
            acm_conjuring_conduit_of_messages.isFocusableInTouchMode = true

            acm_conjuring_conduit_of_messages.post {
                acm_conjuring_conduit_of_messages.requestFocus()
                val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(acm_conjuring_conduit_of_messages, InputMethodManager.SHOW_IMPLICIT)
            }
        }
        acm_conjuring_conduit_of_messages.setOnFocusChangeListener { view, b ->
            if(!b){
                acm_conjuring_conduit_of_messages.isFocusableInTouchMode = false
                val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(acm_conjuring_conduit_of_messages.windowToken, 0)
            }
        }
        acm_drawer_summoner.setOnClickListener {
            if(acm_the_daddy_drawer.panelState == SlidingUpPanelLayout.PanelState.COLLAPSED){
                acm_the_daddy_drawer.panelState = SlidingUpPanelLayout.PanelState.EXPANDED
            } else {
                acm_the_daddy_drawer.panelState == SlidingUpPanelLayout.PanelState.COLLAPSED
            }
        }
        acm_drawer_broadcast.setOnClickListener {

        }
        acm_drawer_file.setOnClickListener {

        }
        acm_drawer_media.setOnClickListener {
            CameraControl.instance.pickImage(this,
                    "Choose a media",
                    CameraControl.instance.requestCode(),
                    false)
        }
        acm_drawer_post.setOnClickListener {
            CameraControl.instance.pickImage(this,
                    "Choose a media",
                    345,
                    false)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == CameraControl.instance.requestCode()){
            controller.onPictureOnlyResult(requestCode, resultCode, data)
        } else if (requestCode == 345){
            controller.onPicturePostResult(requestCode, resultCode, data)
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
        get() = mChannelId.toInt()
}