package com.phdlabs.sungwon.a8chat_android.model.notifications

/**
 * Created by paix on 3/21/18.
 * User Added to a Room Notification
 */
class UserAdedPayload() : EightNotification() {
    var roomId: String? = null
    var groupChat_event_or_channel_id: String? = null
    var userIds_added: String? = null
}