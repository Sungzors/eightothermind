package com.phdlabs.sungwon.a8chat_android.api.response.channels.follow

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.phdlabs.sungwon.a8chat_android.api.response.ErrorResponse
import com.phdlabs.sungwon.a8chat_android.model.channel.Channel

/**
 * Created by paix on 2/27/18.
 */
class ChannelFollowNestResponse : ErrorResponse() {

    @SerializedName("channel")
    @Expose
    var channel: Channel? = null

    @SerializedName("unread_messages")
    @Expose
    var unread_messages: Boolean? = null

    @SerializedName("last_activity")
    @Expose
    var last_activity: String? = null

    @SerializedName("isPopular")
    @Expose
    var isPopular: Boolean? = null

}