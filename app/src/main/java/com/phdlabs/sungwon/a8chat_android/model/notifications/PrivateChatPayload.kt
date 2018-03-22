package com.phdlabs.sungwon.a8chat_android.model.notifications

/**
 * Created by paix on 3/21/18.
 * Private Chat Created -> User can start chatting with another user privately
 */
class PrivateChatPayload {
    var roomId: String? = null
    var userIds_in_private_chat: String? = null
    var notification_type: String? = null
    var badge: String? = null
}