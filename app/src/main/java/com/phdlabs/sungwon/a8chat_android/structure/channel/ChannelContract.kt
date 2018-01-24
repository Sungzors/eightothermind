package com.phdlabs.sungwon.a8chat_android.structure.channel

import android.content.Intent
import android.widget.EditText
import com.phdlabs.sungwon.a8chat_android.api.data.PostChannelData
import com.phdlabs.sungwon.a8chat_android.model.Channel
import com.phdlabs.sungwon.a8chat_android.model.ChannelShowNest
import com.phdlabs.sungwon.a8chat_android.model.Message
import com.phdlabs.sungwon.a8chat_android.structure.application.Application
import com.phdlabs.sungwon.a8chat_android.structure.channel.mychannel.MyChannelActivity
import com.phdlabs.sungwon.a8chat_android.structure.core.BaseController
import com.phdlabs.sungwon.a8chat_android.structure.core.BaseView

/**
 * Created by SungWon on 11/30/2017.
 */
interface ChannelContract {

    interface Create {
        interface View: BaseView<Controller> {
            fun finishActivity(chanId: String, chanName: String, roomId: Int)
        }
        interface Controller: BaseController {

            fun createChannel(data: PostChannelData)
            fun uploadPicture()
        }
    }

    interface PostShow{
        interface View: BaseView<Controller>{
            fun setUpRecycler()
            val getChannelId: String
            val getMessageId: String
            fun onLike()
        }
        interface Controller: BaseController{
            fun likePost(messageId: String, userId: String)
            fun commentPost(messageId: String)
        }
    }

    interface MyChannel{
        interface View: BaseView<Controller>{
            val get8Application : Application
            val getActivity : MyChannelActivity
            val getMessageET : String
            val getMessageETObject : EditText
            val getRoomId : Int
            val getChannelId : Int

            fun updateRecycler()
        }
        interface Controller: BaseController{
            fun destroy()

            fun sendMessage()
            fun sendPost()
            fun sendMedia()

            fun createChannelRoom()
            fun retrieveChatHistory()

            fun getMessages() : MutableList<Message>

            fun onPictureOnlyResult(requestCode: Int, resultCode: Int, data: Intent?)
            fun onPicturePostResult(requestCode: Int, resultCode: Int, data: Intent?)
        }
    }

    interface MyChannelsList{
        interface View: BaseView<Controller>{
            fun addChannel(channel: Channel)
            fun updateRecycler()
        }
        interface Controller: BaseController{
            fun retrieveChannels()
        }
    }

    interface ChannelShow{
        interface View: BaseView<Controller>{
            fun addToChannels(channels: Array<ChannelShowNest>)
            fun addToPosts(list: Array<Message>)

            fun setUpPostRecycler()
            fun setUpTopRecycler()

            fun onLike(messageId: String)
        }
        interface Controller: BaseController{
            fun loadChannel(roomID: Int)

            fun likePost(messageId: String)
            fun commentPost(messageId: String)


        }
    }


}