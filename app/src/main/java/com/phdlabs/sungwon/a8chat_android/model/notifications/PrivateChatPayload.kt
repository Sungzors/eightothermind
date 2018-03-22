package com.phdlabs.sungwon.a8chat_android.model.notifications

/**
 * Created by paix on 3/21/18.
 * Private Chat Created -> User can start chatting with another user privately
 */
class PrivateChatPayload : EightNotification() {
    var roomId: String? = null
    var userIds_in_private_chat: String? = null
}