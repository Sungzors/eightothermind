package com.phdlabs.sungwon.a8chat_android.model.notifications

/**
 * Created by JPAM on 3/21/18.
 * Someone commented on the user's Post
 */
class CommentPayLoad {
    var channel_creator_id: String? = null
    var user_commenting_id: String? = null
    var post_commented_on_id: String? = null
    var notification_type: String? = null
    var badges: String? = null
}