package com.phdlabs.sungwon.a8chat_android.model.user

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.phdlabs.sungwon.a8chat_android.model.user.languages.LanguageSpoken
import com.phdlabs.sungwon.a8chat_android.model.user.location.Position
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import java.util.*

/**
 * Created by JPAM on 12/18/2017
 *
 * Model extends RealmObject & is annotated with @open class (Kotlin class is final by default)
 * @RealmClass inheriting RealModel, using serialized naming convention with API & Exposed names
 * for automatic mapping with Gson, Rx & Retrofit2
 */

@RealmClass
open class User() : RealmObject() {

    @PrimaryKey
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("mediaId")
    @Expose
    var mediaId: String? = null

    @SerializedName("first_name")
    @Expose
    var first_name: String? = null

    @SerializedName("last_name")
    @Expose
    var last_name: String? = null

    @SerializedName("phone")
    @Expose
    var phone: String? = null

    @SerializedName("country_code")
    @Expose
    var country_code: String? = null

    @SerializedName("email")
    @Expose
    var email: String? = null

    /**[LanguageSpoken] @RealmObject class*/
    @SerializedName("language_spoken")
    @Expose
    var language_spoken: RealmList<LanguageSpoken>? = null

    @SerializedName("country")
    @Expose
    var country: String? = null

    @SerializedName("profile_picture_string")
    @Expose
    var profile_picture_string: String? = null

    @SerializedName("avatar")
    @Expose
    var avatar: String? = null

    @SerializedName("position")
    @Expose
    var position: RealmList<Position>? = null //TODO: subject to change to drawer_location

    @SerializedName("verified")
    @Expose
    var verified: Boolean? = null

    @SerializedName("passcode")
    @Expose
    var passcode: String? = null

    @SerializedName("passcode_expiration")
    @Expose
    var passcode_expiration: Date? = null

    @SerializedName("socket_id")
    @Expose
    var socket_id: String? = null

    @SerializedName("stripe_connect_account_id")
    @Expose
    var stripe_connect_account_id: String? = null

    @SerializedName("stripe_customer")
    @Expose
    var stripe_customer: String? = null

    @SerializedName("stripe_customer_id")
    @Expose
    var stripe_customer_id: String? = null

    @SerializedName("ephemeral_key")
    @Expose
    var ephemeral_key: String? = null

    @SerializedName("read_receipt")
    @Expose
    var read_receipt: Boolean? = null

    @SerializedName("translation_services")
    @Expose
    var translation_services: Boolean? = null


}