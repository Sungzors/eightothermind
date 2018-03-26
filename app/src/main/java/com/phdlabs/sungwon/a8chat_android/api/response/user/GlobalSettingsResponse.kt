package com.phdlabs.sungwon.a8chat_android.api.response.user

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.phdlabs.sungwon.a8chat_android.api.response.ErrorResponse
import com.phdlabs.sungwon.a8chat_android.model.contacts.Contact
import io.realm.RealmList

/**
 * Created by paix on 3/24/18.
 * Global User Settings
 */
class GlobalSettingsResponse : ErrorResponse() {

    @SerializedName("language")
    @Expose
    var language: String? = null

    @SerializedName("readReceipts")
    @Expose
    var readReceipts: Boolean? = null

    @SerializedName("blockedUsers")
    @Expose
    var blockedUsers: ArrayList<Contact>? = null

    @SerializedName("message_notifications")
    @Expose
    var message_notifications: Boolean? = null

    @SerializedName("like_notifications")
    @Expose
    var like_notifications: Boolean? = null

    @SerializedName("comment_notifications")
    @Expose
    var comment_notifications: Boolean? = null

    @SerializedName("user_added_notifications")
    @Expose
    var user_added_notifications: Boolean? = null

}