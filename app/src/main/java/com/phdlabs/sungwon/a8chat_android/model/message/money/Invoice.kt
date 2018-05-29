package com.phdlabs.sungwon.a8chat_android.model.message.money

import io.realm.RealmObject
import io.realm.annotations.RealmClass

/**
 * Created by JPAM on 1/31/18.
 * [Invoice] provides specific money transactions information
 * only for the [Message] @Realm model
 * @Deprecated
 */
@RealmClass
open class Invoice: RealmObject() {

    var id: Int? = null

    //TODO: Setup Money Information for future transactions. Stripe is currently Deprecated for this project

}