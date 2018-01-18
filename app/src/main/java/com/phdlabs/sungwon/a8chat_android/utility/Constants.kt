package com.phdlabs.sungwon.a8chat_android.utility

import android.Manifest

/**
 * Created by SungWon on 9/24/2017.
 */
object Constants{

    object IntentKeys{
        const val LOGIN_KEY = "login_key"
        const val LOGIN_CC = "login_cc"
        const val LOGIN_PHONE = "login_phone"
        const val CHANNEL_ID = "channel_id"
        const val MESSAGE_ID = "message_id"
    }

    object PrefKeys{
        const val USER_ID = "user_id"
        const val TOKEN_KEY = "token"
    }

    object SocketKeys{
        const val CONNECT = "connect"
        const val UPDATE_ROOM = "update-room"
        const val UPDATE_CHAT_STRING = "update-chat-string"
        const val UPDATE_CHAT_MEDIA = "update-chat-drawer_media"
        const val UPDATE_CHAT_LOCATION = "update-chat-drawer_location"
        const val UPDATE_CHAT_CHANNEL = "update-chat-channel"
        const val UPDATE_CHAT_CONTACT = "update-chat-drawer_contact"
        const val ON_ERROR = "on-error"
    }

    object RequestCode{
        const val MY_CHANNELS_LIST = 1
    }

    object ResultCode{
        const val SUCCESS = 0
        const val FAILURE = 1
    }

    /*Camera*/
    object CameraPager {
        const val CAMERA_ROLL = 0
        const val NORMAL = 1
        const val HANDS_FREE = 2
    }

    object AppPermissions {
        val CAMERA = Manifest.permission.CAMERA
        val RECORD_AUDIO = Manifest.permission.RECORD_AUDIO
        val WRITE_EXTERNAL = Manifest.permission.WRITE_EXTERNAL_STORAGE
    }

    object PermissionsReqCode {
        val CAMERA_REQ_CODE = 0
        val RECORD_AUDIO_REQ_CODE = 1
        val WRITE_EXTERNAL_REQ_CODE = 2
    }

    object CameraIntents {
        val IMAGE_FILE_PATH = "img_file_path"
        val LENS_FACING = "lens_facing"
    }


}