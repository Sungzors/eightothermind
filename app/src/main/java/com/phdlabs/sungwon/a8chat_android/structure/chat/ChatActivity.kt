package com.phdlabs.sungwon.a8chat_android.structure.chat

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.model.Message
import com.phdlabs.sungwon.a8chat_android.structure.application.Application
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import com.phdlabs.sungwon.a8chat_android.utility.adapter.BaseRecyclerAdapter
import com.phdlabs.sungwon.a8chat_android.utility.adapter.BaseViewHolder
import com.phdlabs.sungwon.a8chat_android.utility.adapter.ViewMap
import kotlinx.android.synthetic.main.activity_chat.*
import java.text.SimpleDateFormat

/**
 * Created by SungWon on 10/18/2017.
 */
class ChatActivity: CoreActivity(), ChatContract.View{

    override lateinit var controller: ChatContract.Controller
    private lateinit var mAdapter: BaseRecyclerAdapter<Message, BaseViewHolder>

    override fun layoutId() = R.layout.activity_chat

    override fun contentContainerId() = 0

    override fun onStart() {
        super.onStart()
        showProgress()
        ChatController(this)
        controller.start()
        controller.createPrivateChatRoom()
        setupClickers()
        setupRecycler()
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

            override fun viewHolder(inflater: LayoutInflater?, parent: ViewGroup?, type: Int): BaseViewHolder {
                return object : BaseViewHolder(R.layout.card_view_chat, inflater!!, parent){
                    override fun addClicks(views: ViewMap?) {
                        super.addClicks(views)
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
                return (controller.getMessages()[position].createdAt!!.time.minus(controller.getMessages()[i].createdAt!!.time)>= 20*60*1000)
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
                return (message.createdAt!!.time.minus(controller.getMessages()[i].createdAt!!.time)>= 20*60*1000)
            }
        }
        return true
    }

    private fun bindMessageViewHolder(viewHolder: BaseViewHolder?, message: Message?){
        val date = viewHolder!!.get<TextView>(R.id.cvc_message_date)
        val bubble = viewHolder.get<TextView>(R.id.cvc_message)
        if(message!!.userId!!.toInt() != controller.getUserId){
            date.visibility = TextView.GONE
            bubble.background = ContextCompat.getDrawable(this, R.drawable.chatbubble_left)
            val params = bubble.layoutParams as RelativeLayout.LayoutParams
            params.setMargins(45, 5, 55, 5)
            params.marginStart = 45
            params.marginEnd = 55
            params.addRule(RelativeLayout.ALIGN_PARENT_START)
            bubble.layoutParams = params
            bubble.textAlignment = TextView.TEXT_ALIGNMENT_TEXT_START
            bubble.text = message.message
            val x = viewHolder.adapterPosition
            if(viewHolder.adapterPosition == 0){
                date.visibility = TextView.VISIBLE
                val formatter = SimpleDateFormat("EEE - h:mm aaa")
                date.text = formatter.format(message.createdAt)
            }
        } else {
            date.visibility = TextView.GONE
            bubble.background = ContextCompat.getDrawable(this, R.drawable.chatbubble_right)
            val params = bubble.layoutParams as RelativeLayout.LayoutParams
            params.setMargins(55, 5, 30, 5)
            params.marginStart = 55
            params.marginEnd = 30
            params.addRule(RelativeLayout.ALIGN_PARENT_END)
            bubble.layoutParams = params
            bubble.textAlignment = TextView.TEXT_ALIGNMENT_TEXT_END
            bubble.text = message.message
            val x = viewHolder.adapterPosition
            if(message.timeDisplayed){
                date.visibility = TextView.VISIBLE
                val formatter = SimpleDateFormat("EEE - h:mm aaa")
                date.text = formatter.format(message.createdAt)
            }
        }
    }

    private fun bindContactViewHolder(viewHolder: BaseViewHolder?, message: Message?){

    }

    private fun bindLocationViewHolder(viewHolder: BaseViewHolder?, message: Message?){
        val date = viewHolder!!.get<TextView>(R.id.cvc_message_date)
        val bubble = viewHolder.get<TextView>(R.id.cvc_message)
        if(message!!.userId!!.toInt() != controller.getUserId){
            date.visibility = TextView.GONE
            bubble.background = ContextCompat.getDrawable(this, R.drawable.chatbubble_left)
            val params = bubble.layoutParams as RelativeLayout.LayoutParams
            params.setMargins(45, 5, 55, 5)
            params.marginStart = 45
            params.marginEnd = 55
            params.addRule(RelativeLayout.ALIGN_PARENT_START)
            bubble.layoutParams = params
            bubble.textAlignment = TextView.TEXT_ALIGNMENT_TEXT_START
            bubble.text = message.locationInfo!!.streetAddress
            val x = viewHolder.adapterPosition
            if(viewHolder.adapterPosition == 0){
                date.visibility = TextView.VISIBLE
                val formatter = SimpleDateFormat("EEE - h:mm aaa")
                date.text = formatter.format(message.createdAt)
            }
        } else {
            date.visibility = TextView.GONE
            bubble.background = ContextCompat.getDrawable(this, R.drawable.chatbubble_right)
            val params = bubble.layoutParams as RelativeLayout.LayoutParams
            params.setMargins(55, 5, 30, 5)
            params.marginStart = 55
            params.marginEnd = 30
            params.addRule(RelativeLayout.ALIGN_PARENT_END)
            bubble.layoutParams = params
            bubble.textAlignment = TextView.TEXT_ALIGNMENT_TEXT_END
            bubble.text = message.locationInfo!!.streetAddress
            val x = viewHolder.adapterPosition
            if(message.timeDisplayed){
                date.visibility = TextView.VISIBLE
                val formatter = SimpleDateFormat("EEE - h:mm aaa")
                date.text = formatter.format(message.createdAt)
            }
        }
    }

    private fun bindFileViewHolder(viewHolder: BaseViewHolder?, message: Message?){

    }

    private fun bindMediaViewHolder(viewHolder: BaseViewHolder?, message: Message?){

    }

    private fun bindMoneyViewHolder(viewHolder: BaseViewHolder?, message: Message?){

    }

    private fun bindChannelViewHolder(viewHolder: BaseViewHolder?, message: Message?){

    }

    override val get8Application: Application
        get() = application as Application

    override val getActivity: ChatActivity
        get() = this

    override val getChatParticipant: Int
        get() = 6 //TODO: grab id from Intent

    override val getMessageET: String
        get() = ac_conjuring_conduit_of_messages.text.toString()

    override val getMessageETObject: EditText
        get() = ac_conjuring_conduit_of_messages

    override fun updateRecycler() {
        mAdapter.clear()
        val m = controller.getMessages()
        mAdapter.setItems(controller.getMessages())
        mAdapter.notifyDataSetChanged()
        if(controller.getMessages().size>1){
            ac_floating_cascade_of_parchments.smoothScrollToPosition(controller.getMessages().size-1)
        }

    }
}