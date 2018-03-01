package com.phdlabs.sungwon.a8chat_android.model.user

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.phdlabs.sungwon.a8chat_android.model.realmNative.RealmInt
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.Index
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

/**
 * Created by SungWon on 1/25/2018.
 * Updated by JPAM 02/22/2018
 */
@RealmClass
open class UserRooms : RealmObject() {


    @PrimaryKey
    @SerializedName("userId")
    @Expose
    var userId: Int? = null

    @SerializedName("roomId")
    @Expose
    @Index
    var roomId: Int? = null

    @SerializedName("incognito_mode")
    @Expose
    var incognito_mode: Boolean? = null

    @SerializedName("favorite")
    @Expose
    var favorite: Boolean? = null

    @SerializedName("last_time_in_room")
    @Expose
    var last_time_in_room: String? = null

    @SerializedName("unread_messages")
    @Expose
    var unread_messages: Boolean? = null

    @SerializedName("subRoom_id")
    @Expose
    var subRoom_id: String? = null

    @SerializedName("message_notifications")
    @Expose
    var message_notifications: Boolean? = null

    @SerializedName("user_added_notifications")
    @Expose
    var user_added_notifications: Boolean? = null

    @SerializedName("entered_room_quantity")
    @Expose
    var entered_room_quantity: Int? = null

    @SerializedName("current_room")
    @Expose
    var current_room: Boolean? = null

    @SerializedName("read_receipt")
    @Expose
    var read_receipt: Boolean? = null

    @SerializedName("createdAt")
    @Expose
    var createdAt: String? = null

    @SerializedName("updatedAt")
    @Expose
    var updatedAt: String? = null

    @SerializedName("messages_not_to_display")
    @Expose
    var messages_not_to_display: RealmList<RealmInt?>? = null

}