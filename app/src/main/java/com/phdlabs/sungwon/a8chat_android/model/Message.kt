package com.phdlabs.sungwon.a8chat_android.model

import com.phdlabs.sungwon.a8chat_android.model.user.User
import com.phdlabs.sungwon.a8chat_android.model.user.UserNest
import java.util.*

/**
 * Created by SungWon on 10/23/2017.
 */
class Message private constructor(){

    var id: String? = null
    var userId: String? = null
    var message: String? = null
    var roomId: String? = null
    var type: String? = null
    var language: String? = null
    var name: String? = null
    var userAvatar: String? = null
    var createdAt: Date? = null
    var updatedAt: Date? = null
    var post: Boolean? = null
    var fileNames: Array<String>? = null
    var subRoom_id: String? = null
    var original_message_id: String? = null
    val mediaArray = mutableListOf<MediaDetailNest>()
    var user: UserNest? = null
    var comments: Int? = null
    var likes: Int? = null
    var channelInfo: Channel? = null
    var contactInfo: User? = null
    var locationInfo: LocationInfoNest? = null

//    @Transient
    var timeDisplayed: Boolean = false

    companion object {
        val TYPE_STRING = "string"
        val TYPE_MEDIA = "media"
        val TYPE_CONTACT = "contact"
        val TYPE_LOCATION = "location"
        val TYPE_FILE = "file"
        val TYPE_CHANNEL = "channel"
        val TYPE_MONEY = "money"
        val TYPE_POST = "post"
    }

    class Builder(private val id: String, private val mType: String, private val mUserId: String, private val mRoomId: String) {
        private var mMessage: String? = null
        private var mChannel: Channel? = null
        private var mContact: User? = null
        private var mMedia: String? = null

        fun message(message: String?): Builder {
            mMessage = message
            return this
        }

        fun channel(channel: Channel): Builder {
            mChannel = channel
            return this
        }

        fun contact(contact: User): Builder {
            mContact = contact
            return this
        }

        fun media(media: String): Builder {
            mMedia = media
            return this
        }

        fun build(): Message {
            val message = Message()
            message.type = mType
            message.userId = mUserId
            message.roomId = mRoomId
            message.message = mMessage
            message.channelInfo = mChannel
            message.contactInfo = mContact
            message.addMedia(MediaDetailNest(mMedia, null))
            return message
        }
    }

    fun addMedia(media: MediaDetailNest){
        mediaArray.add(media)
    }
}