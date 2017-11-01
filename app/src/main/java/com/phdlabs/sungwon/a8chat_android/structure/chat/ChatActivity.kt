package com.phdlabs.sungwon.a8chat_android.structure.chat

import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.model.Message
import com.phdlabs.sungwon.a8chat_android.structure.application.Application
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import com.phdlabs.sungwon.a8chat_android.utility.adapter.BaseRecyclerAdapter
import com.phdlabs.sungwon.a8chat_android.utility.adapter.BaseViewHolder
import com.phdlabs.sungwon.a8chat_android.utility.adapter.ViewMap
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.card_view_chat.*
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
        ChatController(this)
        controller.start()
        controller.createPrivateChatRoom()
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
        mAdapter.setItems(controller.getMessages)
        ac_floating_cascade_of_parchments.layoutManager = LinearLayoutManager(this)
        ac_floating_cascade_of_parchments.adapter = mAdapter
    }

    private fun bindMessageViewHolder(viewHolder: BaseViewHolder?, message: Message?){
        if(message!!.userId!!.toInt() == controller.getUserId){
            cvc_message_date.visibility = TextView.GONE
            cvc_message_bubble.background = ContextCompat.getDrawable(this, R.drawable.chatbubble_left)
            val params = cvc_message_bubble.layoutParams as LinearLayout.LayoutParams
            params.marginStart = 45
            params.marginEnd = 55
            params.gravity = Gravity.START
            cvc_message_bubble.layoutParams = params
            if(viewHolder!!.adapterPosition == 0){
                cvc_message_date.visibility = TextView.VISIBLE
                val formatter = SimpleDateFormat("EEE - h:mm aaa")
                cvc_message_date.text = formatter.format(message.createdAt)
                message.timeDisplayed = true
            } else if (lastTimeDisplayed(viewHolder.adapterPosition)) {
                cvc_message_date.visibility = TextView.VISIBLE
                val formatter = SimpleDateFormat("EEE - h:mm aaa")
                cvc_message_date.text = formatter.format(message.createdAt)
                message.timeDisplayed = true
            }
        } else {
            cvc_message_bubble.background = ContextCompat.getDrawable(this, R.drawable.chatbubble_right)
            val params = cvc_message_bubble.layoutParams as LinearLayout.LayoutParams
            params.marginStart = 55
            params.marginEnd = 15
            params.gravity = Gravity.END
            cvc_message_bubble.layoutParams = params
            cvm_message.textAlignment = TextView.TEXT_ALIGNMENT_TEXT_END
        }
    }

    private fun lastTimeDisplayed(position: Int): Boolean{
        for (i in position - 1 downTo 0){
            if(mAdapter.selectedItems[i].timeDisplayed){
                return (mAdapter.selectedItems[i].createdAt!!.time.minus(mAdapter.selectedItems[position].createdAt!!.time)>= 20*60*1000)
            }
        }
        return true
    }

    private fun bindContactViewHolder(viewHolder: BaseViewHolder?, message: Message?){

    }

    private fun bindLocationViewHolder(viewHolder: BaseViewHolder?, message: Message?){

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
        get() = 2 //TODO: grab id from Intent

    override fun updateRecycler() {
        mAdapter.notifyDataSetChanged()
    }
}