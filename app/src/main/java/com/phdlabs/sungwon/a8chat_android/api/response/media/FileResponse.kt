package com.phdlabs.sungwon.a8chat_android.api.response.media

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.phdlabs.sungwon.a8chat_android.api.response.ErrorResponse
import com.phdlabs.sungwon.a8chat_android.model.message.Message

/**
 * Created by paix on 3/9/18.
 * Shared File Response
 */
class FileResponse : ErrorResponse() {
    @SerializedName("messageInfo")
    @Expose
    var newlyCreatedMsg: PostedFileResponse? = null

}