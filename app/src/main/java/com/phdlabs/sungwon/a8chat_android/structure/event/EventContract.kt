package com.phdlabs.sungwon.a8chat_android.structure.event

import android.content.Intent
import android.widget.EditText
import com.phdlabs.sungwon.a8chat_android.api.data.EventPostData
import com.phdlabs.sungwon.a8chat_android.model.event.EventsEight
import com.phdlabs.sungwon.a8chat_android.model.media.Media
import com.phdlabs.sungwon.a8chat_android.model.message.Message
import com.phdlabs.sungwon.a8chat_android.structure.application.Application
import com.phdlabs.sungwon.a8chat_android.structure.core.BaseController
import com.phdlabs.sungwon.a8chat_android.structure.core.BaseView
import com.phdlabs.sungwon.a8chat_android.structure.event.create.EventCreateActivity
import com.phdlabs.sungwon.a8chat_android.structure.event.view.EventViewActivity

/**
 * Created by SungWon on 1/2/2018.
 * Updated by JPAM on 1/30/2018
 */
interface EventContract {
    interface Create {
        interface View : BaseView<Controller> {
            /*UI*/
            val getActivity: EventCreateActivity

            /*Event Image*/
            fun setEventImage(filePath: String)

            /*Media*/
            fun getMedia(media: Media)

            /*Location*/
            fun getLocation(location: Pair<String?, String?>?)

            /*Transition*/
            fun onCreateEvent(event: EventsEight?)
        }

        interface Controller : BaseController {
            /*Event Picture*/
            fun onPictureResult(requestCode: Int, resultCode: Int, data: Intent?)

            fun showPicture()

            /*Create Event*/
            fun createEvent(eventPostData: EventPostData)
        }
    }

    interface ViewDetail {
        interface View : BaseView<Controller> {
            val get8Application: Application
            val getActivity: EventViewActivity
            val getMessageET: String
            val getMessageETObject: EditText
            val getRoomId: Int

            fun lastTimeDisplayed(position: Int): Boolean
            fun lastTimeDisplayed(message: Message): Boolean

            fun updateRecycler()
            fun hideDrawer()
        }

        interface Controller : BaseController {
            fun sendMessage()
            fun sendChannel(channelId: Int)
            fun sendContact()
            fun sendFile()
            fun sendLocation()
            fun sendMedia()

            fun onDestroy()

            fun setMessageObject(position: Int, message: Message)

            fun retrieveChatHistory()

            fun getMessages(): MutableList<Message>

            fun getUserId(callback: (Int?) -> Unit)

            fun onPictureResult(requestCode: Int, resultCode: Int, data: Intent?)
        }
    }
}