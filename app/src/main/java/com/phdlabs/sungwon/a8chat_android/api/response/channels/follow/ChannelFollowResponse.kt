package com.phdlabs.sungwon.a8chat_android.api.response.channels.follow

import com.phdlabs.sungwon.a8chat_android.api.response.ErrorResponse
import com.phdlabs.sungwon.a8chat_android.model.channel.Channel

/**
 * Created by SungWon on 1/5/2018.
 * Updated by JPAM on 02/27/2018
 */
class ChannelFollowResponse: ErrorResponse(){
    internal var channels:  Array<ChannelFollowNestResponse?>? = null
}