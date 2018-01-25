package com.phdlabs.sungwon.a8chat_android.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.annotations.PrimaryKey
import java.util.*

/**
 * Created by SungWon on 11/21/2017.
 */

//@RealmClass
open class EventsEight{

    @PrimaryKey
    @SerializedName("id")
    @Expose
    var id : String? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("user_creator_id")
    @Expose
    var user_creator_id: String? = null

    @SerializedName("distribution")
    @Expose
    var distribution: String? = null

    @SerializedName("locks_after_24_hours")
    @Expose
    var locks_after_24_hours: Boolean = false

//    @SerializedName("location")
//    @Expose
//    var location: String? = null

    @SerializedName("location_name")
    @Expose
    var location_name: String? = null

    @SerializedName("room_id")
    @Expose
    var room_id: String? = null

    @SerializedName("isRead")
    @Expose
    var isRead: Boolean = true

    @SerializedName("last_activity")
    @Expose
    var last_activity: Date? = null

    @SerializedName("avatar")
    @Expose
    var avatar: String? = null

    @SerializedName("message")
    @Expose
    var message: Message? = null

    var event_name: String? = null
    var eventId: Int? = null
    var roomId: Int? = null
}

