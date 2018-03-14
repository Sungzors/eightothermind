package com.phdlabs.sungwon.a8chat_android.model.files

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.RealmClass

/**
 * Created by JPAM on 1/31/18.
 * @RealmClass for [File]
 *
 * @Warning when creating local files
 * managed by the Android.os do not
 * use this class. This [File] class
 * should only be used within the 8 API.
 */

@RealmClass
open class File: RealmObject() {

    @SerializedName("s3_url")
    @Expose
    var s3_url: String? = null

    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("file_string")
    @Expose
    var file_string: String? = null

    @SerializedName("createdAt")
    @Expose
    var createdAt: String? = null

    @SerializedName("updatedAt")
    @Expose
    var updatedAt: String? = null

    @SerializedName("originalMessageId")
    @Expose
    var originalMessageId: Int? = null

}