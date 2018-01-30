package com.phdlabs.sungwon.a8chat_android.api.utility.deserializers

import com.google.gson.JsonArray
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.phdlabs.sungwon.a8chat_android.model.realmNative.RealmString
import io.realm.RealmList
import java.lang.reflect.Type

/**
 * Created by paix on 1/29/18.
 * Helper class for [Gson] so we can deserialize [RealmList] of [RealmString] &
 * save directly to Realm when using Kotlin RX callbacks
 */
class RealmStringDeserializer : JsonDeserializer<RealmList<RealmString>> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): RealmList<RealmString> {
        val realmList: RealmList<RealmString> = RealmList()
        val stringList: JsonArray? = json?.asJsonArray
        stringList?.let {
            it.mapTo(realmList) { RealmString(it.asString) }
        }
        return realmList
    }
}