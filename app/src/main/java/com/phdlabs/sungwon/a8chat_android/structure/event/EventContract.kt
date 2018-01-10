package com.phdlabs.sungwon.a8chat_android.structure.event

import android.content.Intent
import android.widget.EditText
import com.phdlabs.sungwon.a8chat_android.model.Message
import com.phdlabs.sungwon.a8chat_android.structure.application.Application
import com.phdlabs.sungwon.a8chat_android.structure.core.BaseController
import com.phdlabs.sungwon.a8chat_android.structure.core.BaseView
import com.phdlabs.sungwon.a8chat_android.structure.event.view.EventViewActivity

/**
 * Created by SungWon on 1/2/2018.
 */
interface EventContract {
    interface Create {
        interface View: BaseView<Controller>{
            fun showPicture(url: String)

            val get8Application : Application
        }
        interface Controller: BaseController{
            fun onPictureResult(requestCode: Int, resultCode: Int, data: Intent?)

            fun createEvent(name: String, lock: Boolean)
        }
    }
    interface View {
        interface View: BaseView<Controller>{
            val get8Application : Application
            val getActivity : EventViewActivity
            val getMessageET : String
            val getMessageETObject : EditText
            val getRoomId : Int

            fun lastTimeDisplayed(position : Int) : Boolean
            fun lastTimeDisplayed(message: Message) : Boolean

            fun updateRecycler()
        }
        interface Controller: BaseController {
            fun sendMessage()
            fun sendChannel()
            fun sendContact()
            fun sendFile()
            fun sendLocation()
            fun sendMedia()

            fun onDestroy()

            fun setMessageObject(position: Int, message: Message)

            fun retrieveChatHistory()

            fun getMessages() : MutableList<Message>

            fun getUserId(callback: (Int?) -> Unit)

            fun onPictureResult(requestCode: Int, resultCode: Int, data: Intent?)
        }
    }
}