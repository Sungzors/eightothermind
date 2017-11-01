package com.phdlabs.sungwon.a8chat_android.utility

/**
 * Created by SungWon on 9/24/2017.
 */
object Constants{

    object IntentKeys{
        const val LOGIN_KEY = "login_key"
        const val LOGIN_CC = "login_cc"
        const val LOGIN_PHONE = "login_phone"
    }

    object PrefKeys{
        const val USER_ID = "user_id"
        const val TOKEN_KEY = "token"
    }

    object SocketKeys{
        const val CONNECT = "connect"
        const val UPDATE_ROOM = "update-room"
        const val UPDATE_CHAT_STRING = "update-chat-string"
        const val UPDATE_CHAT_MEDIA = "update-chat-media"
        const val UPDATE_CHAT_LOCATION = "update-chat-location"
        const val UPDATE_CHAT_CHANNEL = "update-chat-channel"
        const val UPDATE_CHAT_CONTACT = "update-chat-contact"
        const val ON_ERROR = "on-error"
    }
}