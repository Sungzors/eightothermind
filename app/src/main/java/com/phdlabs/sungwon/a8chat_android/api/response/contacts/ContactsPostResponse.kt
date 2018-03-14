package com.phdlabs.sungwon.a8chat_android.api.response.contacts

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.phdlabs.sungwon.a8chat_android.api.response.ErrorResponse
import com.phdlabs.sungwon.a8chat_android.model.contacts.LocalContact

/**
 * Created by JPAM on 2/14/18.
 * [ContactsPostResponse] used to serialize valid contact information
 * when contacts are signed up to Eight
 */
class ContactsPostResponse: ErrorResponse() {

    @SerializedName("userContactsWithEightAccount")
    @Expose
    var userContactsWithEightAccount: Array<String>? = null

    @SerializedName("undeterminedPhoneNumbers")
    @Expose
    var undeterminedPhoneNumbers: Array<String>? = null

}