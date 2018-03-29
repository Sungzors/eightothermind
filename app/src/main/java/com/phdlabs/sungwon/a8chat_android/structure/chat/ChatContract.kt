package com.phdlabs.sungwon.a8chat_android.structure.chat

import android.content.Intent
import android.widget.EditText
import com.phdlabs.sungwon.a8chat_android.model.media.Media
import com.phdlabs.sungwon.a8chat_android.model.message.Message
import com.phdlabs.sungwon.a8chat_android.structure.application.Application
import com.phdlabs.sungwon.a8chat_android.structure.core.BaseController
import com.phdlabs.sungwon.a8chat_android.structure.core.BaseView
import java.io.File

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

        fun setMedia(media: Media)
        fun setMedia(mediaList: MutableList<Media>)

        fun hideDrawer()

        fun lastTimeDisplayed(position : Int) : Boolean
        fun lastTimeDisplayed(message: Message) : Boolean

        fun updateRecycler()
        fun updateRecycler(position: Int)
    }

    interface Controller: BaseController{
        fun destroy()

        fun getMessages() : MutableList<Message>
        fun getRoomId() : Int
        fun getUserId(callback: (Int?) -> Unit)

        fun setMessageObject(position: Int, message: Message)

        fun createPrivateChatRoom()

        fun retrieveChatHistory()

        fun onPictureResult(requestCode: Int, resultCode: Int, data: Intent?)

        fun favoriteMessage(message: Message, position: Int)
        fun deleteMessage(message: Message)

        fun sendChannel(channelId: Int)
        fun sendMessage()
        fun sendLocation()
        fun sendMedia()

        /*Permissions*/
        fun requestReadingExternalStorage()
        fun permissionResults(requestCode: Int, permissions: Array<out String>, grantResults: IntArray): Boolean

        /*File*/
        fun sendFile(file: File, path: String)
    }
}