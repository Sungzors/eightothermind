package com.phdlabs.sungwon.a8chat_android.structure.chat

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.model.Message
import com.phdlabs.sungwon.a8chat_android.structure.application.Application
import com.phdlabs.sungwon.a8chat_android.structure.channel.mychannels.MyChannelsListActivity
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import com.phdlabs.sungwon.a8chat_android.utility.adapter.BaseRecyclerAdapter
import com.phdlabs.sungwon.a8chat_android.utility.adapter.BaseViewHolder
import com.phdlabs.sungwon.a8chat_android.utility.adapter.ViewMap
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_chat.*
import java.text.SimpleDateFormat

/**
 * Created by SungWon on 10/18/2017.
 * Updated by JPAM on 12/27/2017
 */
class ChatActivity: CoreActivity(), ChatContract.View{

    override lateinit var controller: ChatContract.Controller
    private lateinit var mAdapter: BaseRecyclerAdapter<Message, BaseViewHolder>
    private var mUserId: Int? = null

    override fun layoutId() = R.layout.activity_chat

    override fun contentContainerId() = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showProgress()
        ChatController(this)
        controller.start()
        controller.getUserId { id ->
            id?.let {
                mUserId = it
            }
        }
        controller.createPrivateChatRoom()
        setupDrawer()
        setupClickers()
        setupRecycler()
    }

    override fun onStart() {
        super.onStart()
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

    private fun setupDrawer() {
        ac_the_daddy_drawer.isClipPanel = false
        ac_the_daddy_drawer.panelHeight = 150
        ac_the_daddy_drawer.coveredFadeColor = ContextCompat.getColor(this, R.color.transparent)
    }

    private fun setupClickers() {
        ac_emitting_button_of_green_arrow.setOnClickListener({
            controller.sendMessage()
        })
        ac_conjuring_conduit_of_messages.setOnClickListener({
            val layoutManager = ac_floating_cascade_of_parchments.layoutManager as LinearLayoutManager
            layoutManager.scrollToPositionWithOffset(controller.getMessages().size -1, 0)
            ac_conjuring_conduit_of_messages.isFocusableInTouchMode = true

            ac_conjuring_conduit_of_messages.post({
                ac_conjuring_conduit_of_messages.requestFocus()
                val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(ac_conjuring_conduit_of_messages, InputMethodManager.SHOW_IMPLICIT)
            })
        })
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

        }
        ac_drawer_file.setOnClickListener {

        }
        ac_drawer_location.setOnClickListener {
            controller.sendLocation()
        }
        ac_drawer_media.setOnClickListener {

        }
        ac_drawer_money.setOnClickListener {

        }
    }

    fun setupRecycler(){
        mAdapter = object: BaseRecyclerAdapter<Message, BaseViewHolder>(){
            override fun onBindItemViewHolder(viewHolder: BaseViewHolder?, data: Message?, position: Int, type: Int) {
                when(data!!.type){
                    Message.TYPE_STRING -> bindMessageViewHolder(viewHolder, data)
                    Message.TYPE_CONTACT -> bindContactViewHolder(viewHolder, data)
                    Message.TYPE_LOCATION -> bindLocationViewHolder(viewHolder, data)
                    Message.TYPE_FILE -> bindFileViewHolder(viewHolder, data)
                    Message.TYPE_MEDIA -> bindMediaViewHolder(viewHolder, data)
                    Message.TYPE_MONEY -> bindMoneyViewHolder(viewHolder, data)
                    Message.TYPE_CHANNEL -> bindChannelViewHolder(viewHolder, data)
                }
            }

            override fun getItemType(t: Message?): Int {
                    if (t!!.userId == mUserId?.toString()){
                        return 0
                    } else {
                        return 1
                    }
            }

            override fun viewHolder(inflater: LayoutInflater?, parent: ViewGroup?, type: Int): BaseViewHolder {
                if(type == 0){
                    return object : BaseViewHolder(R.layout.card_view_chat_right, inflater!!, parent){
                        override fun addClicks(views: ViewMap?) {
                            super.addClicks(views)
                        }
                    }
                } else {
                    return object : BaseViewHolder(R.layout.card_view_chat, inflater!!, parent){
                        override fun addClicks(views: ViewMap?) {
                            super.addClicks(views)
                        }
                    }
                }

            }
        }
        mAdapter.setItems(controller.getMessages())
        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        ac_floating_cascade_of_parchments.layoutManager = layoutManager
        ac_floating_cascade_of_parchments.adapter = mAdapter
    }

    //can move to controllerlrldf
    override fun lastTimeDisplayed(position: Int): Boolean{
        if (position == 0){
            return true
        }
        for (i in position-1 downTo 0){
//            i
//            val x = controller.getMessages()[i].timeDisplayed
            if(controller.getMessages()[i].timeDisplayed){
//                val a = mAdapter.getItem(i)
//                val b = mAdapter.getItem(position)
//                val c = controller.getMessages()[position].createdAt!!.time.minus(controller.getMessages()[i].createdAt!!.time)
                return (controller.getMessages()[position].createdAt!!.time.minus(controller.getMessages()[i].createdAt!!.time) >= 5 * 60 * 1000)
            }
        }
        return true
    }

    override fun lastTimeDisplayed(message: Message): Boolean{
        if (controller.getMessages().size == 0){
            return true
        }
        for (i in controller.getMessages().size -1 downTo 0){
            if(controller.getMessages()[i].timeDisplayed){
//                val a = mAdapter.getItem(i)
//                val b = mAdapter.getItem(position)
//                val c = controller.getMessages()[position].createdAt!!.time.minus(controller.getMessages()[i].createdAt!!.time)
                val a = message.createdAt!!.time.minus(controller.getMessages()[i].createdAt!!.time)
                return (message.createdAt!!.time.minus(controller.getMessages()[i].createdAt!!.time)>= 5 * 60 * 1000 )
            }
        }
        return true
    }

    private fun bindMessageViewHolder(viewHolder: BaseViewHolder?, message: Message?){
        val date = viewHolder!!.get<TextView>(R.id.cvc_message_date)
        val messagetv = viewHolder.get<TextView>(R.id.cvc_message)
        val profPic = viewHolder.get<ImageView>(R.id.cvc_profile_pic)
        val messagePic = viewHolder.get<ImageView>(R.id.cvc_message_pic_small)
        val picContainer1 = viewHolder.get<LinearLayout>(R.id.cvc_message_pic_container_1)
        val picContainer2 = viewHolder.get<LinearLayout>(R.id.cvc_message_pic_container_2)
        val moneyButtonAccept = viewHolder.get<Button>(R.id.cvc_message_button_money_accept)
        val moneyButtonDecline = viewHolder.get<Button>(R.id.cvc_message_button_money_decline)
        date.visibility = TextView.GONE
        messagePic.visibility = ImageView.GONE
        picContainer1.visibility = LinearLayout.GONE
        picContainer2.visibility = LinearLayout.GONE
        moneyButtonAccept.visibility = Button.GONE
        moneyButtonDecline.visibility = Button.GONE

        controller.getUserId { id ->
            id?.let {
                if(message!!.userId!!.toInt() != it){
                    Picasso.with(this).load(message.userAvatar).into(profPic)
                }
                messagetv.setTextColor(ContextCompat.getColor(this, R.color.black))
                messagetv.text = message.message
                if(message.timeDisplayed){
                    date.visibility = TextView.VISIBLE
                    val formatter = SimpleDateFormat("EEE - h:mm aaa")
                    date.text = formatter.format(message.createdAt)
                }
            }
        }
//        if(message!!.userId!!.toInt() != controller.getUserId){
//            date.visibility = TextView.GONE
//            bubble.background = ContextCompat.getDrawable(this, R.drawable.chatbubble_left)
//            val params = bubble.layoutParams as RelativeLayout.LayoutParams
//            params.setMargins(45, 5, 55, 5)
//            params.marginStart = 45
//            params.marginEnd = 55
//            params.addRule(RelativeLayout.ALIGN_PARENT_START)
//            bubble.layoutParams = params
//            bubble.textAlignment = TextView.TEXT_ALIGNMENT_TEXT_START
//            bubble.text = message.message
//            val x = viewHolder.adapterPosition
//            if(viewHolder.adapterPosition == 0){
//                date.visibility = TextView.VISIBLE
//                val formatter = SimpleDateFormat("EEE - h:mm aaa")
//                date.text = formatter.format(message.createdAt)
//            }
//        } else {
//            date.visibility = TextView.GONE
//            bubble.background = ContextCompat.getDrawable(this, R.drawable.chatbubble_right)
//            val params = bubble.layoutParams as RelativeLayout.LayoutParams
//            params.setMargins(55, 5, 30, 5)
//            params.marginStart = 55
//            params.marginEnd = 30
//            params.addRule(RelativeLayout.ALIGN_PARENT_END)
//            bubble.layoutParams = params
//            bubble.textAlignment = TextView.TEXT_ALIGNMENT_TEXT_END
//            bubble.text = message.message
//            val x = viewHolder.adapterPosition
//            if(message.timeDisplayed){
//                date.visibility = TextView.VISIBLE
//                val formatter = SimpleDateFormat("EEE - h:mm aaa")
//                date.text = formatter.format(message.createdAt)
//            }
//        }
    }

    private fun bindContactViewHolder(viewHolder: BaseViewHolder?, message: Message?){
        val date = viewHolder!!.get<TextView>(R.id.cvc_message_date)
        val messagetv = viewHolder.get<TextView>(R.id.cvc_message)
        val profPic = viewHolder.get<ImageView>(R.id.cvc_profile_pic)
        val messagePic = viewHolder.get<ImageView>(R.id.cvc_message_pic_small)
        val picContainer1 = viewHolder.get<LinearLayout>(R.id.cvc_message_pic_container_1)
        val picContainer2 = viewHolder.get<LinearLayout>(R.id.cvc_message_pic_container_2)
        val moneyButtonAccept = viewHolder.get<Button>(R.id.cvc_message_button_money_accept)
        val moneyButtonDecline = viewHolder.get<Button>(R.id.cvc_message_button_money_decline)
        date.visibility = TextView.GONE
        messagePic.visibility = ImageView.VISIBLE
        picContainer1.visibility = LinearLayout.GONE
        picContainer2.visibility = LinearLayout.GONE
        moneyButtonAccept.visibility = Button.GONE
        moneyButtonDecline.visibility = Button.GONE
        messagetv.text = message!!.contactInfo!!.first_name + message.contactInfo!!.last_name
        messagetv.setTextColor(ContextCompat.getColor(this, R.color.confirmText))
        Picasso.with(this).load(message.contactInfo!!.avatar).into(profPic)
        controller.getUserId { id ->
            id?.let {
                if(message.userId!!.toInt() != it){
                    Picasso.with(this).load(message.userAvatar).into(profPic)
                }
            }
        }
        if(message.timeDisplayed){
            date.visibility = TextView.VISIBLE
            val formatter = SimpleDateFormat("EEE - h:mm aaa")
            date.text = formatter.format(message.createdAt)
        }
    }

    private fun bindLocationViewHolder(viewHolder: BaseViewHolder?, message: Message?){
        val date = viewHolder!!.get<TextView>(R.id.cvc_message_date)
        val messagetv = viewHolder.get<TextView>(R.id.cvc_message)
        val profPic = viewHolder.get<ImageView>(R.id.cvc_profile_pic)
        val messagePic = viewHolder.get<ImageView>(R.id.cvc_message_pic_small)
        val picContainer1 = viewHolder.get<LinearLayout>(R.id.cvc_message_pic_container_1)
        val picContainer2 = viewHolder.get<LinearLayout>(R.id.cvc_message_pic_container_2)
        val moneyButtonAccept = viewHolder.get<Button>(R.id.cvc_message_button_money_accept)
        val moneyButtonDecline = viewHolder.get<Button>(R.id.cvc_message_button_money_decline)
        date.visibility = TextView.GONE
        messagePic.visibility = ImageView.GONE
        picContainer1.visibility = LinearLayout.GONE
        picContainer2.visibility = LinearLayout.GONE
        moneyButtonAccept.visibility = Button.GONE
        moneyButtonDecline.visibility = Button.GONE
        messagetv.text = message!!.locationInfo!!.streetAddress
        messagetv.setTextColor(ContextCompat.getColor(this, R.color.confirmText))
        controller.getUserId { id ->
            id?.let {
                if(message.userId!!.toInt() != id){
                    Picasso.with(this).load(message.userAvatar).into(profPic)
                }
            }
        }
        if(message.timeDisplayed){
            date.visibility = TextView.VISIBLE
            val formatter = SimpleDateFormat("EEE - h:mm aaa")
            date.text = formatter.format(message.createdAt)
        }
//        if(message!!.userId!!.toInt() != controller.getUserId){
//            date.visibility = TextView.GONE
//            bubble.background = ContextCompat.getDrawable(this, R.drawable.chatbubble_left)
//            val params = bubble.layoutParams as RelativeLayout.LayoutParams
//            params.setMargins(45, 5, 55, 5)
//            params.marginStart = 45
//            params.marginEnd = 55
//            params.addRule(RelativeLayout.ALIGN_PARENT_START)
//            bubble.layoutParams = params
//            bubble.textAlignment = TextView.TEXT_ALIGNMENT_TEXT_START
//            bubble.text = message.locationInfo!!.streetAddress
//            val x = viewHolder.adapterPosition
//            if(viewHolder.adapterPosition == 0){
//                date.visibility = TextView.VISIBLE
//                val formatter = SimpleDateFormat("EEE - h:mm aaa")
//                date.text = formatter.format(message.createdAt)
//            }
//        } else {
//            date.visibility = TextView.GONE
//            bubble.background = ContextCompat.getDrawable(this, R.drawable.chatbubble_right)
//            val params = bubble.layoutParams as RelativeLayout.LayoutParams
//            params.setMargins(55, 5, 30, 5)
//            params.marginStart = 55
//            params.marginEnd = 30
//            params.addRule(RelativeLayout.ALIGN_PARENT_END)
//            bubble.layoutParams = params
//            bubble.textAlignment = TextView.TEXT_ALIGNMENT_TEXT_END
//            bubble.text = message.locationInfo!!.streetAddress
//            val x = viewHolder.adapterPosition
//            if(message.timeDisplayed){
//                date.visibility = TextView.VISIBLE
//                val formatter = SimpleDateFormat("EEE - h:mm aaa")
//                date.text = formatter.format(message.createdAt)
//            }
//        }
    }

    private fun bindFileViewHolder(viewHolder: BaseViewHolder?, message: Message?){
        val date = viewHolder!!.get<TextView>(R.id.cvc_message_date)
        val messagetv = viewHolder.get<TextView>(R.id.cvc_message)
        val profPic = viewHolder.get<ImageView>(R.id.cvc_profile_pic)
        val messagePic = viewHolder.get<ImageView>(R.id.cvc_message_pic_small)
        val picContainer1 = viewHolder.get<LinearLayout>(R.id.cvc_message_pic_container_1)
        val picContainer2 = viewHolder.get<LinearLayout>(R.id.cvc_message_pic_container_2)
        val moneyButtonAccept = viewHolder.get<Button>(R.id.cvc_message_button_money_accept)
        val moneyButtonDecline = viewHolder.get<Button>(R.id.cvc_message_button_money_decline)
        date.visibility = TextView.GONE
        messagePic.visibility = ImageView.VISIBLE
        picContainer1.visibility = LinearLayout.GONE
        picContainer2.visibility = LinearLayout.GONE
        moneyButtonAccept.visibility = Button.GONE
        moneyButtonDecline.visibility = Button.GONE
        messagetv.text = message!!.fileNames!![0]
        messagetv.setTextColor(ContextCompat.getColor(this, R.color.confirmText))
        Picasso.with(this).load(R.drawable.ic_attachment_file).into(messagePic)
        messagetv.setOnClickListener({
            Toast.makeText(this, "this needs to dl", Toast.LENGTH_SHORT).show()
        })
        controller.getUserId { id ->
         id?.let {
             if(message.userId!!.toInt() != it) {
                 Picasso.with(this).load(message.userAvatar).into(profPic)
             }
         }
        }
        if(message.timeDisplayed){
            date.visibility = TextView.VISIBLE
            val formatter = SimpleDateFormat("EEE - h:mm aaa")
            date.text = formatter.format(message.createdAt)
        }
    }

    private fun bindMediaViewHolder(viewHolder: BaseViewHolder?, message: Message?){
        val date = viewHolder!!.get<TextView>(R.id.cvc_message_date)
        val messagetv = viewHolder.get<TextView>(R.id.cvc_message)
        val profPic = viewHolder.get<ImageView>(R.id.cvc_profile_pic)
        val messagePic = viewHolder.get<ImageView>(R.id.cvc_message_pic_small)
        val picContainer1 = viewHolder.get<LinearLayout>(R.id.cvc_message_pic_container_1)
        val picContainer2 = viewHolder.get<LinearLayout>(R.id.cvc_message_pic_container_2)
        val moneyButtonAccept = viewHolder.get<Button>(R.id.cvc_message_button_money_accept)
        val moneyButtonDecline = viewHolder.get<Button>(R.id.cvc_message_button_money_decline)
        date.visibility = TextView.GONE
        messagePic.visibility = ImageView.GONE
        picContainer1.visibility = LinearLayout.VISIBLE
        picContainer2.visibility = LinearLayout.VISIBLE
        moneyButtonAccept.visibility = Button.GONE
        moneyButtonDecline.visibility = Button.GONE
        messagetv.text = message!!.fileNames!![0]
        messagetv.setTextColor(ContextCompat.getColor(this, R.color.confirmText))
        controller.getUserId { id ->
            id?.let {
                if(message.userId!!.toInt() != id){
                    Picasso.with(this).load(message.userAvatar).into(profPic)
                }
            }
        }
        if(message.timeDisplayed){
            date.visibility = TextView.VISIBLE
            val formatter = SimpleDateFormat("EEE - h:mm aaa")
            date.text = formatter.format(message.createdAt)
        }
    }

    private fun bindMoneyViewHolder(viewHolder: BaseViewHolder?, message: Message?){
        val date = viewHolder!!.get<TextView>(R.id.cvc_message_date)
        val messagetv = viewHolder.get<TextView>(R.id.cvc_message)
        val profPic = viewHolder.get<ImageView>(R.id.cvc_profile_pic)
        val messagePic = viewHolder.get<ImageView>(R.id.cvc_message_pic_small)
        val picContainer1 = viewHolder.get<LinearLayout>(R.id.cvc_message_pic_container_1)
        val picContainer2 = viewHolder.get<LinearLayout>(R.id.cvc_message_pic_container_2)
        val moneyButtonAccept = viewHolder.get<Button>(R.id.cvc_message_button_money_accept)
        val moneyButtonDecline = viewHolder.get<Button>(R.id.cvc_message_button_money_decline)
        date.visibility = TextView.GONE
        messagePic.visibility = ImageView.VISIBLE
        picContainer1.visibility = LinearLayout.GONE
        picContainer2.visibility = LinearLayout.GONE
        moneyButtonAccept.visibility = Button.VISIBLE
        moneyButtonDecline.visibility = Button.VISIBLE
        messagetv.text = message!!.fileNames!![0]
        messagetv.setTextColor(ContextCompat.getColor(this, R.color.black))
        Picasso.with(this).load(R.drawable.ic_money).into(messagePic)
        controller.getUserId { id ->
            id?.let {
                if(message.userId!!.toInt() != it){
                    Picasso.with(this).load(message.userAvatar).into(profPic)
                }
            }
        }
        if(message.timeDisplayed){
            date.visibility = TextView.VISIBLE
            val formatter = SimpleDateFormat("EEE - h:mm aaa")
            date.text = formatter.format(message.createdAt)
        }
    }

    private fun bindChannelViewHolder(viewHolder: BaseViewHolder?, message: Message?){
        val date = viewHolder!!.get<TextView>(R.id.cvc_message_date)
        val messagetv = viewHolder.get<TextView>(R.id.cvc_message)
        val profPic = viewHolder.get<ImageView>(R.id.cvc_profile_pic)
        val messagePic = viewHolder.get<ImageView>(R.id.cvc_message_pic_small)
        val picContainer1 = viewHolder.get<LinearLayout>(R.id.cvc_message_pic_container_1)
        val picContainer2 = viewHolder.get<LinearLayout>(R.id.cvc_message_pic_container_2)
        val moneyButtonAccept = viewHolder.get<Button>(R.id.cvc_message_button_money_accept)
        val moneyButtonDecline = viewHolder.get<Button>(R.id.cvc_message_button_money_decline)
        date.visibility = TextView.GONE
        messagePic.visibility = ImageView.VISIBLE
        picContainer1.visibility = LinearLayout.GONE
        picContainer2.visibility = LinearLayout.GONE
        moneyButtonAccept.visibility = Button.GONE
        moneyButtonDecline.visibility = Button.GONE
        messagetv.text = message!!.channelInfo!!.name
        messagetv.setTextColor(ContextCompat.getColor(this, R.color.confirmText))
        Picasso.with(this).load(message.channelInfo!!.avatar).into(messagePic) //TODO: change this to channel picture from channelinfo
        messagetv.setOnClickListener({
            Toast.makeText(this, "to be implemented", Toast.LENGTH_SHORT).show()
        })
        controller.getUserId { id ->
            id?.let {
                if(message.userId!!.toInt() != it){
                    Picasso.with(this).load(message.userAvatar).into(profPic)
                }
            }
        }
        if(message.timeDisplayed){
            date.visibility = TextView.VISIBLE
            val formatter = SimpleDateFormat("EEE - h:mm aaa")
            date.text = formatter.format(message.createdAt)
        }
    }

    override val get8Application: Application
        get() = application as Application

    override val getActivity: ChatActivity
        get() = this

    override val getChatParticipant: Int
        get() = 8 //TODO: grab id from Intent

    override val getMessageET: String
        get() = ac_conjuring_conduit_of_messages.text.toString()

    override val getMessageETObject: EditText
        get() = ac_conjuring_conduit_of_messages

    override fun updateRecycler() {
        mAdapter.clear()
//        val m = controller.getMessages()
        mAdapter.setItems(controller.getMessages())
        mAdapter.notifyDataSetChanged()
        if(controller.getMessages().size>1){
            ac_floating_cascade_of_parchments.smoothScrollToPosition(controller.getMessages().size-1)
        }

    }

    override fun updateRecycler(position: Int) {
        mAdapter.clear()
//        val m = controller.getMessages()
        mAdapter.setItems(controller.getMessages())
        mAdapter.notifyItemInserted(position)
//        mAdapter.notifyDataSetChanged()
        if(controller.getMessages().size>1){
            ac_floating_cascade_of_parchments.smoothScrollToPosition(controller.getMessages().size-1)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        showProgress()
        when(requestCode){
            Constants.RequestCode.MY_CHANNELS_LIST -> {
                controller.sendChannel(data!!.getIntExtra(Constants.IntentKeys.CHANNEL_ID, 0))
                controller.retrieveChatHistory()
                ac_the_daddy_drawer.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
        hideProgress()
    }
}