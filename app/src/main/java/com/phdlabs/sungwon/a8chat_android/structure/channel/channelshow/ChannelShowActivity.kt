package com.phdlabs.sungwon.a8chat_android.structure.channel.channelshow

import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.model.ChannelShowNest
import com.phdlabs.sungwon.a8chat_android.model.Message
import com.phdlabs.sungwon.a8chat_android.structure.channel.ChannelContract
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import com.phdlabs.sungwon.a8chat_android.utility.adapter.BaseRecyclerAdapter
import com.phdlabs.sungwon.a8chat_android.utility.adapter.BaseViewHolder
import com.phdlabs.sungwon.a8chat_android.utility.adapter.ViewMap
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_channel.*
import java.text.SimpleDateFormat

/**
 * Created by SungWon on 12/12/2017.
 */
class ChannelShowActivity: CoreActivity(), ChannelContract.ChannelShow.View{
    override lateinit var controller: ChannelContract.ChannelShow.Controller

    override fun layoutId(): Int = R.layout.activity_channel

    override fun contentContainerId(): Int = 0

    private lateinit var mChannelAdapter: BaseRecyclerAdapter<ChannelShowNest, BaseViewHolder>
    private lateinit var mPostAdapter: BaseRecyclerAdapter<Message, BaseViewHolder>

    private val mChannelList = mutableListOf<ChannelShowNest>()
    private val mPostList = mutableListOf<Message>()

    override fun onStart() {
        super.onStart()
        ChannelShowController(this)
        controller.start()
        showBackArrow(R.drawable.ic_back, true)
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

    override fun setUpTopRecycler(){
        mChannelAdapter = object : BaseRecyclerAdapter<ChannelShowNest, BaseViewHolder>(){
            override fun onBindItemViewHolder(viewHolder: BaseViewHolder?, data: ChannelShowNest?, position: Int, type: Int) {
                bindTopViewHolder(viewHolder!!, data!!)
            }

            override fun viewHolder(inflater: LayoutInflater?, parent: ViewGroup?, type: Int): BaseViewHolder {
                return object: BaseViewHolder(R.layout.card_view_channel_list, inflater!!, parent){
                    override fun addClicks(views: ViewMap?) {
                        views!!.click({ view ->
                            val a = getItem(adapterPosition)
                            controller.loadChannel(getItem(adapterPosition).channels[0].room_id!!)
                        })
                    }
                }
            }
        }
        mChannelAdapter.setItems(mChannelList)
        ac_channel_list_recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        ac_channel_list_recycler.adapter = mChannelAdapter
    }

    

    private fun bindTopViewHolder(viewHolder: BaseViewHolder, data: ChannelShowNest){
        val pic = viewHolder.get<ImageView>(R.id.cvcl_channel_pic)
        val text = viewHolder.get<TextView>(R.id.cvcl_channel_text)
        Picasso.with(this).load(data.channels[0].avatar).placeholder(R.drawable.addphoto).into(pic)
        text.text = data.channels[0].name
    }

    override fun addToChannels(channels: Array<ChannelShowNest>) {
        mChannelList.clear()
        mChannelList.addAll(channels)
    }

    override fun addToPosts(list: Array<Message>) {
        mPostList.clear()
        mPostList.addAll(list)
    }

    override fun setUpPostRecycler() {
        mPostAdapter = object : BaseRecyclerAdapter<Message, BaseViewHolder>(){
            override fun onBindItemViewHolder(viewHolder: BaseViewHolder?, data: Message?, position: Int, type: Int) {
                bindPostHolder(viewHolder!!, data!!)
            }

            override fun viewHolder(inflater: LayoutInflater?, parent: ViewGroup?, type: Int): BaseViewHolder {
                return object : BaseViewHolder(R.layout.card_view_post_media, inflater!!, parent){

                }
            }
        }
        mPostAdapter.setItems(mPostList)
        ac_post_list.layoutManager = LinearLayoutManager(this)
        ac_post_list.adapter = mPostAdapter
    }

    private fun bindPostHolder(viewHolder: BaseViewHolder, data: Message){
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

        picasso.load(data.user!!.avatar).into(posterPic)
        posterName.text = data.name
        if(data.mediaArray != null){
            picasso.load(data.mediaArray[0].media_file).into(postPic)
        }
        val formatter = SimpleDateFormat("EEE - h:mm aaa")
        postDate.text = formatter.format(data.createdAt)
        likeButton.setOnClickListener {
            controller.likePost(data.id!!)
        }
        commentButton.setOnClickListener {
            controller.commentPost(data.id!!)
        }
        postText.text = data.message
        likeCount.text = data.likes.toString()
        commentCount.text = data.comments.toString()
    }

    override fun onLike(messageId: String) {
        for (message in mPostList){
            if(message.id == messageId){
                message.likes = message.likes!! + 1
            }
        }
    }
}