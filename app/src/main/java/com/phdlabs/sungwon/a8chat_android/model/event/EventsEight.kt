package com.phdlabs.sungwon.a8chat_android.model.event

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.phdlabs.sungwon.a8chat_android.model.event.location.EventLocation
import com.phdlabs.sungwon.a8chat_android.model.message.Message
import io.realm.RealmObject
import io.realm.annotations.Ignore
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import java.util.*

/**
 * Created by SungWon on 11/21/2017.
 * Updated by JPAM on 01/30/2017
 */

@RealmClass
open class EventsEight() : RealmObject() {

    @PrimaryKey
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("distribution")
    @Expose
    var distribution: String? = null

    @SerializedName("user_creator_id")
    @Expose
    var user_creator_id: Int? = null

    @SerializedName("locks_after_24_hours")
    @Expose
    var locks_after_24_hours: Boolean = false

    @SerializedName("location")
    @Expose
    var location: EventLocation? = null

    @SerializedName("location_name")
    @Expose
    var location_name: String? = null

    @SerializedName("profile_picture_string")
    @Expose
    var profile_picture_string: String? = null

    @SerializedName("room_id")
    @Expose
    var room_id: Int? = null

    @SerializedName("isRead")
    @Expose
    var isRead: Boolean = true

    @SerializedName("last_activity")
    @Expose
    var last_activity: Date? = null

    @SerializedName("avatar")
    @Expose
    var avatar: String? = null

    @SerializedName("updatedAt")
    @Expose
    var updatedAt: String? = null

    @SerializedName("createdAt")
    @Expose
    var createdAt: String? = null

    @SerializedName("read_only_participants")
    @Expose
    var read_only_participants: Boolean? = null

    @SerializedName("notification")
    @Expose
    var notification: Boolean = false

    @SerializedName("message")
    @Expose
    @Ignore
    var message: Message? = null


}

