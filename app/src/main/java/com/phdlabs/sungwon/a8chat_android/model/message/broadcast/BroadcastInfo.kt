package com.phdlabs.sungwon.a8chat_android.model.message.broadcast

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.RealmClass

/**
 * Created by JPAM on 4/10/18.
 * [BroadcastInfo] provides specific information
 * for a Broadcast Type [Message]
 */
@RealmClass
open class BroadcastInfo: RealmObject() {

    @SerializedName("start_time")
    @Expose
    var start_time: String? = null

    @SerializedName("end_time")
    @Expose
    var end_time: String? = null
}