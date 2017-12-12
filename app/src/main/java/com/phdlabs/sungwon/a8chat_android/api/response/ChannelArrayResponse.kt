package com.phdlabs.sungwon.a8chat_android.api.response

import com.phdlabs.sungwon.a8chat_android.model.Channel

/**
 * Created by SungWon on 12/6/2017.
 */
class ChannelArrayResponse: ErrorResponse(){
    internal var channels: Array<Channel>? = null
}