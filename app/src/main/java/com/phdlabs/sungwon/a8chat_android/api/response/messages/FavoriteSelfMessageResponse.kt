package com.phdlabs.sungwon.a8chat_android.api.response.messages

import com.phdlabs.sungwon.a8chat_android.api.response.ErrorResponse
import com.phdlabs.sungwon.a8chat_android.model.message.Message

/**
 * Created by SungWon on 3/29/2018.
 */
class FavoriteSelfMessageResponse: ErrorResponse(){
    internal val favoriteMessages: Array<Message>? = null
}