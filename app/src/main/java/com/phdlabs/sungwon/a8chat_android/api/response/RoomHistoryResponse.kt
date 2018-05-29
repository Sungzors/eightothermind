package com.phdlabs.sungwon.a8chat_android.api.response

import com.phdlabs.sungwon.a8chat_android.api.response.messages.MessageResponseNest

/**
 * Created by SungWon on 10/30/2017.
 */
class RoomHistoryResponse: ErrorResponse(){
    internal var messages: MessageResponseNest? = null
}