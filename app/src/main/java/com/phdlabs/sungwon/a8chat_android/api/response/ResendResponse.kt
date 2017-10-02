package com.phdlabs.sungwon.a8chat_android.api.response

import java.util.*

/**
 * Created by SungWon on 9/27/2017.
 */
class ResendResponse: ErrorResponse(){
    internal var passcode: String? = null
    internal var passcode_expiration: Date? = null
}