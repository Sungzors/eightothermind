package com.phdlabs.sungwon.a8chat_android.model.realmNative

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.RealmClass
import io.realm.annotations.Required

/**
 * Created by paix on 1/27/18.
 * @RealmObject for RealmList used in [Room] @RealmObject
 */

@RealmClass
open class RealmInt(var intValue: Int? = 0) : RealmObject()