package com.phdlabs.sungwon.a8chat_android.structure.camera.share.fragments

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.db.channels.ChannelsManager
import com.phdlabs.sungwon.a8chat_android.model.channel.Channel
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreFragment
import com.phdlabs.sungwon.a8chat_android.utility.adapter.BaseRecyclerAdapter
import com.phdlabs.sungwon.a8chat_android.utility.adapter.BaseViewHolder
import com.phdlabs.sungwon.a8chat_android.utility.camera.CircleTransform
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_share_to_channel.*

/**
 * Created by JPAM on 3/26/18.
 * This fragment manages Channels & Events for sharing Camera media
 */
class ShareToChannelFragment : CoreFragment() {

    /*Layout*/
    override fun layoutId(): Int = R.layout.fragment_share_to_channel

    /*Properties*/
    private lateinit var mAdapterMyChannel: BaseRecyclerAdapter<Channel, BaseViewHolder>
    var mSelectedChannelList: MutableList<Channel> = mutableListOf()


    override fun onStart() {
        super.onStart()
        //Query Channels
        ChannelsManager.instance.queryMyChannels()?.let {
            setUpMyChannelRecycler(it.toMutableList())
        }
        //Clear selected channels
        mSelectedChannelList.clear()
    }

    /*My Channels*/
    private fun setUpMyChannelRecycler(myChannels: MutableList<Channel>) {
        mAdapterMyChannel = object : BaseRecyclerAdapter<Channel, BaseViewHolder>() {
            override fun onBindItemViewHolder(viewHolder: BaseViewHolder?, data: Channel?, position: Int, type: Int) {
                bindMyChannelViewHolder(viewHolder!!, data!!)
            }

            override fun viewHolder(inflater: LayoutInflater?, parent: ViewGroup?, type: Int): BaseViewHolder {
                return object : BaseViewHolder(R.layout.card_view_lobby_my_channel, inflater!!, parent) {
                }
            }
        }
        mAdapterMyChannel.setItems(myChannels)
        fstc_my_channels_title.visibility = TextView.VISIBLE
        fstc_my_channels_recycler.visibility = RecyclerView.VISIBLE
        fstc_my_channels_recycler.layoutManager = LinearLayoutManager(coreActivity.context, LinearLayoutManager.HORIZONTAL, false)
        fstc_my_channels_recycler.adapter = mAdapterMyChannel
    }

    private fun bindMyChannelViewHolder(viewHolder: BaseViewHolder, data: Channel) {
        val profilePic = viewHolder.get<ImageView>(R.id.cvlc_picture_profile)
        val channelName = viewHolder.get<TextView>(R.id.cvlc_name_channel)
        val unreadChannelIndicator = viewHolder.get<ImageView>(R.id.cvlc_background_selected)
        //Unread indicator
        data.unread_messages?.let {
            if (it) {
                unreadChannelIndicator.visibility = View.VISIBLE
            } else {
                unreadChannelIndicator.visibility = View.GONE
            }
        }
        Picasso.with(coreActivity.context).load(data.avatar).placeholder(R.drawable.ic_launcher_round).transform(CircleTransform()).into(profilePic)
        channelName.text = data.name
        //Select channels for sharing data
        profilePic.setOnClickListener {
            if (unreadChannelIndicator.visibility == View.GONE) { //Select
                unreadChannelIndicator.visibility = View.VISIBLE
                if (!mSelectedChannelList.contains(data)) {
                    mSelectedChannelList.add(data)
                }
            } else {
                unreadChannelIndicator.visibility = View.GONE
                if (mSelectedChannelList.contains(data)) { //Un-select
                    mSelectedChannelList.remove(data)
                }
            }
        }
    }

    /*Filter*/
    fun filterChannelsAdapter(p0: String?) {
        //UI
        p0?.let {
            //Filter Adapter
            mAdapterMyChannel?.setFilter { filter ->
                filter?.name?.toLowerCase()?.startsWith(p0.toLowerCase(), false)
            }
        }
    }

}