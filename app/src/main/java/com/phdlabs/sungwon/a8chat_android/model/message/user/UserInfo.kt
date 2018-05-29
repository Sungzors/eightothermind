package com.phdlabs.sungwon.a8chat_android.model.message.user

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.RealmClass

/**
 * Created by JPAM on 1/31/18.
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

    //todo: Missing languages spoken Realm List... not sure if we should map that?

    /**
     * [hasFullName]
     * Verifies full name availability
     * @return Pair<Bool,String?> where
     * @Bool availability of full name
     * @String? Full name
     * */
    fun hasFullName(): Pair<Boolean, String?> {
        val firstName: String = this.first_name ?: ""
        val lastName: String = this.last_name ?: ""
        if (firstName.isBlank() || lastName.isBlank()) {
            return Pair(false, null)
        }
        val fullName: String = firstName + " " + lastName
        return Pair(true, fullName)
    }

}