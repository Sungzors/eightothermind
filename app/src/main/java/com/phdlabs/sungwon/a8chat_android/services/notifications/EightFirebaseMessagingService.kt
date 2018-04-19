package com.phdlabs.sungwon.a8chat_android.services.notifications


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationCompat.DEFAULT_ALL
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.api.utility.GsonHolder
import com.phdlabs.sungwon.a8chat_android.db.channels.ChannelsManager
import com.phdlabs.sungwon.a8chat_android.db.room.RoomManager
import com.phdlabs.sungwon.a8chat_android.db.user.UserManager
import com.phdlabs.sungwon.a8chat_android.model.notifications.*
import com.phdlabs.sungwon.a8chat_android.structure.channel.broadcast.BroadcastActivity
import com.phdlabs.sungwon.a8chat_android.structure.channel.mychannel.MyChannelActivity
import com.phdlabs.sungwon.a8chat_android.structure.channel.postshow.ChannelPostShowActivity
import com.phdlabs.sungwon.a8chat_android.structure.chat.ChatActivity
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import com.vicpin.krealmextensions.save
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by JPAM on 3/19/18.
 * [EightFirebaseMessagingService] makes use of [FirebaseMessagingService]
 * to provide messaging handling beyond receiving notifications whtn the
 * app is in the background.
 * This also handles notifications in foreground, data payload, and upstream messages.
 */
class EightFirebaseMessagingService : FirebaseMessagingService() {

    /*Properties*/
    private var mUserId: Int? = null

    init {
        UserManager.instance.getCurrentUser { success, user, _ ->
            if (success) {
                user?.id?.let {
                    mUserId = it
                }
            }
        }
    }


    /*LifeCycle*/
    /**
     * - Data Type messages are handled in [onMessageReceived] when the application is
     * in the Foreground & in the Background.
     * - Notification Type messages are handled in [onMessageReceived] only when the app
     * is in the Foreground. When the app is in the background the Notification Type message
     * is handled by the system tray.
     * */
    override fun onMessageReceived(p0: RemoteMessage?) {
        super.onMessageReceived(p0)
        p0?.let {
            //Dev
            println("Remote Message Type: " + it.messageType)//Not set by Backend
            println("Remote Message Sent Time: " + it.sentTime)
            println("Remote Message Collapse Key: " + it.collapseKey)
            println("Remote Message From: " + it.from)
            //Handle Custom Notification
            showNotification(it.notification, it.data)
        }
    }

    override fun onDeletedMessages() {
        super.onDeletedMessages()
    }

    /**
     * [showNotification]
     * Create Notification to show when the app is in the Foreground.
     * When the app is in the Background, Android OS handles the Notification Payload
     * with the System Tray
     * @param messageBody
     * @param -> Probably add the Data payload to be added on a pending intent
     * */
    private fun showNotification(notification: RemoteMessage.Notification?, data: MutableMap<String, String>?) {
        //Default Intent
        var displayIntent: Intent?
        //Notification Manager
        val mBuilder = NotificationCompat.Builder(this, Constants.Notifications.GLOBAL_CHANNEL)
                .setSmallIcon(R.drawable.ic_default_notification)
        mBuilder.setDefaults(DEFAULT_ALL)
        val mManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        //Android Oreo Channel Support
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mManager.createNotificationChannel(
                    NotificationChannel(
                            Constants.Notifications.GLOBAL_CHANNEL,
                            "Global Application Notifications Channel",
                            NotificationManager.IMPORTANCE_HIGH))
        }
        //Default Priority
        mBuilder.priority = NotificationCompat.PRIORITY_HIGH //Lower Android API's
        mBuilder.setAutoCancel(true) //Dismiss after tapping on Notification
        //Check for Notification Payload
        notification?.let {
            mBuilder.setContentTitle(it.title)
            mBuilder.setContentText(it.body)
        }
        //Check for Notification Data Payload
        data?.let {
            if (it.count() > 0) {
                //Build Notification based on Type
                val jsonObject = JSONObject(it)
                when (jsonObject.getString(Constants.Notifications.TYPE)) {

                /*User has been invited to a GroupChat, Event || Channel*/
                    Constants.Notifications.USER_ADDED -> {//GroupChat, Event || Channel
                        //Parse Notification Data Payload
                        val data = GsonHolder.instance.get()?.fromJson(jsonObject.toString(), UserAdedPayload::class.java)
                        //Intent
                        //TODO: Create Intent
                        //Retrieve Room Information
                        data?.roomId?.let {
                            RoomManager.instance.getRoomInfo(it.toInt(), { response ->
                                response?.second?.let {
                                    //Error
                                } ?: run {
                                    response.first?.let {
                                        //Cache Room
                                        it.save()
                                        //Evaluate Room Type:
                                        it.channel?.let {
                                            //TODO: Ask Tomer if this call includes Channels, I think not!!!!
                                            if (it) {
                                                //TODO: Navigate to Channel -> verify channel ID from payload

                                                //TODO: Build Intent to access Channel
                                            }
                                        }
                                        it.event?.let {
                                            if (it) {
                                                //TODO: Navigate to Event -> verify event ID from payload

                                                //TODO: Build intent to access Event
                                            }
                                        }
                                        it.groupChat?.let {
                                            if (it) {
                                                //TODO: Navigate to GroupChat -> verify groupChat ID from payload

                                                //TODO: Build intent to access Group Chat
                                            }
                                        }
                                    }
                                }
                            })
                        }
                    }

                /*User has been invited to a Private Chat*/
                    Constants.Notifications.PRIVATE_CHAT -> {//Private Chat Created -> Room
                        //TODO: Not Yet Tested
                        //Parse Notification Data Payload
                        val data = GsonHolder.instance.get()?.fromJson(jsonObject.toString(), PrivateChatPayload::class.java)
                        //Intent
                        displayIntent = Intent(this, ChatActivity::class.java)
                        displayIntent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        RoomManager.instance.getPrivateAndGroupChats { response ->
                            response.first?.let {
                                it.forEach { room ->
                                    if (room.id.toString() == data?.roomId) {
                                        displayIntent?.putExtra(Constants.IntentKeys.CHAT_NAME, room.user?.first_name + " " + room.user?.last_name)
                                        displayIntent?.putExtra(Constants.IntentKeys.PARTICIPANT_ID, room.user?.userRooms?.userId)
                                        displayIntent?.putExtra(Constants.IntentKeys.ROOM_ID, room.id)
                                        displayIntent?.putExtra(Constants.IntentKeys.CHAT_PIC, room.user?.avatar)
                                        displayNotification(mBuilder, mManager, displayIntent!!)
                                        return@getPrivateAndGroupChats
                                    }
                                }
                            }
                        }
                    }

                /*Someone commented on a User's Post*/
                    Constants.Notifications.COMMENT -> {//Channel
                        //Parse Notification Data Payload
                        val data = GsonHolder.instance.get()?.fromJson(jsonObject.toString(), CommentPayLoad::class.java)
                        //Verify if the user is in the broadcast to avoid notifications
                        BroadcastActivity.isInBroadcast?.let {
                            if (it) {
                                return
                            }
                        }
                        //Verify if I'm the author of the Comment
                        mUserId?.let {
                            if (data?.user_commenting_id == it.toString()) {
                                return
                            }
                        }
                        //Intent
                        displayIntent = Intent(this, ChannelPostShowActivity::class.java)
                        //Refresh Message History on My Channels
                        UserManager.instance.getCurrentUser { success, user, token ->
                            if (success) {
                                user?.id?.let {
                                    if (data?.channel_creator_id == it.toString()) {
                                        ChannelsManager.instance.getUserChannels(it, true, {
                                            it.first?.let {
                                                it.forEach { channel ->
                                                    channel.room_id?.let {
                                                        ChannelsManager.instance.getChannelPosts(true, it, null, {
                                                            //Check for commented post
                                                            it.first?.let {
                                                                if (it.count() > 0) {
                                                                    it.forEach {
                                                                        if (it.id.toString() == data.post_commented_on_id) {
                                                                            //This is the post & belongs to that channel, create intent
                                                                            displayIntent?.putExtra(Constants.IntentKeys.MESSAGE_ID, it.id)
                                                                            displayIntent?.putExtra(Constants.IntentKeys.CHANNEL_NAME, channel.name)
                                                                            it.mediaArray?.let {
                                                                                if (it.count() > 0) {
                                                                                    displayIntent?.putExtra(Constants.IntentKeys.INCLUDES_MEDIA, true)
                                                                                }
                                                                            }
                                                                            displayIntent?.putExtra(Constants.IntentKeys.INCLUDES_MEDIA, false)
                                                                            displayNotification(mBuilder, mManager, displayIntent!!)
                                                                            return@getChannelPosts
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        })
                                                    }
                                                }
                                            }
                                        })
                                    }
                                }
                            }
                        }
                    }

                /*Someone liked a User's Post*/
                    Constants.Notifications.LIKE -> {//Channel
                        //Parse Notification Data Payload
                        val data = GsonHolder.instance.get()?.fromJson(jsonObject.toString(), LikePayload::class.java)
                        //Verify if the user is in the broadcast to avoid notifications
                        BroadcastActivity.isInBroadcast?.let {
                            if (it) {
                                return
                            }
                        }
                        //Verify if I'm the author of the like
                        mUserId?.let {
                            if (data?.user_liking_id == it.toString()) {
                                return
                            }
                        }
                        //Verify if the user is in the broadcast to avoid notifications
                        //TODO
                        //Intent
                        displayIntent = Intent(this, ChannelPostShowActivity::class.java)
                        //Refresh Message History on My Channels
                        UserManager.instance.getCurrentUser { success, user, token ->
                            if (success) {
                                user?.id?.let {
                                    if (data?.channel_creator_id == it.toString()) {
                                        ChannelsManager.instance.getUserChannels(it, true, {
                                            it.first?.let {
                                                it.forEach { channel ->
                                                    channel.room_id?.let {
                                                        ChannelsManager.instance.getChannelPosts(true, it, null, {
                                                            //Check for commented post
                                                            it.first?.let {
                                                                if (it.count() > 0) {
                                                                    it.forEach {
                                                                        if (it.id.toString() == data.messageId) {
                                                                            //This is the post & belongs to that channel, create intent
                                                                            displayIntent?.putExtra(Constants.IntentKeys.MESSAGE_ID, it.id)
                                                                            displayIntent?.putExtra(Constants.IntentKeys.CHANNEL_NAME, channel.name)
                                                                            it.mediaArray?.let {
                                                                                if (it.count() > 0) {
                                                                                    displayIntent?.putExtra(Constants.IntentKeys.INCLUDES_MEDIA, true)
                                                                                }
                                                                            }
                                                                            displayIntent?.putExtra(Constants.IntentKeys.INCLUDES_MEDIA, false)
                                                                            displayNotification(mBuilder, mManager, displayIntent!!)
                                                                            return@getChannelPosts
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        })
                                                    }
                                                }
                                            }
                                        })
                                    }
                                }
                            }
                        }
                    }

                /*New Post on a Followed Channel*/
                    //FIXME: Not hitting POST case
                    Constants.Notifications.POST -> {//Channel
                        //Parse Notification Data Payload
                        val data = GsonHolder.instance.get()?.fromJson(jsonObject.toString(), PostPayload::class.java)
                        //Verify if I'm the author of the post
//                        mUserId?.let {
//                            if (data?.userId == it.toString()) {
//                                return
//                            }
//                        }
                        //Get Channel Info from Room ID
                        data?.roomId?.let { roomId ->
                            val channel = ChannelsManager.instance.querySingleChannelWithRoomId(roomId.toInt())
                            channel?.let {
                                when (data.message_type) {
                                /*Broadcast Post*/
                                    Constants.MessageTypes.BROADCAST -> {
                                        //Build Intent to access channel where the new broadcast is happening
                                        displayIntent = Intent(this, MyChannelActivity::class.java)
                                        displayIntent?.putExtra(Constants.IntentKeys.CHANNEL_ID, channel.id)
                                        displayIntent?.putExtra(Constants.IntentKeys.CHANNEL_NAME, channel.name)
                                        displayIntent?.putExtra(Constants.IntentKeys.ROOM_ID, roomId)
                                        displayIntent?.putExtra(Constants.IntentKeys.OWNER_ID, data.userId)
                                        displayNotification(mBuilder, mManager, displayIntent!!)
                                    }
                                /*Media or Message Post*/
                                    else -> {
                                        //Build Intent to access the new post
                                        displayIntent = Intent(this, ChannelPostShowActivity::class.java)
                                        ChannelsManager.instance.getChannelPosts(true, roomId.toInt(), null, {
                                            //Check for commented post
                                            it.first?.let {
                                                if (it.count() > 0) {
                                                    it.forEach {
                                                        if (it.id.toString() == data.messageId) {
                                                            //This is the post & belongs to that channel, create intent
                                                            displayIntent?.putExtra(Constants.IntentKeys.MESSAGE_ID, it.id)
                                                            displayIntent?.putExtra(Constants.IntentKeys.CHANNEL_NAME, channel.name)
                                                            it.mediaArray?.let {
                                                                if (it.count() > 0) {
                                                                    displayIntent?.putExtra(Constants.IntentKeys.INCLUDES_MEDIA, true)
                                                                }
                                                            }
                                                            displayIntent?.putExtra(Constants.IntentKeys.INCLUDES_MEDIA, false)
                                                            displayNotification(mBuilder, mManager, displayIntent!!)
                                                            return@getChannelPosts
                                                        }
                                                    }
                                                }
                                            }
                                        })

                                    }
                                }
                            }
                        }
                    }

                //TODO
                    Constants.Notifications.VOICE_CALL -> {
                        //Parse Notification Data Payload
                        val data = GsonHolder.instance.get()?.fromJson(jsonObject.toString(), IncommingCallPayload::class.java)
                        //TODO: Navigate to App's Phone Extension screen
                    }
                //TODO
                    Constants.Notifications.MISSED_VOICE_CALL -> {
                        //Parse Notification Data Payload
                        val data = GsonHolder.instance.get()?.fromJson(jsonObject.toString(), MissedCallPayload::class.java)
                        //TODO: Navigate to Contact's page
                    }
                //TODO
                    Constants.Notifications.VIDEO_CALL -> {
                        //Parse Notification Data Payload
                        val data = GsonHolder.instance.get()?.fromJson(jsonObject.toString(), IncommingCallPayload::class.java)
                        //TODO: Navigate to App's Video Call Extension screen
                    }
                //TODO
                    Constants.Notifications.MISSED_VIDEO_CALL -> {
                        //Parse Notification Data Payload
                        val data = GsonHolder.instance.get()?.fromJson(jsonObject.toString(), MissedCallPayload::class.java)
                        //TODO: Navigate to Contact's page
                    }
                /*Received a New Message*/
                    Constants.Notifications.MESSAGE -> {//Any Kind of message
                        //Parse Notification Data Payload
                        val data = GsonHolder.instance.get()?.fromJson(jsonObject.toString(), MessagePayload::class.java)
                        //TODO: Check the Room ID & Message Type to define where Navigation should be
                        //QUERY ROOM ID
                    }
                }
                //Dev
                println("Remote Message Data: " + jsonObject.toString())
            }
        }
    }

    /**
     * [displayNotification]
     * Used to build notification when intent has the data
     * */
    fun displayNotification(builder: NotificationCompat.Builder, manager: NotificationManager, intent: Intent) {
        builder.setContentIntent(PendingIntent.getActivity(
                this,
                Constants.RequestCodes.NOTIF_REQUEST_CODE,
                intent,
                PendingIntent.FLAG_ONE_SHOT)
        )
        manager.notify(createId(), builder.build())
    }

    /**
     * [createId]
     * Create a Notification ID so they're not recycled in the System Tray
     * This helps order notifications when multiple ones are instantiated in a
     * short period of time
     * */
    fun createId(): Int = Integer.parseInt(SimpleDateFormat("ddHHmmss", Locale.US).format(Date()))

}