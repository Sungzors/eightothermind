package com.phdlabs.sungwon.a8chat_android.api.response.channels.post

import com.phdlabs.sungwon.a8chat_android.api.response.ErrorResponse
import com.phdlabs.sungwon.a8chat_android.model.message.Message

/**
 * Created by paix on 3/5/18.
 * [ChannelPostResponse] maps the User's post inside an owned [Channel]
 */
class ChannelPostResponse: ErrorResponse() {

    var messageInfo: Message? = null

}