package com.phdlabs.sungwon.a8chat_android.model.channel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.phdlabs.sungwon.a8chat_android.model.message.user.UserInfo
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

/**
 * Created by SungWon on 12/4/2017.
 * Updated by JPAM on 03/7/2018
 */
@RealmClass
open class Comment : RealmObject() {

    @SerializedName("id")
    @Expose
    @PrimaryKey
    var id: Int? = null

    @SerializedName("isTranslatable")
    @Expose
    var isTranslatable: Boolean? = null

    @SerializedName("createdAt")
    @Expose
    var createdAt: String? = null

    @SerializedName("updatedAt")
    @Expose
    var updatedAt: String? = null

    @SerializedName("deletedAt")
    @Expose
    var deletedAt: String? = null

    @SerializedName("messageId")
    @Expose
    var messageId: Int? = null

    @SerializedName("userId")
    @Expose
    var userId: Int? = null

    @SerializedName("roomId")
    @Expose
    var roomId: Int? = null

    @SerializedName("user")
    @Expose
    var user: UserInfo? = null

    @SerializedName("comment")
    @Expose
    var comment: String? = null

    @SerializedName("language")
    @Expose
    var language: String? = null

}