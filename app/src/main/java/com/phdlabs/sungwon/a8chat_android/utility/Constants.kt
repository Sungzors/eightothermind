package com.phdlabs.sungwon.a8chat_android.utility

import android.Manifest
import io.agora.rtc.Constants

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
        const val PARTICIPANT_ID = "dunga"
        const val CHAT_PIC = "chat_pic"
        const val ROOM_ID = "room_id"
        const val MESSAGE_ID = "message_id"
        const val OWNER_ID = "owner_id"
        const val USER_ID = "userId"
        const val FROM_CONTACTS = "from_contacts"
        const val MEDIA_POST = "media_post"
        const val MEDIA_POST_MESSAGE = "media_post_message"
        const val INCLUDES_MEDIA = "includes_media"
        const val CHANNEL_DELETED = "channel_deleted"
        const val FAVE_TYPE = "fave_type"
        const val BROADCAST_MESSAGE_ID = "messageId"
    }

    /**
     * [SocketKeys]
     * */
    object SocketKeys {
        //Entering 8
        const val USER_ENTERED_8 = "user-entered-8" //FIXME not used for now until further notice
        //Connect
        const val CONNECT_ROOMS = "connect-rooms"
        //Update
        const val UPDATE_ROOM = "update-room"
        //Update Types
        const val UPDATE_CHAT_STRING = "update-chat-string"
        const val UPDATE_CHAT_MEDIA = "update-chat-media"
        const val UPDATE_CHAT_LOCATION = "update-chat-location"
        const val UPDATE_CHAT_CHANNEL = "update-chat-channel"
        const val UPDATE_CHAT_CONTACT = "update-chat-contact"
        const val UPDATE_CHAT_MONEY = "update-chat-money"
        const val UPDATE_CHAT_FILE = "update-chat-file"
        const val UPDATE_CHAT_BROADCAST = "update-chat-broadcast"
        //Liking a Channel Post
        const val LIKE = "like"
        //Sharing Channel inside conversation
        const val SHARE_CHANNEL = "update-chat-channel-message"
        //Comment & Editing Comments -> Channel Post & Broadcast
        const val COMMENT = "comment"
        const val EDIT_COMMENT = "edit-comment"
        //Editing Message
        const val EDIT_MESSAGE = "edit-message"
        //Delete
        const val DELETE_MESSAGE = "delete"
        //Error
        const val ON_ERROR = "on-error"
        //TODO: Read Receipts
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
     * Google Play Services
     * */
    object GPServices {
        const val GPS_DOWNLOAD = "play.google.com/store/apps/details?id=com.google.android.gms&hl=en"
    }

    /**
     * Notifications
     * */
    object Notifications {
        //Notification Types
        var USER_ADDED = "user_added"
        var PRIVATE_CHAT = "private_chat"
        var COMMENT = "comment"
        var LIKE = "like"
        var POST = "post"
        var VIDEO_CALL = "incoming_video_call"
        var VOICE_CALL = "incoming_voice_call"
        var MISSED_VIDEO_CALL = "missed_video_call"
        var MISSED_VOICE_CALL = "missed_video_call"
        var MESSAGE = "message"
        //Notification Classifiers
        var TYPE = "notification_type"
        var BADGES = "badges"
        //Notification Channels
        var GLOBAL_CHANNEL = "globalChannel"
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

        /**
         * Used for LogIn & SignUp
         * */
        const val LOGIN_SIGNUP = 20

        /**
         * Used for LogIn & Signup Confirmation
         * */
        const val SIGNUP_CONFIRMATION = 21

        /**
         * Used for LogIn & SignUp Transition to Update Profile
         * */
        const val MY_PROFILE_UPDATE_REGISTER = 22

        /**
         * Edit Channel
         * */
        const val EDIT_CHANNEL = 23

        const val OPEN_FAVE = 24

        /**
         * Google Play Services
         * Used for error handling
         * [onActivityResult] [MainActivity]
         * */
        const val VALIDATE_GOOGLE_PLAY_SERVICES = 25

        /**
         * Notification Request Code
         * */
        const val NOTIF_REQUEST_CODE = 26

        /**
         * Share media from Camera App
         * */
        const val SHARE_MEDIA = 27

        /**
         * Start & Finish Broadcast inside channel
         * */
        const val BROADCAST_REQ_CODE = 29

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
        val BROADCAST = "broadcast"
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

    /**
     * Agora.io Live Video Broadcasting through Eight Channels
     * Resolution and Peer management constants
     * */
    object Broadcast {

        const val MAX_PEER_COUNT = 3 // TODO: Allow maximum peer count after testing

        var VIDEO_PROFILES = intArrayOf(
                Constants.VIDEO_PROFILE_120P,
                Constants.VIDEO_PROFILE_180P,
                Constants.VIDEO_PROFILE_240P,
                Constants.VIDEO_PROFILE_360P,
                Constants.VIDEO_PROFILE_480P,
                Constants.VIDEO_PROFILE_720P,
                Constants.VIDEO_PROFILE_1080P_3
        )

        val DEFAULT_PROFILE_IDX = 2 // default use 240P //TODO: Test with different resolutions

        object PrefManager {
            const val PREF_PROPERTY_PROFILE_IDX = "pref_profile_index"
            const val PREF_PROPERTY_UID = "pOCXx_uid"
        }

        val ACTION_KEY_CROLE = "C_Role"
    }

}