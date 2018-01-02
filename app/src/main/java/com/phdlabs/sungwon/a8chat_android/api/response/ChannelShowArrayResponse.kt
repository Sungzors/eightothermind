package com.phdlabs.sungwon.a8chat_android.api.response

import com.phdlabs.sungwon.a8chat_android.model.ChannelShowNest

/**
 * Created by SungWon on 12/21/2017.
 */
class ChannelShowArrayResponse: ErrorResponse(){
    internal var channels: Array<ChannelShowNest>? = null
}