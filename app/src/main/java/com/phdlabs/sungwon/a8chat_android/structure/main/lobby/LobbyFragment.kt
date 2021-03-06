package com.phdlabs.sungwon.a8chat_android.structure.main.lobby

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.db.EightQueries
import com.phdlabs.sungwon.a8chat_android.model.channel.Channel
import com.phdlabs.sungwon.a8chat_android.model.event.EventsEight
import com.phdlabs.sungwon.a8chat_android.model.room.Room
import com.phdlabs.sungwon.a8chat_android.structure.channel.mychannel.MyChannelActivity
import com.phdlabs.sungwon.a8chat_android.structure.chat.ChatActivity
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreFragment
import com.phdlabs.sungwon.a8chat_android.structure.event.view.EventViewActivity
import com.phdlabs.sungwon.a8chat_android.structure.groupchat.GroupChatActivity
import com.phdlabs.sungwon.a8chat_android.structure.main.LobbyContract
import com.phdlabs.sungwon.a8chat_android.structure.main.MainActivity
import com.phdlabs.sungwon.a8chat_android.structure.main.lobbyOverlay.LobbyOverlayFragment
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import com.phdlabs.sungwon.a8chat_android.utility.adapter.BaseRecyclerAdapter
import com.phdlabs.sungwon.a8chat_android.utility.adapter.BaseViewHolder
import com.phdlabs.sungwon.a8chat_android.utility.adapter.ViewMap
import com.phdlabs.sungwon.a8chat_android.utility.camera.CircleTransform
import com.squareup.picasso.Picasso
import com.vicpin.krealmextensions.queryFirst
import kotlinx.android.synthetic.main.fragment_lobby.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by SungWon on 10/17/2017.
 * Updated by JPAM on 03/09/2018
 */

class LobbyFragment : CoreFragment(), LobbyContract.View {

    /*Controller*/
    override lateinit var controller: LobbyContract.Controller

    /*Adapters*/
    //Channels
    private lateinit var mAdapterChannels: BaseRecyclerAdapter<Channel, BaseViewHolder>
    private var mMyChannelsList: MutableList<Channel> = mutableListOf()
    //Events
    private lateinit var mAdapterEvent: BaseRecyclerAdapter<Pair<Room, EventsEight?>?, BaseViewHolder>
    private var mEventsList: MutableList<Pair<Room, EventsEight?>> = mutableListOf()
    //Private Chats
    private lateinit var mAdapterChat: BaseRecyclerAdapter<Room, BaseViewHolder>
    private var mChatsList: MutableList<Room> = mutableListOf()


    //Channel separator
    private var separatorPosition: Int = -1

    /*Layout*/
    override fun layoutId() = R.layout.fragment_lobby

    /*Companion*/
    companion object {

        /*Global control variable*/
        var refresh: Boolean = true

        fun newInstance(shouldUpdate: Boolean): LobbyFragment {
            this.refresh = shouldUpdate
            return LobbyFragment()
        }
    }

    //Activity
    override fun activity(): MainActivity = activity as MainActivity

    /*LifeCycle*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LobbyController(this, refresh)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        controller.onViewCreated()
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
    }

    override fun onStop() {
        super.onStop()
        controller.stop()
    }

    /**CHANNELS*/
    /*My Channels*/
    override fun setUpChannelRecycler() {
        mAdapterChannels = object : BaseRecyclerAdapter<Channel, BaseViewHolder>() {
            override fun onBindItemViewHolder(viewHolder: BaseViewHolder?, data: Channel?, position: Int, type: Int) {
                bindMyChannelViewHolder(viewHolder!!, data!!)
            }

            override fun viewHolder(inflater: LayoutInflater?, parent: ViewGroup?, type: Int): BaseViewHolder {
                return object : BaseViewHolder(R.layout.card_view_lobby_my_channel, inflater!!, parent) {

                }
            }
        }
        mAdapterChannels.setItems(mMyChannelsList)
        fl_channels_title.visibility = TextView.VISIBLE
        fl_channels_recycler.visibility = RecyclerView.VISIBLE
        fl_channels_recycler.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        fl_channels_recycler.adapter = mAdapterChannels
    }

    private fun bindMyChannelViewHolder(viewHolder: BaseViewHolder, data: Channel) {
        val profilePic = viewHolder.get<ImageView>(R.id.cvlc_picture_profile)
        val channelName = viewHolder.get<TextView>(R.id.cvlc_name_channel)
        val unreadChannelIndicator = viewHolder.get<ImageView>(R.id.cvlc_background_unread)
        val separator = viewHolder.get<ImageView>(R.id.cvlc_separator)
        //Unread indicator
        data.unread_messages?.let {
            if (it) {
                unreadChannelIndicator.background = activity?.getDrawable(R.drawable.bg_circle_blue_lobby)
            } else {
                unreadChannelIndicator.background = activity?.getDrawable(R.drawable.bg_circle_white_lobby)
            }
        }

        Picasso.with(context).load(data.avatar).placeholder(R.mipmap.ic_launcher_round).transform(CircleTransform()).into(profilePic)
        channelName.text = data.name

        profilePic.setOnClickListener {
            val intent = Intent(activity, MyChannelActivity::class.java)
            intent.putExtra(Constants.IntentKeys.CHANNEL_ID, data.id)
            intent.putExtra(Constants.IntentKeys.CHANNEL_NAME, data.name)
            intent.putExtra(Constants.IntentKeys.ROOM_ID, data.room_id?.toInt())
            intent.putExtra(Constants.IntentKeys.OWNER_ID, data.user_creator_id!!.toInt())
            startActivity(intent)
        }

        if (separatorPosition > -1) {
            if (viewHolder.adapterPosition == separatorPosition) separator.visibility = ImageView.VISIBLE else separator.visibility = ImageView.GONE
        }

    }

    /**EVENTS*/
    /*Events*/
    override fun setUpEventsRecycler() {
        mAdapterEvent = object : BaseRecyclerAdapter<Pair<Room, EventsEight?>?, BaseViewHolder>() {
            override fun onBindItemViewHolder(viewHolder: BaseViewHolder?, data: Pair<Room, EventsEight?>?, position: Int, type: Int) {
                bindEventViewHolder(viewHolder!!, data!!)

            }

            override fun viewHolder(inflater: LayoutInflater?, parent: ViewGroup?, type: Int): BaseViewHolder {
                return object : BaseViewHolder(R.layout.card_view_lobby_event, inflater!!, parent) {
                    override fun addClicks(views: ViewMap?) {
                        views!!.click {
                            val data = getItem(adapterPosition)
                            //TODO: Only Pass event id & room id
                            val intent = Intent(context, EventViewActivity::class.java)
                            intent.putExtra(Constants.IntentKeys.EVENT_ID, data?.second?.id)
                            startActivityForResult(intent, Constants.RequestCodes.EVENT_CHAT_REQ_CODE)
                        }
                    }
                }
            }
        }

        mAdapterEvent.setItems(mEventsList)
        fl_events_title.visibility = TextView.VISIBLE
        fl_events_recycler.visibility = RecyclerView.VISIBLE
        fl_events_recycler.layoutManager = LinearLayoutManager(context)
        fl_events_recycler.adapter = mAdapterEvent
    }

    //TODO: make this method more maintainable
    private fun bindEventViewHolder(viewHolder: BaseViewHolder, data: Pair<Room, EventsEight?>) {
        val eventPic = viewHolder.get<ImageView>(R.id.cvle_picture_event)
        val eventIndicator = viewHolder.get<ImageView>(R.id.cvle_read_indicator)
        val title = viewHolder.get<TextView>(R.id.cvle_title)
        val message = viewHolder.get<TextView>(R.id.cvle_message)
        val time = viewHolder.get<TextView>(R.id.cvle_time)
        val event = data.second
        //Room Info
        Picasso.with(context).load(event?.avatar).placeholder(R.mipmap.ic_launcher_round).transform(CircleTransform()).into(eventPic)
        title.text = event?.name
        if (data.first.message != null) {
            when (data.first.message!!.type) {
                "string" -> message.text = data.first.message!!.message
                "media" -> message.text = "Picture posted"
                "contact" -> message.text = data.first.message!!.contactInfo!!.first_name + " " + data.first.message!!.contactInfo!!.last_name
                "channel" -> message.text = data.first.message!!.channelInfo!!.name
                "location" -> message.text = data.first.message!!.locationInfo!!.streetAddress
            }
            if (Date().time.minus(data.first.last_activity!!.time) >= 24 * 60 * 60 * 1000) {
                time.text = SimpleDateFormat("EEE").format(data.first.last_activity)
            } else {
                time.text = SimpleDateFormat("h:mm aaa").format(data.first.last_activity)
            }
        } else {
            message.text = ""
            time.text = ""
        }
        if (!data.first.isRead) {
            eventIndicator.visibility = ImageView.VISIBLE
        } else {
            eventIndicator.visibility = ImageView.INVISIBLE
        }

    }

    /**PRIVATE CHATS*/
    /*CHAT - Conversations*/
    override fun setUpChatRecycler() {
        mAdapterChat = object : BaseRecyclerAdapter<Room, BaseViewHolder>() {
            override fun onBindItemViewHolder(viewHolder: BaseViewHolder?, data: Room?, position: Int, type: Int) {
                bindRoomViewHolder(viewHolder!!, data!!)
            }

            override fun viewHolder(inflater: LayoutInflater?, parent: ViewGroup?, type: Int): BaseViewHolder {
                return object : BaseViewHolder(R.layout.card_view_lobby_event, inflater!!, parent) {
                    override fun addClicks(views: ViewMap?) {
                        views?.click {

                            //TODO: Avoid long press on Eight Contact

                            val room = getItem(adapterPosition)
                            if (room.chatType == "private") {
                                val intent = Intent(context, ChatActivity::class.java)
                                intent.putExtra(Constants.IntentKeys.CHAT_NAME, room.user?.first_name + " " + room.user?.last_name)
                                intent.putExtra(Constants.IntentKeys.PARTICIPANT_ID, room.user?.userRooms?.userId)
                                intent.putExtra(Constants.IntentKeys.ROOM_ID, room.id)
                                intent.putExtra(Constants.IntentKeys.CHAT_PIC, room.user?.avatar
                                        ?: "")
                                startActivity(intent)
                            } else if (room.chatType == "group") {
                                val intent = Intent(context, GroupChatActivity::class.java)
                                intent.putExtra(Constants.IntentKeys.CHAT_NAME, room.groupChatInfo?.name)
                                intent.putExtra(Constants.IntentKeys.ROOM_ID, room.id)
                                intent.putExtra(Constants.IntentKeys.CHAT_PIC, room.groupChatInfo?.avatar
                                        ?: "")
                                startActivity(intent)
                            } else if (room.event!!) {
                                val data = getItem(adapterPosition)
                                val intent = Intent(context, EventViewActivity::class.java)
                                intent.putExtra(Constants.IntentKeys.EVENT_ID, data.events?.id)
                                intent.putExtra(Constants.IntentKeys.EVENT_NAME, data.events?.name)
                                intent.putExtra(Constants.IntentKeys.EVENT_LOCATION, data.events?.location_name)
                                intent.putExtra(Constants.IntentKeys.ROOM_ID, data.id)
                                startActivity(intent)
                            }

                        }
                    }
                }
            }
        }

        mAdapterChat?.setItems(mChatsList)
        mAdapterChat?.setSortComparator(EightQueries.Comparators.dateComparatorRooms)
        fl_chat_title.visibility = TextView.VISIBLE
        fl_chat_recycler.visibility = RecyclerView.VISIBLE
        val linearLayoutManager = object : LinearLayoutManager(context) {
            override fun canScrollHorizontally(): Boolean = false
            override fun canScrollVertically(): Boolean = false
        }
        fl_chat_recycler.layoutManager = linearLayoutManager
        fl_chat_recycler.adapter = mAdapterChat

    }

    private fun bindRoomViewHolder(viewHolder: BaseViewHolder, data: Room) {
        val eventPic = viewHolder.get<ImageView>(R.id.cvle_picture_event)
        val eventIndicator = viewHolder.get<ImageView>(R.id.cvle_read_indicator)
        val title = viewHolder.get<TextView>(R.id.cvle_title)
        val message = viewHolder.get<TextView>(R.id.cvle_message)
        val time = viewHolder.get<TextView>(R.id.cvle_time)
        if (data.chatType == "private") {
            Picasso.with(context).load(data.user!!.avatar).placeholder(R.mipmap.ic_launcher_round).transform(CircleTransform()).into(eventPic)
            eventPic.setOnLongClickListener {
                activity().replaceFragment(R.id.overlay_content_frame, LobbyOverlayFragment.newInstance(data.id!!, data.user!!.first_name + " " + data.user!!.last_name, data.user!!.avatar!!, data.user!!.phone!!), true, true)
                true
            }
            title.text = data.user!!.first_name + " " + data.user!!.last_name
            if (data.message != null) {
                when (data.message!!.type) {
                    "string" -> message.text = data.message!!.message
                    "media" -> message.text = "Picture posted"
                    "contact" -> message.text = data.message!!.contactInfo!!.first_name + " " + data.message!!.contactInfo!!.last_name
                    "channel" -> message.text = data.message!!.channelInfo!!.name
                    "location" -> message.text = data.message!!.locationInfo!!.streetAddress
                }
                data.last_activity?.let {
                    if (Date().time.minus(it.time) >= 24 * 60 * 60 * 1000) {
                        time.text = SimpleDateFormat("EEE").format(it)
                    } else {
                        time.text = SimpleDateFormat("h:mm aaa").format(it)
                    }
                }
            } else {
                message.text = ""
                time.text = ""
            }

            if (!data.isRead) {
                eventIndicator.visibility = ImageView.VISIBLE
            } else {
                eventIndicator.visibility = ImageView.INVISIBLE
            }
        } else if (data.chatType == "group") {
            Picasso.with(context).load(data.groupChatInfo!!.avatar).placeholder(R.mipmap.ic_launcher_round).transform(CircleTransform()).into(eventPic)
            title.text = data.groupChatInfo!!.name
//            eventPic.setOnLongClickListener {
//                activity().replaceFragment(R.id.overlay_content_frame, LobbyOverlayFragment.newInstance(data.id!!, data.groupChatInfo!!.name!!, data.groupChatInfo!!.avatar!!), true)
//                true
//            }
            if (data.message != null) {
                when (data.message!!.type) {
                    "string" -> message.text = data.message!!.message
                    "media" -> message.text = "Picture posted"
                    "contact" -> message.text = data.message!!.contactInfo!!.first_name + " " + data.message!!.contactInfo!!.last_name
                    "channel" -> message.text = data.message!!.channelInfo!!.name
                    "location" -> message.text = data.message!!.locationInfo!!.streetAddress
                }
                data.last_activity?.let {
                    if (Date().time.minus(it.time) >= 24 * 60 * 60 * 1000) {
                        time.text = SimpleDateFormat("EEE").format(it)
                    } else {
                        time.text = SimpleDateFormat("h:mm aaa").format(it)
                    }
                }
            } else {
                message.text = ""
                time.text = ""
            }
            if (!data.isRead) {
                eventIndicator.visibility = ImageView.VISIBLE
            } else {
                eventIndicator.visibility = ImageView.INVISIBLE
            }
        } else if (data.event!!) {
            EventsEight().queryFirst { equalTo("room_id", data.id) }.let {
                Picasso.with(context).load(it?.avatar).placeholder(R.mipmap.ic_launcher_round).transform(CircleTransform()).into(eventPic)
                title.text = it?.name
                if (data.message != null) {
                    when (data.message!!.type) {
                        "string" -> message.text = data.message!!.message
                        "media" -> message.text = "Picture posted"
                        "contact" -> message.text = data.message!!.contactInfo!!.first_name + " " + data.message!!.contactInfo!!.last_name
                        "channel" -> message.text = data.message!!.channelInfo!!.name
                        "location" -> message.text = data.message!!.locationInfo!!.streetAddress
                    }
                    if (Date().time.minus(data.last_activity!!.time) >= 24 * 60 * 60 * 1000) {
                        time.text = SimpleDateFormat("EEE").format(data.last_activity)
                    } else {
                        time.text = SimpleDateFormat("h:mm aaa").format(data.last_activity)
                    }
                } else {
                    message.text = ""
                    time.text = ""
                }
                if (!data.isRead) {
                    eventIndicator.visibility = ImageView.VISIBLE
                } else {
                    eventIndicator.visibility = ImageView.INVISIBLE
                }
            }
        }
    }

    /*Updates*/

    override fun refreshMyChannels() {
        //TODO: If there are no channels create a zero state
        mAdapterChannels.setItems(controller.getMyChannels())
        mAdapterChannels.notifyDataSetChanged()
    }

    override fun refreshFollowedChannels() {
        mAdapterChannels.addAll(controller.getChannelsFollowed())
        mAdapterChannels.notifyDataSetChanged()
    }

    override fun refreshEvents() {
        /**
         * Load events & begin object relationship with realm [Room] [EventsEight]
         * */
        val events = controller.getEvents()
        val eventsList = mutableListOf<Pair<Room, EventsEight?>>()
        for (event in events) {
            EventsEight().queryFirst {
                equalTo("room_id", event.id)
            }.let {
                eventsList.add(Pair(event, it))
            }
        }
        //UI
        if (eventsList.isEmpty()) {
            fl_events_title.visibility = TextView.GONE
        } else {
            fl_events_title.visibility = TextView.VISIBLE
        }
        mAdapterEvent.clear()
        mAdapterEvent.setItems(eventsList)
        eventsList.clear()
        mAdapterEvent.notifyDataSetChanged()
    }

    override fun refreshChat() {
        //TODO: If there are not chats create a zero state
        mChatsList.clear()
        mChatsList.addAll(controller.getChat())
        mAdapterChat.clear()
        mAdapterChat.addAll(mChatsList)
        mAdapterChat.setSortComparator(EightQueries.Comparators.dateComparatorRooms)
        mAdapterChat.notifyDataSetChanged()
    }

    /*Channel Separator*/
    override fun setSeparatorCounter(pos: Int) {
        separatorPosition = pos
    }

}