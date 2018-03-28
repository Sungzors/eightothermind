package com.phdlabs.sungwon.a8chat_android.model.notifications

/**
 * Created by JPAM on 3/21/18.
 * New Post in Followed Channel
 */
class PostPayload {
    var messageId: String? = null
    var userId: String? = null
    var roomId: String? = null
    var message_type: String? = null
    var notification_type: String? = null
    var badges: String? = null
}