package com.phdlabs.sungwon.a8chat_android.structure.chat

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

        fun updateRecycler()
    }

    interface Controller: BaseController{
        fun destroy()

        val getMessages : MutableList<Message>
        val getUserId : Int

        fun createPrivateChatRoom()
    }
}