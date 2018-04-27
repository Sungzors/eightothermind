package com.phdlabs.sungwon.a8chat_android.model.channel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by paix on 4/18/18.
 */
class NewlyCreatedComment {

    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("original_comment")
    @Expose
    var original_comment: String? = null

    @SerializedName("roomId")
    @Expose
    var roomId: Int? = null

    @SerializedName("userId")
    @Expose
    var userId: Int? = null

    @SerializedName("messageId")
    @Expose
    var messageId: Int? = null

    @SerializedName("isTranslatable")
    @Expose
    var isTranslatable: Boolean? = null

    @SerializedName("english_comment")
    @Expose
    var english_comment: String? = null

    @SerializedName("updatedAt")
    @Expose
    var updatedAt: String? = null

    @SerializedName("createdAt")
    @Expose
    var createdAt: String? = null

    @SerializedName("portuguese_comment")
    @Expose
    var portuguese_comment: String? = null

    @SerializedName("spanish_comment")
    @Expose
    var spanish_comment: String? = null

    @SerializedName("french_comment")
    @Expose
    var french_comment: String? = null

    @SerializedName("arabic_comment")
    @Expose
    var arabic_comment: String? = null

    @SerializedName("deletedAt")
    @Expose
    var deletedAt: String? = null


}