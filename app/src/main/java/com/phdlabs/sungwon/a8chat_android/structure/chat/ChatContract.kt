package com.phdlabs.sungwon.a8chat_android.structure.chat

import android.widget.EditText
import com.phdlabs.sungwon.a8chat_android.model.Message
import com.phdlabs.sungwon.a8chat_android.structure.application.Application
import com.phdlabs.sungwon.a8chat_android.structure.core.BaseController
import com.phdlabs.sungwon.a8chat_android.structure.core.BaseView

/**
 * Created by SungWon on 10/18/2017.
 */
interface ChatContract {
    interface View: BaseView<Controller>{
        val get8Application : Application
        val getActivity : ChatActivity
        val getChatParticipant : Int
        val getMessageET : String
        val getMessageETObject : EditText

        fun lastTimeDisplayed(position : Int) : Boolean
        fun lastTimeDisplayed(message: Message) : Boolean

        fun updateRecycler()
        fun updateRecycler(position: Int)
    }

    interface Controller: BaseController{
        fun destroy()

        fun getMessages() : MutableList<Message>
        val getUserId : Int

        fun setMessageObject(position: Int, message: Message)

        fun createPrivateChatRoom()

        fun retrieveChatHistory()

        fun sendChannel(channelId: Int)

        fun sendMessage()

        fun sendLocation()
    }
}