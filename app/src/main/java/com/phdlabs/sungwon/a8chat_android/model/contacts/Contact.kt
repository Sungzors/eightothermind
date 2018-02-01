package com.phdlabs.sungwon.a8chat_android.model.contacts

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.phdlabs.sungwon.a8chat_android.model.event.location.EventLocation
import com.phdlabs.sungwon.a8chat_android.model.realmNative.RealmString
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.Ignore
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

/**
 * Created by paix on 1/31/18.
 * Similar model as the User model
 * used on [Message] & Contact management
 * @Warning many properties will probably be null when received
 */
@RealmClass
open class Contact : RealmObject() {

    @PrimaryKey
    @SerializedName("id")
    @Expose
    var id: Int? = null

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
    @SerializedName("languages_spoken")
    @Expose
    var languages_spoken: RealmList<RealmString>? = null

    @SerializedName("country")
    @Expose
    var country: String? = null

    @SerializedName("profile_picture_string")
    @Expose
    var profile_picture_string: String? = null

    @SerializedName("avatar")
    @Expose
    var avatar: String? = null

    /**
     * [position] legacy database
     * Not used in current model
     * @DEPRECATED
     * */
    @SerializedName("position")
    @Expose
    @Ignore
    var position: EventLocation? = null

    @SerializedName("facebook_token")
    @Expose
    var facebook_token: String? = null

    @SerializedName("verified")
    @Expose
    var verified: Boolean? = null

    @SerializedName("passcode")
    @Expose
    var passcode: String? = null

    @SerializedName("passcode_expiration")
    @Expose
    var passcode_expiration: String? = null

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

    @SerializedName("createdAt")
    @Expose
    var createdAt: String? = null

    @SerializedName("updatedAt")
    @Expose
    var updatedAt: String? = null

    @SerializedName("fcm_token")
    @Expose
    var fcm_token: String? = null
}