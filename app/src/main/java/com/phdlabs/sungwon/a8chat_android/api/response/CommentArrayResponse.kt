package com.phdlabs.sungwon.a8chat_android.api.response

import com.phdlabs.sungwon.a8chat_android.model.channel.Comment

/**
 * Created by SungWon on 12/5/2017.
 */
class CommentArrayResponse: ErrorResponse(){
    internal var comments: Array<Comment>? = null
}