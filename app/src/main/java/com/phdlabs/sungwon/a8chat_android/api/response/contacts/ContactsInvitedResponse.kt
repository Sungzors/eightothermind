package com.phdlabs.sungwon.a8chat_android.api.response.contacts

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.phdlabs.sungwon.a8chat_android.api.response.ErrorResponse

/**
 * Created by paix on 2/20/18.
 * [ContactsInvitedResponse] used to serialize valid contact information
 * when contacts are invited to Eight
 */

class ContactsInvitedResponse : ErrorResponse() {

    @SerializedName("successfullySentSMS")
    @Expose
    var successfullySentSMS: Array<String>? = null

    @SerializedName("unsuccessfullySentSMS")
    @Expose
    var unsuccessfullySentSMS: Array<String>? = null

}