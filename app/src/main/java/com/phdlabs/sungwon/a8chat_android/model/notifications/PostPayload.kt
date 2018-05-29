package com.phdlabs.sungwon.a8chat_android.model.notifications

/**
 * Created by JPAM on 3/21/18.
 * New Post in Followed Channel
 */
class PostPayload {
    var messageId: String? = null //Message -> Post ID
    var userId: String? = null //Channel owner
    var roomId: String? = null // Room id for the channel
    var message_type: String? = null // MessageType
    var notification_type: String? = null
    var badges: String? = null
}