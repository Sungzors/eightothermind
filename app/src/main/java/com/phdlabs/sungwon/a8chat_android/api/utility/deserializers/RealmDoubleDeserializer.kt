package com.phdlabs.sungwon.a8chat_android.api.utility.deserializers

import com.google.gson.JsonArray
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.phdlabs.sungwon.a8chat_android.model.realmNative.RealmDouble
import io.realm.RealmList
import java.lang.reflect.Type

/**
 * Created by JPAM on 1/30/18.
 * Helper class for [Gson] so we can deserialize [RealmList] of [RealmDouble] &
 * save directly to Realm when using Kotlin RX callbacks
 */
class RealmDoubleDeserializer : JsonDeserializer<RealmList<RealmDouble>> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): RealmList<RealmDouble> {
        val realmList: RealmList<RealmDouble> = RealmList()
        val lngList: JsonArray? = json?.asJsonArray
        lngList?.let {
            it.mapTo(realmList) { RealmDouble(it.asDouble) }
        }
        return realmList
    }
}