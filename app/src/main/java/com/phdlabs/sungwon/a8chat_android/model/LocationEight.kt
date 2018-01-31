package com.phdlabs.sungwon.a8chat_android.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.phdlabs.sungwon.a8chat_android.model.realmNative.RealmDouble
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.RealmClass

/**
 * Created by paix on 1/30/18.
 * [LocationEight]
 * @see RealmObject
 * used to define user location & match
 * Geometry backend type using Point
 */
@RealmClass
open class LocationEight : RealmObject() {

    @SerializedName("type")
    @Expose
    var type: String? = null

    @SerializedName("coordinates")
    @Expose
    var coordinates: RealmList<RealmDouble>? = null

}