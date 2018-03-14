package com.phdlabs.sungwon.a8chat_android.api.response.channels.search

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.phdlabs.sungwon.a8chat_android.api.response.ErrorResponse
import com.phdlabs.sungwon.a8chat_android.model.channel.Channel

/**
 * Created by paix on 3/13/18.
 */
class SearchChannelsResponse : ErrorResponse() {
    @SerializedName("channels")
    @Expose
    internal val channels: Array<Channel>? = null
}