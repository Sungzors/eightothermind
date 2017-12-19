package com.phdlabs.sungwon.a8chat_android.structure.channel

import com.phdlabs.sungwon.a8chat_android.api.data.PostChannelData
import com.phdlabs.sungwon.a8chat_android.model.Channel
import com.phdlabs.sungwon.a8chat_android.model.Message
import com.phdlabs.sungwon.a8chat_android.structure.core.BaseController
import com.phdlabs.sungwon.a8chat_android.structure.core.BaseView

/**
 * Created by SungWon on 11/30/2017.
 */
interface ChannelContract {
    interface Create {
        interface View: BaseView<Controller>{
            fun finishActivity()
        }
        interface Controller: BaseController{

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
            fun addToChannels(channel: Channel)
            fun addToPosts(list: Array<Message>)
            fun setUpPostRecycler()
            fun onLike(messageId: String)
        }
        interface Controller: BaseController{
            fun loadChannel(roomID: Int)
            fun likePost(messageId: String)
            fun commentPost(messageId: String)
        }
    }
}