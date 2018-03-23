package com.phdlabs.sungwon.a8chat_android.services.notifications

import android.content.Intent
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.phdlabs.sungwon.a8chat_android.structure.main.MainActivity
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import org.json.JSONObject

/**
 * Created by JPAM on 3/19/18.
 * [EightFirebaseMessagingService] makes use of [FirebaseMessagingService]
 * to provide messaging handling beyond receiving notifications whtn the
 * app is in the background.
 * This also handles notifications in foreground, data payload, and upstream messages.
 */
class EightFirebaseMessagingService : FirebaseMessagingService() {


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
        var displayIntent: Intent? = null
        //Check for Data Payload
        data?.let {
            if (it.count() > 0) {
                //Build Notification based on Type
                val jsonObject = JSONObject(it)
                when (jsonObject.getString(Constants.Notifications.TYPE)) {
                    Constants.Notifications.USER_ADDED -> {//GroupChat & Event
                        //TODO: Navigates to the Contacts page
                    }
                    Constants.Notifications.PRIVATE_CHAT -> {//Private Chat Created -> Room
                        //TODO: Navigates to the Chat Screen
                    }
                    Constants.Notifications.COMMENT -> {//Channel
                        //TODO: Navigates to the Commented Post
                    }
                    Constants.Notifications.LIKE -> {//Channel
                        //TODO: Navigates to the Liked Post
                    }
                    Constants.Notifications.POST -> {//Channel
                        //TODO: Navigates to the Channel
                    }
                    Constants.Notifications.VOICE_CALL -> {
                        //TODO: Navigate to App's Phone Extension screen
                    }
                    Constants.Notifications.MISSED_VOICE_CALL -> {
                        //TODO: Navigate to Contact's page
                    }
                    Constants.Notifications.VIDEO_CALL -> {
                        //TODO: Navigate to App's Video Call Extension screen
                    }
                    Constants.Notifications.MISSED_VIDEO_CALL -> {
                        //TODO: Navigate to Contact's page
                    }
                    Constants.Notifications.MESSAGE -> {//Any Kind of message
                        //TODO: Check the Room ID & Message Type to define where Navigation should be
                        //QUERY ROOM ID
                    }
                }
                //Dev
                println("Remote Message Data: " + jsonObject.toString())
            }
        }
        //Check for Notification Payload
        notification?.let {
            //TODO: Handle Notification body payload
        }
        //TODO: Clear notifications badges on pending intent opened ->
    }
}