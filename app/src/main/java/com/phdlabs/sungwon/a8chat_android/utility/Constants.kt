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
        const val FROM_CONTACTS = "from_contacts"
        const val MEDIA_POST = "media_post"
        const val MEDIA_POST_MESSAGE = "media_post_message"
        const val INCLUDES_MEDIA = "includes_media"
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
        const val UPDATE_CHAT_FILE = "update-chat-file"
        const val USER_ENTERED_8 = "user-entered-8"
        const val ON_ERROR = "on-error"
        const val COMMENT = "comment"
        const val EDIT_COMMENT = "edit-comment"
    }

    /**
     * [SocketTypes]
     * */
    object SocketTypes {
        const val PRIVATE_CHAT = "privateChat"
        const val GROUP_CHAT = "groupChat"
        const val CHANNEL = "channel"
        const val EVENT = "event"
    }

    /**
     * [RequestCodes]
     * */
    object RequestCodes {
        const val MY_CHANNELS_LIST = 1
        const val INVITE_GROUP = 2

        /**
         * Used when creating a new Channel, Event or GroupChat
         * Starting Navigation through Main Activity (Lobby Frag)
         * */
        const val CREATE_NEW_BACK_REQ_CODE = 13

        /**
         * Used when creating a new Post inside a channel
         * */
        const val CREATE_NEW_POST_REQ_CODE = 14

        /**
         * Used when opening a current post
         * - Possible Post commenting & Liking
         * */
        const val VIEW_POST_REQ_CODE = 17

        /**
         * Used when opening Channel Settings
         * */
        const val CHANNEL_SETTINGS = 18

        /**
         * Opened new channel after search
         * */
        const val OPEN_CHANNEL = 19
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
        val READ_EXTERNAL = Manifest.permission.READ_EXTERNAL_STORAGE
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
        val READ_EXTERNAL_STORAGE = 16
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
        val OPEN_MEDIA_PICKER = 15
    }

    /**
     * [ContactIntents]
     * */
    object ContactItems {
        val CONTACTS_REQ_CODE = 9
        val INVITE_CONTACTS_REQ_CODE = 11
        var TO_CHAT_FROM_CONTACT_REQ_CODE = 12
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
     * [EventAssociation]
     * Used to determine the current user relationship to the event
     * */
    object EventAssociation {
        var EVENT_CREATED = "created"
        var EVENT_FULL_PARTICIPANT = "fullParticipant"
        var EVENT_READ_ONLY = "readOnly"
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

    /**
     * [ChatTypes]
     * represents the type of room
     * - Chat with single or multiple users
     * */
    object ChatTypes {
        val PRIVATE = "private"
        val GROUP = "group"
    }

    /**
     * [RoomState]
     * represents the state of the current room
     * Chat state with specific user
     * Used to display notifications & favorited rooms
     * These properties can apply to both [ChatTypes]
     * */
    object RoomState {
        val TYPE_UNREAD_FAVORITE = "unreadAndFavorite"
        val TYPE_UNREAD = "unread"
        val TYPE_FAVORITE = "favorite"
        val TYPE_READ_NO_FAVORITE = "readandNonfavorite"
    }

}