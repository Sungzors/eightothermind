package com.phdlabs.sungwon.a8chat_android.api.response.channels.broadcast

import com.phdlabs.sungwon.a8chat_android.api.response.ErrorResponse
import com.phdlabs.sungwon.a8chat_android.model.message.Message

/**
 * Created by JPAM on 4/10/18.
 * Returns the Message Info created inside the channel so the user can start a live broadcast
 */
class StartBroadcastResponse : ErrorResponse() {

    var newlyCreatedMsg: Message? = null

}