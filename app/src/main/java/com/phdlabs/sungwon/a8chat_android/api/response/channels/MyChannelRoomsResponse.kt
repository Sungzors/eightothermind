package com.phdlabs.sungwon.a8chat_android.api.response.channels

import com.phdlabs.sungwon.a8chat_android.api.response.ErrorResponse
import com.phdlabs.sungwon.a8chat_android.model.channel.ChannelShowNest

/**
 * Created by SungWon on 12/21/2017.
 * Response object called @channels referring to the [Room]
 * User channels are nested & accessed using [ChannelShowNest]
 */
class MyChannelRoomsResponse : ErrorResponse(){
    //channels represents rooms
    internal var channels: Array<ChannelShowNest>? = null
}