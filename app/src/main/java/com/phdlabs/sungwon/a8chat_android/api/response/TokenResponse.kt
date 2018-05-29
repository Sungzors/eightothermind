package com.phdlabs.sungwon.a8chat_android.api.response

import com.phdlabs.sungwon.a8chat_android.model.user.User

/**
 * Created by JPAM on 12/21/17.
 */
class TokenResponse : ErrorResponse() {
    /*Token used to store a Realm Token*/
    internal var token: String? = null
    /*Realm user used to update local cache -> Verified*/
    internal val user: User? = null
}
