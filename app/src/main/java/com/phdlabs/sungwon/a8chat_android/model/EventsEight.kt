package com.phdlabs.sungwon.a8chat_android.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

/**
 * Created by SungWon on 11/21/2017.
 */

@RealmClass
open class EventsEight(): RealmObject(){

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

    @SerializedName("location")
    @Expose
    var location: RealmList<String>? = null

    @SerializedName("room_id")
    @Expose
    var room_id: String? = null

    @SerializedName("isRead")
    @Expose
    var isRead: Boolean = true

    @SerializedName("avatar")
    @Expose
    var avatar: String? = null
}

