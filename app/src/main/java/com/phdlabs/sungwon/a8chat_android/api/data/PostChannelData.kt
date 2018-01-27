package com.phdlabs.sungwon.a8chat_android.api.data

/**
 * Created by SungWon on 11/30/2017.
 * Updated by JPAM on 01/26/2018
 */
data class PostChannelData(
        /*Constructor*/
        val mediaId: String?,
        val name: String?,
        val unique_id: String?,
        val description: String?,
        val add_to_profile: Boolean?,
        var user_creator_id: Int?
)