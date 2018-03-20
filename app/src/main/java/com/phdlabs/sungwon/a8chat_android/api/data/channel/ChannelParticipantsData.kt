package com.phdlabs.sungwon.a8chat_android.api.data.channel

import java.util.*

/**
 * Created by JPAM on 3/16/18.
 */

data class ChannelParticipantsData(var userIds: Array<out Any>) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ChannelParticipantsData

        if (!Arrays.equals(userIds, other.userIds)) return false

        return true
    }

    override fun hashCode(): Int {
        return Arrays.hashCode(userIds)
    }
}