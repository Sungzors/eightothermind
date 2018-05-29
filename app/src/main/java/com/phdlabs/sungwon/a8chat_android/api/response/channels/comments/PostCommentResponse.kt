package com.phdlabs.sungwon.a8chat_android.api.response.channels.comments

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.phdlabs.sungwon.a8chat_android.api.response.ErrorResponse
import com.phdlabs.sungwon.a8chat_android.model.channel.NewlyCreatedComment

/**
 * Created by paix on 4/18/18.
 */
class PostCommentResponse : ErrorResponse() {

    @SerializedName("newlyCreatedComment")
    @Expose
    var newlyCreatedComment: NewlyCreatedComment? = null
}