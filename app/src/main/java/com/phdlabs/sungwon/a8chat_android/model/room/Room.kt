package com.phdlabs.sungwon.a8chat_android.model.room

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.phdlabs.sungwon.a8chat_android.model.Message
import com.phdlabs.sungwon.a8chat_android.model.channel.RealmInt
import com.phdlabs.sungwon.a8chat_android.model.user.User
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.Ignore
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

/**
 * Created by SungWon on 10/30/2017.
 * Room describes a chat room.
 *
 * Model extends RealmObject & is annotated with @open class (Kotlin class is final by default)
 * @RealmClass inheriting RealModel, using serialized naming convention with API & Exposed names
 * for automatic mapping with Gson, Rx & Retrofit2
 *
 */
@RealmClass

open class Room() : RealmObject() {

    /*Remote properties*/
    @PrimaryKey
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("channel")
    @Expose
    var channel: Boolean? = null

    @SerializedName("event")
    @Expose
    var event: Boolean? = null

    @SerializedName("groupChat")
    @Expose
    var groupChat: Boolean? = null

    @SerializedName("privateChat")
    @Expose
    var privateChat: Boolean? = null

    /**[ParticipantsId] @RealmObject class*/
    @SerializedName("participantsId")
    @Expose
    var participantsId: RealmList<RealmInt>? = null

    @SerializedName("locked")
    @Expose
    var locked: Boolean? = null

    @SerializedName("last_activity")
    @Expose
    var last_activity: String? = null

    /*Local Properties*/
    var isRead: Boolean? = true

    var isFavorite: Boolean? = false

    var chatType: String? = null

    @Ignore
    var message: Message? = null //Todo: Message in Realm

    var user: User? = null

}
