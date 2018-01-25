package com.phdlabs.sungwon.a8chat_android.model.user

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

/**
 * Created by SungWon on 1/25/2018.
 */
@RealmClass
open class UserRooms : RealmObject() {


    @PrimaryKey
    @SerializedName("userId")
    @Expose
    var userId: Int? = null

    @SerializedName("roomId")
    @Expose
    var roomId: Int? = null
}