package com.phdlabs.sungwon.a8chat_android.model.user.registration

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

/**
 * Created by paix on 12/21/17.
 * Cached Token
 */
@RealmClass
open class Token : RealmObject() {

    /*A single token is allowed during the App lifecycle
    * id=0 will represent the only available token in the Realm*/
    @PrimaryKey
    @SerializedName("id")
    @Expose
    var id: Int = 0

    @SerializedName("token")
    @Expose
    var token: String? = null

}