package com.phdlabs.sungwon.a8chat_android.structure.event.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.structure.application.Application
import com.phdlabs.sungwon.a8chat_android.structure.channel.mychannels.MyChannelsListActivity
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import com.phdlabs.sungwon.a8chat_android.structure.event.EventContract
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import com.phdlabs.sungwon.a8chat_android.utility.camera.CameraControl
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import kotlinx.android.synthetic.main.activity_chat.*

/**
 * Created by SungWon on 1/8/2018.
 */
class EventViewActivity: CoreActivity(), EventContract.View.View{
    override lateinit var controller: EventContract.View.Controller

    override fun layoutId(): Int = R.layout.activity_chat

    override fun contentContainerId(): Int = 0

    private lateinit var mEventId: String
    private lateinit var mEventName: String
    private var mEventLocation: String? = null
    private var mRoomId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventViewController(this)
        mEventId = intent.getStringExtra(Constants.IntentKeys.EVENT_ID)
        mEventName = intent.getStringExtra(Constants.IntentKeys.EVENT_NAME)
        mEventLocation = intent.getStringExtra(Constants.IntentKeys.EVENT_LOCATION)
        mRoomId = intent.getIntExtra(Constants.IntentKeys.ROOM_ID, 0)
        showBackArrow(R.drawable.ic_back)
        mEventLocation?.let {
            setDoubleToolbarTitle(mEventName, mEventLocation)
        } ?: run {
            setToolbarTitle(mEventName)
        }
        controller.start()
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
        controller.onDestroy()
    }

    override fun updateRecycler() {
    }

    private fun setUpDrawer(){
        ac_the_daddy_drawer.isClipPanel = false
        ac_the_daddy_drawer.panelHeight = 150
        ac_the_daddy_drawer.coveredFadeColor = ContextCompat.getColor(this, R.color.transparent)
        ac_drawer_money_container.visibility = LinearLayout.GONE
        ac_drawer_money_filler.visibility = View.GONE
    }

    private fun setUpClickers(){
        ac_emitting_button_of_green_arrow.setOnClickListener {
            controller.sendMessage()
        }
        ac_conjuring_conduit_of_messages.setOnClickListener {
            val layoutManager = ac_floating_cascade_of_parchments.layoutManager as LinearLayoutManager
            layoutManager.scrollToPositionWithOffset(controller.getMessages().size -1, 0)
            ac_conjuring_conduit_of_messages.isFocusableInTouchMode = true

            ac_conjuring_conduit_of_messages.post({
                ac_conjuring_conduit_of_messages.requestFocus()
                val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(ac_conjuring_conduit_of_messages, InputMethodManager.SHOW_IMPLICIT)
            })
        }
        ac_conjuring_conduit_of_messages.setOnFocusChangeListener({ view, b ->
            if (!b){
                ac_conjuring_conduit_of_messages.isFocusableInTouchMode = false
                val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(ac_conjuring_conduit_of_messages.windowToken, 0)
            }
        })
        ac_drawer_summoner.setOnClickListener {
            if(ac_the_daddy_drawer.panelState == SlidingUpPanelLayout.PanelState.COLLAPSED){
                ac_the_daddy_drawer.panelState = SlidingUpPanelLayout.PanelState.EXPANDED
            } else {
                ac_the_daddy_drawer.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
            }
        }
        ac_drawer_channel.setOnClickListener {
            val intent = Intent(this, MyChannelsListActivity::class.java)
            startActivityForResult(intent, Constants.RequestCode.MY_CHANNELS_LIST)
        }
        ac_drawer_contact.setOnClickListener {
            controller.sendContact()
        }
        ac_drawer_file.setOnClickListener {
            controller.sendFile()
        }
        ac_drawer_location.setOnClickListener {
            controller.sendLocation()
        }
        ac_drawer_media.setOnClickListener {
            CameraControl.instance.pickImage(this,
                    "Choose a media",
                    CameraControl.instance.requestCode(),
                    false)
        }
    }

    override val getRoomId: Int
        get() = mRoomId
    override val get8Application: Application
        get() = application as Application
    override val getActivity: EventViewActivity
        get() = this
    override val getMessageET: String
        get() = ac_conjuring_conduit_of_messages.text.toString()
    override val getMessageETObject: EditText
        get() = ac_conjuring_conduit_of_messages
}