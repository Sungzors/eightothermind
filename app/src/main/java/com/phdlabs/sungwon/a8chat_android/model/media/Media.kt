package com.phdlabs.sungwon.a8chat_android.model.media

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.Index
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

/**
 * Created by JPAM on 12/21/17.
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

    @SerializedName("updatedAt")
    @Expose
    var updatedAt: String? = null

    @SerializedName("createdAt")
    @Expose
    var createdAt: String? = null

    /* Shared between two users in Private chat
    * @isPrivate && -> sharedWithUserId
    * */
    @Index
    var sharedWithUserId: Int? = null

    class Builder(private val media_file: String) {
        fun build(): Media {
            val media = Media()
            media.media_file = media_file
            return media
        }
    }

}