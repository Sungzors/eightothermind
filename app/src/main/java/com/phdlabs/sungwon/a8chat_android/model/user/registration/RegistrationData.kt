package com.phdlabs.sungwon.a8chat_android.model.user.registration

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.RealmClass

/**
 * Created by JPAM on 12/21/17.
 * Registration data for new users
 *
 * Model extends RealmObject & is annotated with @open class (Kotlin class is final by default)
 * @RealmClass inheriting RealModel, using serialized naming convention with API & Exposed names
 * for automatic mapping with Gson, Rx & Retrofit2
 *
 */
@RealmClass
open class RegistrationData: RealmObject() {

    @SerializedName("phone")
    @Expose
    var phone:String? = null

    @SerializedName("country_code")
    @Expose
    var country_code:String? = null

}