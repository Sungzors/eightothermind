package com.phdlabs.sungwon.a8chat_android.api.response.channels.broadcast

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.phdlabs.sungwon.a8chat_android.api.response.ErrorResponse
import com.phdlabs.sungwon.a8chat_android.model.message.Message

/**
 * Created by JPAM on 4/10/18.
 * Live Video Broadcast end response
 */
class EndBroadcastResponse : ErrorResponse() {

    @SerializedName("message")
    @Expose
    var message: Message? = null

}