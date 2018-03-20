package com.phdlabs.sungwon.a8chat_android.structure.channel

import android.content.Intent
import android.net.Uri
import android.widget.EditText
import com.phdlabs.sungwon.a8chat_android.api.data.channel.ChannelPostData
import com.phdlabs.sungwon.a8chat_android.model.channel.Comment
import com.phdlabs.sungwon.a8chat_android.model.channel.ChannelShowNest
import com.phdlabs.sungwon.a8chat_android.model.channel.Channel
import com.phdlabs.sungwon.a8chat_android.model.media.Media
import com.phdlabs.sungwon.a8chat_android.model.message.Message
import com.phdlabs.sungwon.a8chat_android.model.room.Room
import com.phdlabs.sungwon.a8chat_android.structure.application.Application
import com.phdlabs.sungwon.a8chat_android.structure.channel.create.ChannelCreateActivity
import com.phdlabs.sungwon.a8chat_android.structure.channel.createPost.CreatePostActivity
import com.phdlabs.sungwon.a8chat_android.structure.channel.mychannel.MyChannelActivity
import com.phdlabs.sungwon.a8chat_android.structure.channel.postshow.ChannelPostShowActivity
import com.phdlabs.sungwon.a8chat_android.structure.core.BaseController
import com.phdlabs.sungwon.a8chat_android.structure.core.BaseView
import java.io.File

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

            fun onUpdateChannel(chanId: Int?, chanName: String?, roomId: Int?, ownerId: Int?)
        }

        interface Controller : BaseController {
            /*Create Channel*/
            fun getUserId(callback: (Int?) -> Unit)

            fun createChannel(channelPostData: ChannelPostData)
            fun onPictureResult(requestCode: Int, resultCode: Int, data: Intent?)
            fun showPicture()

            /*Edit Channel*/
            fun getRoomInfo(id: Int, callback: (Room?) -> Unit)

            fun getChannelInfo(id: Int, callback: (Channel?) -> Unit)
            fun updateChannel(channelId: Int, channelPostData: ChannelPostData)
        }
    }

    interface PostShow {
        interface View : BaseView<Controller> {
            val get8Application: Application
            val getActivity: ChannelPostShowActivity
            val getChannelId: Int?
            val getMessageId: Int?
            fun updateCommentsRecycler()
        }

        interface Controller : BaseController {
            fun commentPost(messageId: String, comment: String)
            fun pullPostComments(messageId: Int)
            fun getPostComments(): ArrayList<Comment>
            /*Like Post*/
            fun likePost(messageId: Int, unlike: Boolean)

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

            fun updateContentRecycler()
            fun updateFollowedChannelsRecycler()
        }

        interface Controller : BaseController {
            /*LifeCycle*/
            fun onCreate()

            fun destroy()

            /*String message*/
            fun sendMessage()

            /*File message*/
            fun sendFile(file: File, path: String)

            /*Get Followed Channels*/
            fun getFollowedChannels(): MutableList<Channel>?

            /*Pull Chat History*/
            fun retrieveChatHistory(refresh: Boolean)

            /*Chat History*/
            fun getMessages(): MutableList<Message>

            /*Picture Intent -> Post only media*/
            fun onPictureOnlyResult(requestCode: Int, resultCode: Int, data: Intent?)

            /*Create Post*/
            fun createPost(message: String?, filePaths: ArrayList<String>?)

            /*Like Post*/
            fun likePost(messageId: Int, unlike: Boolean)

            /*Keep Channel-Room socket connection if opening a post*/
            fun keepSocketConnection(keepConnection: Boolean)

            /*Read Files Permissions*/
            fun requestReadingExternalStorage()

            fun permissionResults(requestCode: Int, permissions: Array<out String>, grantResults: IntArray): Boolean
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

    /*CREATE POST*/
    interface CreatePost {

        interface View : BaseView<Controller> {
            var activity: CreatePostActivity
            fun refreshMediaAdapter()
            fun validatePost(): Boolean
            fun getPostData(): Pair<String, MutableList<Uri>>
        }

        interface Controller : BaseController {
            fun requestStoragePermissions()
            fun openMediaPicker()
            fun createPost()
        }
    }


}