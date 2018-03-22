package com.phdlabs.sungwon.a8chat_android.services.notifications

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONObject

/**
 * Created by paix on 3/19/18.
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
        //Check for Data Payload
        data?.let {
            if (it.count() > 0) {
                //Get Notification Type
                val jsonObject = JSONObject(it)

                //Dev
                println("Remote Message Data: " + jsonObject.toString())
            }
        }
        //Check for Notification Payload
        notification?.let {
            //TODO: Handle Notification body payload

        }
    }
}