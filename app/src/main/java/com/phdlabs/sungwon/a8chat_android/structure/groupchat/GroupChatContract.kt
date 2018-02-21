package com.phdlabs.sungwon.a8chat_android.structure.groupchat

import android.content.Intent
import android.widget.EditText
import com.phdlabs.sungwon.a8chat_android.model.message.Message
import com.phdlabs.sungwon.a8chat_android.structure.application.Application
import com.phdlabs.sungwon.a8chat_android.structure.core.BaseController
import com.phdlabs.sungwon.a8chat_android.structure.core.BaseView

/**
 * Created by SungWon on 2/19/2018.
 */
interface GroupChatContract {
    interface View: BaseView<Controller>{
        val get8Application : Application
        val getActivity : GroupChatActivity
        val getChatParticipant : List<Int>
        val getMessageET : String
        val getMessageETObject : EditText
        val getRoomId: Int

        fun hideDrawer()

        fun lastTimeDisplayed(position : Int) : Boolean
        fun lastTimeDisplayed(message: Message) : Boolean

        fun updateRecycler()
        fun updateRecycler(position: Int)
    }
    interface Controller: BaseController {
        fun destroy()

        fun getMessages() : MutableList<Message>
        fun getRoomId() : Int
        fun getUserId(callback: (Int?) -> Unit)

        fun setMessageObject(position: Int, message: Message)

        fun onPictureResult(requestCode: Int, resultCode: Int, data: Intent?)

        fun retrieveChatHistory()

        fun sendChannel(channelId: Int)
        fun sendMessage()
        fun sendLocation()
        fun sendMedia()
    }
}