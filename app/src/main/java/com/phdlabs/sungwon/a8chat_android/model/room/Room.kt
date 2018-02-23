package com.phdlabs.sungwon.a8chat_android.model.room

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.phdlabs.sungwon.a8chat_android.model.message.Message
import com.phdlabs.sungwon.a8chat_android.model.realmNative.RealmInt
import com.phdlabs.sungwon.a8chat_android.model.user.User
import com.phdlabs.sungwon.a8chat_android.model.user.UserRooms
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.Ignore
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import java.util.*

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
    var last_activity: Date? = null

    @SerializedName("last_activity_in_associated_channel_event_or_groupChat")
    @Expose
    var last_activity_in_associated_channel_event_or_groupChat: String? = null

    @SerializedName("createdAt")
    @Expose
    var createdAt: String? = null

    @SerializedName("updatedAt")
    @Expose
    var updatedAt: String? = null

    @SerializedName("groupChatInfo")
    @Expose
    var groupChatInfo: GroupChatInfo? = null

    @SerializedName("chatType")
    @Expose
    var chatType: String? = null

    @SerializedName("type")
    @Expose
    var type: String?= null

    /*Local Properties*/
    var isRead: Boolean = true

    @Ignore
    var message: Message? = null //Todo: Message in Realm

    /**
     * [User] assigned property when creating a Room
     * */
    var user: User? = null

    /**
     *  [userRoom]
     *  This variable only exists when a room has been favorite'd or we
     *  query a specific room on an API.
     *  WARNING: This is not the same as [User] [UserRooms] even though we are using the same model
     *  @see Realm [UserRooms]
     * */
    @SerializedName("userRoom")
    @Expose
    var userRoom: UserRooms? = null

}
