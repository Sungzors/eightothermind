package com.phdlabs.sungwon.a8chat_android.structure.groupchat

import android.content.Intent
import android.widget.EditText
import com.phdlabs.sungwon.a8chat_android.api.data.GroupChatPostData
import com.phdlabs.sungwon.a8chat_android.model.media.Media
import com.phdlabs.sungwon.a8chat_android.model.message.Message
import com.phdlabs.sungwon.a8chat_android.structure.application.Application
import com.phdlabs.sungwon.a8chat_android.structure.core.BaseController
import com.phdlabs.sungwon.a8chat_android.structure.core.BaseView
import com.phdlabs.sungwon.a8chat_android.structure.groupchat.create.GroupCreateActivity
import com.phdlabs.sungwon.a8chat_android.structure.groupchat.groupinvite.GroupInviteActivity

/**
 * Created by SungWon on 2/19/2018.
 */
interface GroupChatContract {
    interface Create {
        interface View : BaseView<Controller>{
            val getActivity: GroupCreateActivity

            fun setChannelImage(filePath: String)

            fun setMedia(media: Media)

            fun onCreateGroup(name: String, id: Int, pic: String)

        }
        interface Controller : BaseController {
            /*Event Picture*/
            fun onPictureResult(requestCode: Int, resultCode: Int, data: Intent?)

            fun showPicture()

            fun createGroupChat(data: GroupChatPostData)

        }
    }

    interface Invite {
        interface View : BaseView<Controller>{
            val activity: GroupInviteActivity


            fun refreshRecycler()
        }
        interface Controller: BaseController {
            /*load contacts*/
            fun loadContactsCheckCache()
            /*Permissions*/
            fun permissionResults(requestCode: Int, permissions: Array<out String>, grantResults: IntArray):Boolean
        }
    }

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