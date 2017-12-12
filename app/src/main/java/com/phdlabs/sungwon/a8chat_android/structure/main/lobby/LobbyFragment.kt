package com.phdlabs.sungwon.a8chat_android.structure.main.lobby

import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.api.event.Event
import com.phdlabs.sungwon.a8chat_android.api.response.ChannelArrayResponse
import com.phdlabs.sungwon.a8chat_android.api.rest.Rest
import com.phdlabs.sungwon.a8chat_android.api.utility.Callback8
import com.phdlabs.sungwon.a8chat_android.db.EventBusManager
import com.phdlabs.sungwon.a8chat_android.model.Channel
import com.phdlabs.sungwon.a8chat_android.model.EventsEight
import com.phdlabs.sungwon.a8chat_android.model.Room
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreFragment
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import com.phdlabs.sungwon.a8chat_android.utility.Preferences
import com.phdlabs.sungwon.a8chat_android.utility.adapter.BaseRecyclerAdapter
import com.phdlabs.sungwon.a8chat_android.utility.adapter.BaseViewHolder
import com.phdlabs.sungwon.a8chat_android.utility.camera.CircleTransform
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_lobby.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by SungWon on 10/17/2017.
 */
class LobbyFragment: CoreFragment(), LobbyContract.View {

    override lateinit var controller: LobbyContract.Controller
    private val mChannels = mutableListOf<Channel>()

    private lateinit var mAdapterMyChannel: BaseRecyclerAdapter<Channel, BaseViewHolder>
    private lateinit var mAdapterEvent: BaseRecyclerAdapter<EventsEight, BaseViewHolder>
    private lateinit var mAdapterFollow: BaseRecyclerAdapter<Channel, BaseViewHolder>
    private lateinit var mAdapterChannel: BaseRecyclerAdapter<Channel, BaseViewHolder>
    private lateinit var mAdapterChat: BaseRecyclerAdapter<Room, BaseViewHolder>

    override fun layoutId() = R.layout.fragment_lobby

    companion object {
        fun newInstance(): LobbyFragment = LobbyFragment()
    }

    override fun onStart() {
        super.onStart()
        LobbyController(this)
        controller.start()
        val call = Rest.getInstance().caller.getChannel(Preferences(coreActivity.context).getPreferenceString(Constants.PrefKeys.TOKEN_KEY))
        val events = EventBusManager.instance().mDataEventBus
        call.enqueue(object: Callback8<ChannelArrayResponse, Event>(events){
            override fun onSuccess(data: ChannelArrayResponse?) {
                mChannels.addAll(data!!.channels!!)
                setUpMyChannelRecycler()
            }
        })
//        setUpChannelRecycler()
//        setUpChannelsFollowedRecycler()
//        setUpChatRecycler()
//        setUpEventsRecycler()
//        setUpMyChannelRecycler()
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

    override fun setUpMyChannelRecycler() {
        mAdapterMyChannel = object: BaseRecyclerAdapter<Channel, BaseViewHolder>(){
            override fun onBindItemViewHolder(viewHolder: BaseViewHolder?, data: Channel?, position: Int, type: Int) {
                bindMyChannelViewHolder(viewHolder!!, data!!)
            }

            override fun viewHolder(inflater: LayoutInflater?, parent: ViewGroup?, type: Int): BaseViewHolder {
                return object : BaseViewHolder(R.layout.card_view_lobby_channel, inflater!!, parent){

                }
            }
        }
        mAdapterMyChannel.setItems(mChannels)//TODO: fix to controller.getMyChannel()
        fl_my_channels_title.visibility = TextView.VISIBLE
        fl_my_channels_recycler.visibility = RecyclerView.VISIBLE
        fl_my_channels_recycler.layoutManager = LinearLayoutManager(coreActivity.context, LinearLayoutManager.HORIZONTAL, false)
        fl_my_channels_recycler.adapter = mAdapterMyChannel
    }

    private fun bindMyChannelViewHolder(viewHolder: BaseViewHolder, data: Channel){
        val bg = viewHolder.get<ImageView>(R.id.cvlc_background_unread)
        val profilePic = viewHolder.get<ImageView>(R.id.cvlc_picture_profile)
        val channelName = viewHolder.get<TextView>(R.id.cvlc_name_channel)
        Picasso.with(coreActivity.context).load(R.drawable.bg_circle_orange_lobby).into(bg)
        Picasso.with(coreActivity.context).load(data.avatar).placeholder(R.drawable.addphoto).transform(CircleTransform()).into(profilePic)
        channelName.text = data.name
    }

    override fun setUpEventsRecycler() {
        mAdapterEvent = object : BaseRecyclerAdapter<EventsEight, BaseViewHolder>(){
            override fun onBindItemViewHolder(viewHolder: BaseViewHolder?, data: EventsEight?, position: Int, type: Int) {
                bindEventViewHolder(viewHolder!!, data!!)
            }

            override fun viewHolder(inflater: LayoutInflater?, parent: ViewGroup?, type: Int): BaseViewHolder {
                return object : BaseViewHolder(R.layout.card_view_lobby_event, inflater!!, parent){

                }
            }
        }
        mAdapterEvent.setItems(controller.getEvents())
        fl_events_recycler.visibility = RecyclerView.VISIBLE
        fl_events_recycler.layoutManager = LinearLayoutManager(coreActivity.context)
        fl_events_recycler.adapter = mAdapterEvent
    }

    private fun bindEventViewHolder(viewHolder: BaseViewHolder, data: EventsEight){
        val eventPic = viewHolder.get<ImageView>(R.id.cvle_picture_event)
        val eventIndicator = viewHolder.get<ImageView>(R.id.cvle_read_indicator)
        val title = viewHolder.get<TextView>(R.id.cvle_title)
        val message = viewHolder.get<TextView>(R.id.cvle_message)
        val time = viewHolder.get<TextView>(R.id.cvle_time)
        //TODO: populate pic once tomer gets that shit done
        title.text = data.name
        message.text = data.distribution //TODO: no subtext?
        if(!data.isRead){
            eventIndicator.visibility = ImageView.VISIBLE
        } else {
            eventIndicator.visibility = ImageView.INVISIBLE
        }
    }

    override fun setUpChannelsFollowedRecycler() {
        mAdapterFollow = object: BaseRecyclerAdapter<Channel, BaseViewHolder>(){
            override fun onBindItemViewHolder(viewHolder: BaseViewHolder?, data: Channel?, position: Int, type: Int) {
                when(data!!.isRead){
                    true -> bindReadChannelViewHolder(viewHolder!!, data)
                    false -> bindUnreadChannelViewHolder(viewHolder!!, data)
                }
            }

            override fun viewHolder(inflater: LayoutInflater?, parent: ViewGroup?, type: Int): BaseViewHolder {
                return object : BaseViewHolder(R.layout.card_view_lobby_channel, inflater!!, parent){

                }
            }
        }
        mAdapterFollow.setItems(controller.getChannelsFollowed())
        fl_follow_recycler.visibility = RecyclerView.VISIBLE
        fl_follow_recycler.layoutManager = LinearLayoutManager(coreActivity.context, LinearLayoutManager.HORIZONTAL, false)
        fl_follow_recycler.adapter = mAdapterFollow
    }

    override fun setUpChannelRecycler() {
        mAdapterChannel = object: BaseRecyclerAdapter<Channel, BaseViewHolder>(){
            override fun onBindItemViewHolder(viewHolder: BaseViewHolder?, data: Channel?, position: Int, type: Int) {
                when(data!!.isRead){
                    true -> bindReadChannelViewHolder(viewHolder!!, data)
                    false -> bindUnreadChannelViewHolder(viewHolder!!, data)
                }
            }

            override fun viewHolder(inflater: LayoutInflater?, parent: ViewGroup?, type: Int): BaseViewHolder {
                return object : BaseViewHolder(R.layout.card_view_lobby_channel, inflater!!, parent){

                }
            }
        }
        mAdapterChannel.setItems(controller.getChannel())
        fl_channels_recycler.visibility = RecyclerView.VISIBLE
        fl_channels_recycler.layoutManager = LinearLayoutManager(coreActivity.context, LinearLayoutManager.HORIZONTAL, false)
        fl_channels_recycler.adapter = mAdapterChannel
    }

    private fun bindReadChannelViewHolder(viewHolder: BaseViewHolder, data: Channel){
        val bg = viewHolder.get<ImageView>(R.id.cvlc_background_unread)
        val profilePic = viewHolder.get<ImageView>(R.id.cvlc_picture_profile)
        val channelName = viewHolder.get<TextView>(R.id.cvlc_name_channel)
        bg.setBackgroundColor(ContextCompat.getColor(coreActivity.context, R.color.white))//TODO: set this to shadowed bg
        //TODO: set prof pic after tomer makes the thing
        channelName.text = data.name
    }

    private fun bindUnreadChannelViewHolder(viewHolder: BaseViewHolder, data: Channel){
        val bg = viewHolder.get<ImageView>(R.id.cvlc_background_unread)
        val profilePic = viewHolder.get<ImageView>(R.id.cvlc_picture_profile)
        val channelName = viewHolder.get<TextView>(R.id.cvlc_name_channel)
        //TODO: set this to picture bg
        //TODO: set prof pic after tomer makes the thing
        channelName.text = data.name
    }

    override fun setUpChatRecycler() {
        mAdapterChat = object: BaseRecyclerAdapter<Room, BaseViewHolder>(){
            override fun onBindItemViewHolder(viewHolder: BaseViewHolder?, data: Room?, position: Int, type: Int) {
                bindRoomViewHolder(viewHolder!!, data!!)
            }

            override fun viewHolder(inflater: LayoutInflater?, parent: ViewGroup?, type: Int): BaseViewHolder {
                return object : BaseViewHolder(R.layout.card_view_lobby_event, inflater!!, parent){

                }
            }
        }
        mAdapterChat.setItems(controller.getChat())
        fl_chat_recycler.visibility = RecyclerView.VISIBLE
        fl_chat_recycler.layoutManager = LinearLayoutManager(coreActivity.context)
        fl_chat_recycler.adapter = mAdapterChat
    }

    private fun bindRoomViewHolder(viewHolder: BaseViewHolder, data: Room){
        val eventPic = viewHolder.get<ImageView>(R.id.cvle_picture_event)
        val eventIndicator = viewHolder.get<ImageView>(R.id.cvle_read_indicator)
        val title = viewHolder.get<TextView>(R.id.cvle_title)
        val message = viewHolder.get<TextView>(R.id.cvle_message)
        val time = viewHolder.get<TextView>(R.id.cvle_time)
        //TODO: populate pic once tomer gets that shit done
        title.text = data.users!!.first_name + " " + data.users!!.last_name
        message.text = data.subRooms!!.message.message
        if(!data.isRead){
            eventIndicator.visibility = ImageView.VISIBLE
        } else {
            eventIndicator.visibility = ImageView.INVISIBLE
        }
        if(Date().time.minus(data.subRooms!!.message.createdAt!!.time)>=86400000){
            time.text = SimpleDateFormat("EEE").format(data.subRooms!!.message.createdAt)
        } else {
            time.text = SimpleDateFormat("h:mm aaa").format(data.subRooms!!.message.createdAt)
        }
    }

    override fun updateMyChannelRecycler() {
        mAdapterMyChannel.clear()
        mAdapterMyChannel.setItems(controller.getMyChannel())
        mAdapterMyChannel.notifyDataSetChanged()
    }

    override fun updateEventsRecycler() {
        mAdapterEvent.clear()
        mAdapterEvent.setItems(controller.getEvents())
        mAdapterEvent.notifyDataSetChanged()
    }

    override fun updateChannelsFollowedRecycler() {
        mAdapterFollow.clear()
        mAdapterFollow.setItems(controller.getChannelsFollowed())
        mAdapterFollow.notifyDataSetChanged()
    }

    override fun updateChannelRecycler() {
        mAdapterChannel.clear()
        mAdapterChannel.setItems(controller.getChannel())
        mAdapterChannel.notifyDataSetChanged()
    }

    override fun updateChatRecycler() {
        mAdapterChat.clear()
        mAdapterChat.setItems(controller.getChat())
        mAdapterChat.notifyDataSetChanged()
    }
}