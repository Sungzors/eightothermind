package com.phdlabs.sungwon.a8chat_android.model.realmNative

import io.realm.RealmObject
import io.realm.annotations.RealmClass

/**
 * Created by JPAM on 1/29/18.
 * @RealmObject for RealmList used in [EventsEight] @RealmObject
 */
@RealmClass
open class RealmString(var stringValue: String? = "") : RealmObject()