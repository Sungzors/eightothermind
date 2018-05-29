package com.phdlabs.sungwon.a8chat_android.model.room

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import java.util.*

/**
 * Created by SungWon on 2/21/2018.
 */
@RealmClass
open class GroupChatInfo : RealmObject() {
    @PrimaryKey
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("avatar")
    @Expose
    var avatar: String? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("profile_picture_string")
    @Expose
    var profile_picture_string: String? = null

    @SerializedName("createdAt")
    @Expose
    var createdAt: Date? = null

    @SerializedName("updatedAt")
    @Expose
    var updatedAt: Date? = null

    @SerializedName("roomId")
    @Expose
    var roomId: Int? = null

    @SerializedName("adminId")
    @Expose
    var adminId: Int? = null

}