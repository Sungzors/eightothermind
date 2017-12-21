package com.phdlabs.sungwon.a8chat_android.model.user.location

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.RealmClass

/**
 * Created by paix on 12/20/17.
 * Represents current user location
 * @RealmClass inheriting RealModel, using serialized naming convention with API & Exposed names
 * for automatic mapping with Gson, Rx & Retrofit2
 *
 * TODO: adapt to GEOMETRY data type with GeoJSON
 * http://docs.sequelizejs.com/variable/index.html#static-variable-DataTypes
 *
 */

@RealmClass
open class Position : RealmObject() {

    /*Latitude*/
    @SerializedName("lat")
    @Expose
    var lat: Long? = null

    /*Longitude*/
    @SerializedName("lon")
    @Expose
    var lon: Long? = null

}