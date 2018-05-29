package com.phdlabs.sungwon.a8chat_android.api.response.channels.edit

import com.phdlabs.sungwon.a8chat_android.api.response.ErrorResponse
import com.phdlabs.sungwon.a8chat_android.model.channel.Channel

/**
 * Created by JPAM on 3/19/18.
 */
class ChannelEditResponse : ErrorResponse() {
    internal var channel: Channel? = null
}