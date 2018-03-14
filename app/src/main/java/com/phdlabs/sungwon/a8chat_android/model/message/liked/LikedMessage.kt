package com.phdlabs.sungwon.a8chat_android.model.message.liked

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.phdlabs.sungwon.a8chat_android.model.realmNative.RealmInt
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

/**
 * Created by JPAM on 3/8/18.
 * [LikedMessage]
 * Used to collect liked messages by the user on the [Channel] feed
 */
@RealmClass
open class LikedMessage: RealmObject() {

    @SerializedName("id")
    @Expose
    var id: Int? = null

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

    @SerializedName("original_message")
    @Expose
    var original_message: String? = null

    @SerializedName("type")
    @Expose
    var type: String? = null

    @SerializedName("locationInfo")
    @Expose
    var locationInfo: String? = null

    @SerializedName("channelId")
    @Expose
    var channelId: String? = null

    @SerializedName("contactId")
    @Expose
    var contactId: Int? = null

    @SerializedName("moneyInfo")
    @Expose
    var moneyInfo: String? = null

    @SerializedName("mediaIds")
    @Expose
    var mediaIds: RealmList<RealmInt>? = null

    @SerializedName("fileIds")
    @Expose
    var fileIds: RealmList<RealmInt>? = null

    @SerializedName("likes")
    @Expose
    var likes: Int? = null

    @SerializedName("comments")
    @Expose
    var comments: Int? = null

    @SerializedName("self_destruct")
    @Expose
    var self_destruct: Boolean? = null

    @SerializedName("minutes")
    @Expose
    var minutes: Int? = null

    @SerializedName("channelPostId")
    @Expose
    var channelPostId: Int? = null

    @SerializedName("createdAt")
    @Expose
    var createdAt: String? = null

    @SerializedName("post")
    @Expose
    var post: Boolean? = null

    @SerializedName("isTranslatable")
    @Expose
    var isTranslatable: Boolean? = null

    @SerializedName("updatedAt")
    @Expose
    var updatedAt: String? = null

    @SerializedName("deletedAt")
    @Expose
    var deletedAt: String? = null

    @SerializedName("userId")
    @Expose
    var userId: Int? = null

    @SerializedName("roomId")
    @Expose
    var roomId: Int? = null


}