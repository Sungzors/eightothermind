package com.phdlabs.sungwon.a8chat_android.model.channel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.Index
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

/**
 * Created by paix on 1/29/18.
 */
@RealmClass
open class Channel : RealmObject() {

    @PrimaryKey
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("unique_id")
    @Expose
    var unique_id: String? = null

    @SerializedName("room_id")
    @Expose
    @Index
    var room_id: Int? = null

    @SerializedName("description")
    @Expose
    var description: String? = null

    @SerializedName("color")
    @Expose
    var color: String? = null

    @SerializedName("background")
    @Expose
    var background: String? = null

    @SerializedName("add_to_profile")
    @Expose
    var add_to_profile: Boolean? = null

    @SerializedName("user_creator_id")
    @Expose
    var user_creator_id: String? = null

    @SerializedName("isRead")
    @Expose
    var isRead: Boolean? = false

    @SerializedName("profile_picture_string")
    @Expose
    var profile_picture_string: String? = null

    @SerializedName("avatar")
    @Expose
    var avatar: String? = null

    @SerializedName("createdAt")
    @Expose
    var createdAt: String? = null

}