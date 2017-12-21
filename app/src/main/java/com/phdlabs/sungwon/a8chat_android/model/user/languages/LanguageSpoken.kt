package com.phdlabs.sungwon.a8chat_android.model.user.languages

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.RealmClass
import io.realm.annotations.Required

/**
 * Created by paix on 12/18/17.
 * @RealmObject for RealmList used in User @RealmObject
 */

@RealmClass
open class LanguageSpoken : RealmObject() {

    @Required
    @SerializedName("language")
    @Expose
    var language:String? = null

}