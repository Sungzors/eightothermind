package com.phdlabs.sungwon.a8chat_android.model.media

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.Index
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

/**
 * Created by paix on 12/21/17.
 * Model extends RealmObject & is annotated with @open class (Kotlin class is final by default)
 * @RealmClass inheriting RealModel, using serialized naming convention with API & Exposed names
 * for automatic mapping with Gson, Rx & Retrofit2
 * @Index variables improve query speed
 */
@RealmClass
open class Media : RealmObject() {

    @PrimaryKey
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("media_file_string")
    @Expose
    var media_file_string: String? = null

    @SerializedName("media_file")
    @Expose
    var media_file: String? = null

    @SerializedName("user_id")
    @Expose
    @Index
    var user_id: Int? = null

    @SerializedName("channelId")
    @Expose
    @Index
    var channelId: Int? = null

    @SerializedName("groupChatId")
    @Expose
    @Index
    var groupChatId: Int? = null

    @SerializedName("eventId")
    @Expose
    @Index
    var eventId: Int? = null

    @SerializedName("originalMessageId")
    @Expose
    var originalMessageId: Int? = null

}