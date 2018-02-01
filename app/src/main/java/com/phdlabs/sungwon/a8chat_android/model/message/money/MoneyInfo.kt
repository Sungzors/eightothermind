package com.phdlabs.sungwon.a8chat_android.model.message.money

import io.realm.RealmObject
import io.realm.annotations.RealmClass

/**
 * Created by paix on 1/31/18.
 * [MoneyInfo] provides specific money transactions information
 * only for the [Message] @Realm model
 * @Deprecated
 */
@RealmClass
open class MoneyInfo: RealmObject() {

    var id: Int? = null

    //TODO: Setup Money Information for future transactions. Stripe is currently Deprecated for this project

}