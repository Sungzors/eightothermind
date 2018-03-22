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

    //TODO: User followed channel RemoteMessage doesn't hold a notification_type -> Talk with Tomer

    override fun onMessageReceived(p0: RemoteMessage?) {
        super.onMessageReceived(p0)
        p0?.let {
            //Dev
            println("Remote Message Type: " + it.messageType)//Not set by Backend
            println("Remote Message Sent Time: " + it.sentTime)
            println("Remote Message Collapse Key: " + it.collapseKey)
            println("Remote Message From: " + it.from)
            //Check for data
            it.data?.let {
                if (it.count() > 0) {
                    //Get Notification Type
                    val jsonObject = JSONObject(it)
                    
                    //Dev
                    println("Remote Message Data: " + jsonObject.toString())
                }
            }
        }
    }


    override fun onDeletedMessages() {
        super.onDeletedMessages()
    }
}