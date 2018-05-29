package com.phdlabs.sungwon.a8chat_android.api.response.messages

import com.phdlabs.sungwon.a8chat_android.api.response.ErrorResponse
import com.phdlabs.sungwon.a8chat_android.model.message.Message

/**
 * Created by SungWon on 3/20/2018.
 */
class FavoriteMessageResponse: ErrorResponse(){
    internal val messages: Array<Message>? = null
}