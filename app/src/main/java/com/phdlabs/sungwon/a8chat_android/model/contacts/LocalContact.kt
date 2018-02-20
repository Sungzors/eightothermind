package com.phdlabs.sungwon.a8chat_android.model.contacts

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject

/**
 * Created by paix on 2/13/18.
 * [LocalContact] represents the local phone contact query used for the [ContactsFileProvider]
 * - Only the phone number is required, but the [ContactsFileProvider] can return number & name
 */
data class LocalContact(val phone: String, val displayName: String?)