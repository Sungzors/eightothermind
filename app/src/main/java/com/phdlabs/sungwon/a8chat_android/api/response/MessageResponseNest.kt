package com.phdlabs.sungwon.a8chat_android.api.response

import com.phdlabs.sungwon.a8chat_android.model.Message

/**
 * Created by SungWon on 10/30/2017.
 */
class MessageResponseNest {
    internal var unread: Array<Message>? = null
    internal var read: Array<Message>? = null
    internal var allMessages: Array<Message>? = null
}