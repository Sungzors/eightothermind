package com.phdlabs.sungwon.a8chat_android.api.utility.deserializers

import com.google.gson.JsonArray
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.phdlabs.sungwon.a8chat_android.model.realmNative.RealmInt
import io.realm.RealmList
import java.lang.reflect.Type

/**
 * Created by JPAM on 1/29/18.
 * Helper class for [Gson] so we can deserialize [RealmList] of [RealmInt] &
 * save directly to Realm when using Kotlin RX callbacks
 */
class RealmIntDeserializer : JsonDeserializer<RealmList<RealmInt>> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): RealmList<RealmInt> {
        val realmList: RealmList<RealmInt> = RealmList()
        val intList: JsonArray? = json?.asJsonArray
        intList?.let {
            it.mapTo(realmList) { RealmInt(it.asInt) }
        }
        return realmList
    }
}