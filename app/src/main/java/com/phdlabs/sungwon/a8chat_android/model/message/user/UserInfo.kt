package com.phdlabs.sungwon.a8chat_android.model.message.user

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.RealmClass

/**
 * Created by paix on 1/31/18.
 * [UserInfo] provides specific user information
 * only for the [Message] @Realm model
 */
@RealmClass
open class UserInfo : RealmObject() {

    @SerializedName("avatar")
    @Expose
    var avatar: String? = null

    @SerializedName("first_name")
    @Expose
    var first_name: String? = null

    @SerializedName("last_name")
    @Expose
    var last_name: String? = null

    @SerializedName("profile_picture_string")
    @Expose
    var profile_picture_string: String? = null

}