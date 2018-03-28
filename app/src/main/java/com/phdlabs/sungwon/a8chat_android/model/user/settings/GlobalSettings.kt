package com.phdlabs.sungwon.a8chat_android.model.user.settings

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.phdlabs.sungwon.a8chat_android.model.contacts.Contact
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

/**
 * Created by JPAM on 3/24/18.
 * User saved settings
 */
@RealmClass
open class GlobalSettings : RealmObject() {

    @PrimaryKey
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("language")
    @Expose
    var language: String? = null

    @SerializedName("readReceipts")
    @Expose
    var readReceipts: Boolean? = null

    @SerializedName("blockedUsers")
    @Expose
    var blockedUsers: RealmList<Contact>? = null

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