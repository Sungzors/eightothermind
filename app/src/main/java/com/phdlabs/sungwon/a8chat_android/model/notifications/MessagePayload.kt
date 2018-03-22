package com.phdlabs.sungwon.a8chat_android.model.notifications

/**
 * Created by paix on 3/21/18.
 * New Message
 */
class MessagePayload : EightNotification() {
    var messageId: String? = null
    var userId: String? = null
    var roomId: String? = null
    var message_type: String? = null
}