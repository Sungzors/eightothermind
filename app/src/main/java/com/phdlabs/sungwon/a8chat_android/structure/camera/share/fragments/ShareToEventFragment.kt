package com.phdlabs.sungwon.a8chat_android.structure.camera.share.fragments

import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.db.events.EventsManager
import com.phdlabs.sungwon.a8chat_android.model.event.EventsEight
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreFragment
import com.phdlabs.sungwon.a8chat_android.utility.adapter.BaseRecyclerAdapter
import com.phdlabs.sungwon.a8chat_android.utility.adapter.BaseViewHolder
import com.phdlabs.sungwon.a8chat_android.utility.camera.CircleTransform
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_share_to_event.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by paix on 3/26/18.
 */
class ShareToEventFragment : CoreFragment() {


    override fun layoutId(): Int = R.layout.fragment_share_to_event

    private lateinit var mAdapterEvent: BaseRecyclerAdapter<EventsEight, BaseViewHolder>
    var mEventList: MutableList<EventsEight> = mutableListOf()

    override fun onStart() {
        super.onStart()
        //Query Events
        EventsManager.instance.queryMyEvents()?.let {
            setUpEventsRecycler(it.toMutableList())
        }
        mEventList.clear()
    }


    /*Events*/
    private fun setUpEventsRecycler(events: MutableList<EventsEight>) {
        mAdapterEvent = object : BaseRecyclerAdapter<EventsEight, BaseViewHolder>() {
            override fun onBindItemViewHolder(viewHolder: BaseViewHolder?, data: EventsEight?, position: Int, type: Int) {
                bindEventViewHolder(viewHolder!!, data!!)
            }

            override fun viewHolder(inflater: LayoutInflater?, parent: ViewGroup?, type: Int): BaseViewHolder {
                return object : BaseViewHolder(R.layout.card_view_lobby_event, inflater!!, parent) {
                }
            }

        }
        mAdapterEvent.setItems(events)
        fstc_events_title.visibility = TextView.VISIBLE
        fstc_events_recycler.visibility = RecyclerView.VISIBLE
        fstc_events_recycler.layoutManager = LinearLayoutManager(coreActivity.context)
        fstc_events_recycler.adapter = mAdapterEvent
    }

    private fun bindEventViewHolder(viewHolder: BaseViewHolder, data: EventsEight) {
        val selectionContainer = viewHolder.get<RelativeLayout>(R.id.cvle_selected_container)
        val selectedView = viewHolder.get<ImageView>(R.id.cvle_selected_iv)
        val eventPic = viewHolder.get<ImageView>(R.id.cvle_picture_event)
        val eventIndicator = viewHolder.get<ImageView>(R.id.cvle_read_indicator)
        val title = viewHolder.get<TextView>(R.id.cvle_title)
        val message = viewHolder.get<TextView>(R.id.cvle_message)
        val time = viewHolder.get<TextView>(R.id.cvle_time)
        Picasso.with(coreActivity.context).load(data.avatar).placeholder(R.drawable.addphoto).transform(CircleTransform()).into(eventPic)
        title.text = data.event_name
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
        selectionContainer.setOnClickListener {
            if (selectedView.visibility == View.VISIBLE) {
                selectedView.visibility = View.GONE
                if (mEventList.contains(data)) {
                    mEventList.remove(data)
                }
            } else {
                selectedView.visibility = View.VISIBLE
                if (!mEventList.contains(data)) {
                    mEventList.add(data)
                }
            }
        }
    }

    /*Filter*/
    fun filterEventsAdapter(p0: String?) {
        //UI
        p0?.let {
            //Filter Adapter
            mAdapterEvent?.setFilter { filter ->
                filter?.event_name?.toLowerCase()?.startsWith(p0.toLowerCase(), false)
            }
        }
    }
}