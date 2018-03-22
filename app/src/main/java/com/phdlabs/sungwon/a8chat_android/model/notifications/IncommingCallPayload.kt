package com.phdlabs.sungwon.a8chat_android.model.notifications

/**
 * Created by paix on 3/21/18.
 * Incomming Twillio Call through App
 */
class IncommingCallPayload : EightNotification() {
    var userCallingId: String? = null
    var userReceivingCallId: String? = null
    var roomId: String? = null
}