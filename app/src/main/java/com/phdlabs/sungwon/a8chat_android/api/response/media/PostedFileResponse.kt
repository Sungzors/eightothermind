package com.phdlabs.sungwon.a8chat_android.api.response.media

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by paix on 3/9/18.
 * [PostedFileResponse]
 */
class PostedFileResponse {

    @SerializedName("createdAt")
    @Expose
    var createdAt: String? = null

    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("original_message")
    @Expose
    var original_message: String? = null

    @SerializedName("roomId")
    @Expose
    var roomId: Int? = null

    @SerializedName("userId")
    @Expose
    var userId: Int? = null

    @SerializedName("type")
    @Expose
    var type: String? = null

    @SerializedName("self_destruct")
    @Expose
    var self_destruct: Boolean? = null

    @SerializedName("minutes")
    @Expose
    var minutes: Int? = null

    @SerializedName("comments")
    @Expose
    var comments: Int? = null

    @SerializedName("likes")
    @Expose
    var likes: Int? = 0

    @SerializedName("post")
    @Expose
    var post: Boolean? = null

    @SerializedName("isTranslatable")
    @Expose
    var isTranslatable: Boolean? = null

    @SerializedName("fileIds")
    @Expose
    var fileIds: Array<Int>? = null

    @SerializedName("updatedAt")
    @Expose
    var updatedAt: String? = null

    @SerializedName("english_message")
    @Expose
    var english_message: String? = null

    @SerializedName("arabic_message")
    @Expose
    var arabic_message: String? = null

    @SerializedName("french_message")
    @Expose
    var french_message: String? = null

    @SerializedName("spanish_message")
    @Expose
    var spanish_message: String? = null

    @SerializedName("portuguese_message")
    @Expose
    var portuguese_message: String? = null

    @SerializedName("channelId")
    @Expose
    var channelId: Int? = null

    @SerializedName("channelPostId")
    @Expose
    var channelPostId: Int? = null

    @SerializedName("deletedAt")
    @Expose
    var deletedAt: String? = null

}