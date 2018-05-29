package com.phdlabs.sungwon.a8chat_android.model.realmNative

import io.realm.RealmObject
import io.realm.annotations.RealmClass

/**
 * Created by JPAM on 1/30/18.
 * @RealmObject for RealmList used in [Event] @RealmObject
 * & other location objects
 */
@RealmClass
open class RealmDouble(internal var doubleValue: Double? = 0.0) : RealmObject()