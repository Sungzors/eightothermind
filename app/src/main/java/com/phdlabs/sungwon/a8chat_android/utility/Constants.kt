package com.phdlabs.sungwon.a8chat_android.utility

import android.Manifest

/**
 * Created by SungWon on 9/24/2017.
 */
object Constants {

    /**
     * [IntentKeys]
     * */
    object IntentKeys {
        const val LOGIN_KEY = "login_key"
        const val LOGIN_CC = "login_cc"
        const val LOGIN_PHONE = "login_phone"
        const val CHANNEL_ID = "channel_id"
        const val CHANNEL_NAME = "channel_name"
        const val EVENT_ID = "event_id"
        const val EVENT_NAME = "event_name"
        const val EVENT_LOCATION = "event_location"
        const val CHAT_NAME = "chat_name"
        const val PARTICIPANT_ID = "dunga" //todo: what is this?
        const val CHAT_PIC = "chat_pic"
        const val ROOM_ID = "room_id"
        const val MESSAGE_ID = "message_id"
        const val OWNER_ID = "owner_id"
    }

    /**
     * [SocketKeys]
     * */
    object SocketKeys {
        const val CONNECT = "connect"
        const val UPDATE_ROOM = "update-room"
        const val UPDATE_CHAT_STRING = "update-chat-string"
        const val UPDATE_CHAT_MEDIA = "update-chat-media"
        const val UPDATE_CHAT_LOCATION = "update-chat-location"
        const val UPDATE_CHAT_CHANNEL = "update-chat-channel"
        const val UPDATE_CHAT_CONTACT = "update-chat-contact"
        const val UPDATE_CHAT_POST = "update-chat-post"
        const val ON_ERROR = "on-error"
    }

    /**
     * [ChannelRequestCodes]
     * */
    object ChannelRequestCodes {
        const val MY_CHANNELS_LIST = 1
    }

    /**
     * [ResultCode]
     * */
    object ResultCode {
        const val SUCCESS = 0
        const val FAILURE = 1
    }

    /**
     * [CameraPager] constants
     * to control the camera state
     * */
    object CameraPager {
        const val CAMERA_ROLL = 0
        const val NORMAL = 1
        const val HANDS_FREE = 2
    }

    /**
     * [AppPermissions] used for permission check management
     * */
    object AppPermissions {
        val CAMERA = Manifest.permission.CAMERA
        val RECORD_AUDIO = Manifest.permission.RECORD_AUDIO
        val WRITE_EXTERNAL = Manifest.permission.WRITE_EXTERNAL_STORAGE
        val FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
        val COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION
        val CONTACTS = Manifest.permission.READ_CONTACTS
    }

    /**
     * [PermissionsReqCode] used for permission request & result
     * */
    object PermissionsReqCode {
        val CAMERA_REQ_CODE = 0
        val RECORD_AUDIO_REQ_CODE = 1
        val WRITE_EXTERNAL_REQ_CODE = 2
        val LOCATION_REQ_CODE = 3
        val CONTACTS_REQ_CODE = 4
    }

    /**
     * [CameraIntents] used to control camera hardware & file management
     * */
    object CameraIntents {
        val IMAGE_FILE_PATH = "img_file_path"
        val LENS_FACING = "lens_facing"
        var IS_FROM_CAMERA_ROLL = "is_from_camera_roll"
        val EDITING_REQUEST_CODE = 7
        val CAMERA_REQUEST_CODE = 8
    }

    /**
     * [ContactIntents]
     * */
    object ContactIntens {
        val CONTACTS_REQ_CODE = 9
        val INVITE_CONTACTS_REQ_CODE = 11
    }

    /**
     * [ProfileIntents]
     * */
    object ProfileIntents {
        val WILL_EDIT_PROFILE = "edit_profile"
        val EDIT_MY_PROFIILE = 10
    }

    /**
     * [EventPrivacy] used to control event privacy at [EventsEight] creation
     * */
    object EventPrivacy {
        val ONLY_FRIENDS = "only my friends"
        var FRIENDS_OF_FRIENDS = "friends of friends"
    }

    /**
     * [MessageTypes] used to select single message type on [Message]
     * */
    object MessageTypes {
        val TYPE_STRING = "string"
        val TYPE_MEDIA = "media"
        val TYPE_CONTACT = "contact"
        val TYPE_LOCATION = "location"
        val TYPE_FILE = "file"
        val TYPE_CHANNEL = "channel"
        val TYPE_MONEY = "money"
        val TYPE_POST = "post"
    }


}