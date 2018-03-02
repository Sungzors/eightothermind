package com.phdlabs.sungwon.a8chat_android.model.channel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.phdlabs.sungwon.a8chat_android.model.message.Message
import com.phdlabs.sungwon.a8chat_android.model.realmNative.RealmInt
import io.realm.RealmList
import java.util.*

/**
 * Created by SungWon on 12/21/2017.
 * Updated by JPAM on 02/26/2018
 * Initializing variable is the array of [Channel]s
 * Properties make reference to the current [Channel]'s [Room]
 * @see Realm
 */
data class ChannelShowNest(val channels: Array<Channel?>) {

    //TODO: Could also get subrooms from this call to get the latest message

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

    //Received as array, but it only contains the last message index [0]
    @SerializedName("messages")
    @Expose
    var messages: Array<Message?>? = null

    //Query hash maps
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ChannelShowNest

        if (!Arrays.equals(channels, other.channels)) return false

        return true
    }

    override fun hashCode(): Int = Arrays.hashCode(channels)
}