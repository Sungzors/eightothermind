package com.phdlabs.sungwon.a8chat_android.structure.createnew.searchChannels

import android.os.Bundle
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.model.channel.Channel
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreFragment
import com.phdlabs.sungwon.a8chat_android.structure.createnew.CreateNewActivity
import com.phdlabs.sungwon.a8chat_android.structure.createnew.CreateNewContract
import com.phdlabs.sungwon.a8chat_android.utility.adapter.BaseRecyclerAdapter
import com.phdlabs.sungwon.a8chat_android.utility.adapter.BaseViewHolder
import com.phdlabs.sungwon.a8chat_android.utility.adapter.ViewMap
import com.phdlabs.sungwon.a8chat_android.utility.camera.CircleTransform
import com.squareup.picasso.Picasso
import com.vicpin.krealmextensions.save
import kotlinx.android.synthetic.main.fragment_createnew_channels_search.*

/**
 * Created by JPAM on 3/12/18.
 */
class ChannelSearchFragment : CoreFragment(), CreateNewContract.ChannelSearch.View {

    /*Controller*/
    override lateinit var controller: CreateNewContract.ChannelSearch.Controller

    /*Layout*/
    override fun layoutId(): Int = R.layout.fragment_createnew_channels_search

    /*Properties*/
    override lateinit var getAct: CreateNewActivity
    private var mChannelList = mutableListOf<Channel>()
    private lateinit var mChannelAdapter: BaseRecyclerAdapter<Channel, BaseViewHolder>

    init {
        ChannelSearchFragController(this)
    }

    /*LifeCycle*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getAct = activity as CreateNewActivity
    }

    override fun onStart() {
        super.onStart()
        controller.start()
        //Setup Adapter
        setupChannelAdapter()
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

    /*Channels*/
    private fun setupChannelAdapter() {
        mChannelAdapter = object : BaseRecyclerAdapter<Channel, BaseViewHolder>() {

            override fun onBindItemViewHolder(viewHolder: BaseViewHolder?, data: Channel?, position: Int, type: Int) {
                /*UI*/
                bindChannel(viewHolder, data)
            }

            override fun viewHolder(inflater: LayoutInflater?, parent: ViewGroup?, type: Int): BaseViewHolder {
                return object : BaseViewHolder(R.layout.view_eight_channel_card, inflater!!, parent) {
                    override fun addClicks(views: ViewMap?) {
                        //Open Channel
                        views?.click {
                            openChannel(getItem(adapterPosition))
                        }
                        super.addClicks(views)
                    }
                }
            }

        }
        mChannelAdapter.setItems(mChannelList)
        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        fcncs_channels_recycler.layoutManager = layoutManager
        fcncs_channels_recycler.adapter = mChannelAdapter
    }

    //Bind Recycler's Channels
    private fun bindChannel(viewHolder: BaseViewHolder?, data: Channel?) {
        val channelImage = viewHolder?.get<ImageView>(R.id.vech_channel_image)
        val channelName = viewHolder?.get<TextView>(R.id.vech_channel_name)
        viewHolder?.let {
            context?.let {
                /*Load profile picture*/
                Picasso.with(it)
                        .load(data?.avatar)
                        .resize(70, 70)
                        .centerCrop()
                        .placeholder(R.drawable.addphoto)
                        .transform(CircleTransform())
                        .into(channelImage)
                channelName?.text = data?.name
            }
        }
    }

    /*Selected channel intent*/
    private fun openChannel(channel: Channel) {
        //Save to Realm
        channel.save()
        //Transition to Open Channel (Room has been cached on success)
        channel.room_id?.let {
            controller.pullChannelRoom(it, { success ->
                if (success) {
                    getAct.controller.openChannel(channel.id, channel.name, channel.room_id, channel.user_creator_id)
                }
            })

        }
    }

    override fun updateChannelRecycler(channels: MutableList<Channel>?) {
        channels?.let {
            if (it.count() > 0) {
                mChannelAdapter.clear()
                mChannelList.clear()
                mChannelList.addAll(it)
                mChannelAdapter.setItems(mChannelList)
                mChannelAdapter.notifyDataSetChanged()
            }
        }
    }

}