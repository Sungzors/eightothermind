package com.phdlabs.sungwon.a8chat_android.model.message.location

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.RealmClass

/**
 * Created by paix on 1/31/18.
 * [LocationInfo] provides specific location information
 * only for the [Message] @Realm model
 * @Warning this class is different from the [EventLocation]
 */
@RealmClass
open class LocationInfo: RealmObject() {

    @SerializedName("lat")
    @Expose
    var lat: String? = null

    @SerializedName("lng")
    @Expose
    var lng: String? = null

    @SerializedName("streetAddress")
    @Expose
    var streetAddress: String? = null

}