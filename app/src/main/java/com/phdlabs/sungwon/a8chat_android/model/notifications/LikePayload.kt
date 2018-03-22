package com.phdlabs.sungwon.a8chat_android.model.notifications

/**
 * Created by paix on 3/21/18.
 * Someone Liked a Channel's Post
 */
class LikePayload {
    var channel_creator_id: String? = null
    var user_liking_id: String? = null
    var messageId: String? = null
    var notification_type: String? = null
    var badge: String? = null
}