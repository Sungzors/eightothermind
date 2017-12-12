package com.phdlabs.sungwon.a8chat_android.model

/**
 * Created by SungWon on 12/4/2017.
 */
data class Comment(
        val comment: String,
        val userId: String,
        val name: String
){
    var userAvatar: String? = null
    var messageId: String? = null
    var subRoom_id: String? = null
    var original_comment_id: String? = null
    var language: String? = null
    var createdAt: String? = null
}