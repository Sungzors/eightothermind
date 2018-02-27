package com.phdlabs.sungwon.a8chat_android.structure.channel

import android.content.Intent
import android.widget.EditText
import com.phdlabs.sungwon.a8chat_android.api.data.ChannelPostData
import com.phdlabs.sungwon.a8chat_android.model.channel.ChannelShowNest
import com.phdlabs.sungwon.a8chat_android.model.channel.Channel
import com.phdlabs.sungwon.a8chat_android.model.media.Media
import com.phdlabs.sungwon.a8chat_android.model.message.Message
import com.phdlabs.sungwon.a8chat_android.structure.application.Application
import com.phdlabs.sungwon.a8chat_android.structure.channel.create.ChannelCreateActivity
import com.phdlabs.sungwon.a8chat_android.structure.channel.mychannel.MyChannelActivity
import com.phdlabs.sungwon.a8chat_android.structure.core.BaseController
import com.phdlabs.sungwon.a8chat_android.structure.core.BaseView

/**
 * Created by SungWon on 11/30/2017.
 * Updated by JPAM on 26/01/2018
 */
interface ChannelContract {

    interface Create {
        interface View : BaseView<Controller> {
            /*UI*/
            val getActivity: ChannelCreateActivity
            /*Camera||Photo*/
            fun setChannelImage(filePath: String)
            /*Media*/
            fun getMedia(media: Media)
            /*LifeCycle*/
            fun onCreateChannel(chanId: Int?, chanName: String?, roomId: Int?, ownerId: Int?)
        }

        interface Controller : BaseController {
            fun getUserId(callback: (Int?) -> Unit)
            fun createChannel(channelPostData: ChannelPostData)
            fun onPictureResult(requestCode: Int, resultCode: Int, data: Intent?)
            fun showPicture()
        }
    }

    interface PostShow {
        interface View : BaseView<Controller> {
            fun setUpRecycler()
            val getChannelId: Int?
            val getMessageId: Int?
            fun onLike()
        }

        interface Controller : BaseController {
            fun likePost(messageId: Int?, userId: Int?)
            fun commentPost(messageId: String)
        }
    }

    interface MyChannel {
        interface View : BaseView<Controller> {
            val get8Application: Application
            val getActivity: MyChannelActivity
            val getMessageET: String
            val getMessageETObject: EditText
            val getRoomId: Int
            val getChannelId: Int

            fun updateRecycler()
        }

        interface Controller : BaseController {
            fun destroy()

            fun sendMessage()
            fun sendPost()
            fun sendMedia()

            fun createChannelRoom()
            fun retrieveChatHistory()

            fun getMessages(): MutableList<Message>

            fun onPictureOnlyResult(requestCode: Int, resultCode: Int, data: Intent?)
            fun onPicturePostResult(requestCode: Int, resultCode: Int, data: Intent?)
        }
    }

    interface MyChannelsList {
        interface View : BaseView<Controller> {
            fun addChannel(channel: Channel)
            fun updateRecycler()
        }

        interface Controller : BaseController {
            fun retrieveChannels()
        }
    }

    interface ChannelShow {
        interface View : BaseView<Controller> {
            fun addToChannels(channels: Array<ChannelShowNest>)
            fun addToPosts(list: Array<Message>)

            fun setUpPostRecycler()
            fun setUpTopRecycler()

            fun onLike(messageId: Int?)
        }

        interface Controller : BaseController {
            fun loadChannel(roomID: Int)

            fun likePost(messageId: Int?)
            fun commentPost(messageId: Int?)


        }
    }


}