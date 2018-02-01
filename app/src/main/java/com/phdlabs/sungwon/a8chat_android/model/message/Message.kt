package com.phdlabs.sungwon.a8chat_android.model.message

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.phdlabs.sungwon.a8chat_android.model.channel.Channel
import com.phdlabs.sungwon.a8chat_android.model.contacts.Contact
import com.phdlabs.sungwon.a8chat_android.model.files.File
import com.phdlabs.sungwon.a8chat_android.model.media.Media
import com.phdlabs.sungwon.a8chat_android.model.message.location.LocationInfo
import com.phdlabs.sungwon.a8chat_android.model.message.money.MoneyInfo
import com.phdlabs.sungwon.a8chat_android.model.message.user.UserInfo
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.Ignore
import io.realm.annotations.RealmClass

/**
 * Created by JPAM on 1/31/2018
 * [Message] @Realm class used for caching messages.
 *
 * @Warning Remember to refresh realm message data & do
 * manual garbage collection to avoid huge data sets & lower
 * memory performance. This class can hold too much data.
 *
 */
@RealmClass
open class Message : RealmObject() {

    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    /**
     * [type] can be addressed with
     * [Constants.MessageTypes] for Queries
     * */
    @SerializedName("type")
    @Expose
    var type: String? = null

    @SerializedName("language")
    @Expose
    var language: String? = null

    /**
     * [locationInfo] maps [LocationInfo]
     * @see RealmClass
     * */
    @SerializedName("locationInfo")
    @Expose
    var locationInfo: LocationInfo? = null

    /**
     * [channelInfo] maps [Channel]
     * @see RealmClass
     * */
    @SerializedName("channelInfo")
    @Expose
    var channelInfo: Channel? = null

    /**
     * [contactInfo] maps [ContactInfo]
     * @see RealmClass
     * */
    @SerializedName("contactInfo")
    @Expose
    var contactInfo: Contact? = null

    /**
     * [moneyInfo] maps [MoneyInfo]
     * @see RealmClass
     * @warning This attribute is currently @Deprecated
     * */
    @SerializedName("moneyInfo")
    @Expose
    @Ignore
    var moneyInfo: MoneyInfo? = null

    /**
     * [mediaArray] maps [Media]
     * @see RealmClass
     * */
    @SerializedName("mediaArray")
    @Expose
    var mediaArray: RealmList<Media>? = null

    /**
     * [files] maps @RealmList of [File]
     * @see RealmClass
     * */
    @SerializedName("files")
    @Expose
    var files:RealmList<File>? = null

    /**
     * [likes]
     * The number of likes a particular message on a channel has.
     * This attribute will be more than zero only if the message is in a room channel
     * */
    @SerializedName("likes")
    @Expose
    var likes: Int? = null

    /**
     * [comments]
     * The number of comments a particular message on a channel has.
     * This attribute will be more than zero only if the message is in a room channel
     * */
    @SerializedName("comments")
    @Expose
    var comments: Int? = null

    @SerializedName("self_destruct")
    @Expose
    var self_destruct: Boolean? = null

    /**
     * [minutes] is the number of minutes a post will be scheduled for in the future
     * */
    @SerializedName("minutes")
    @Expose
    var minutes: Int? = null

    /**
     * [channelPostInfo] is a Channel Post a user is sharing with another user.
     * It can have any of the attributes of a [Message]
     * */
    @SerializedName("channelPostInfo")
    @Expose
    var channelPostInfo: Message? = null

    @SerializedName("createdAt")
    @Expose
    var createdAt: String? = null

    @SerializedName("post")
    @Expose
    var post: Boolean? = null

    @SerializedName("updatedAt")
    @Expose
    var updatedAt: String? = null

    @SerializedName("deletedAt")
    @Expose
    var deletedAt: String? = null

    @SerializedName("original_message_id")
    @Expose
    var original_message_id: Int? = null

    /**
     * [userId] of the [User] [Message] author
     * Not nested under the [user] property
     * because this field is not variable
     * */
    @SerializedName("userId")
    @Expose
    var userId: Int? = null

    @SerializedName("subRoom_id")
    @Expose
    var subRoom_id: Int? = null

    @SerializedName("roomId")
    @Expose
    var roomId: Int? = null

    @SerializedName("user")
    @Expose
    var user: UserInfo? = null

    /*Transient*/
    var timeDisplayed: Boolean = false

    /*Message methods*/
    fun getUserName(): String? = (user?.first_name + " " + user?.last_name)

}