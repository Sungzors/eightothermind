package com.phdlabs.sungwon.a8chat_android.model.notifications

/**
 * Created by JPAM on 3/21/18.
 * Missed Twillio Call through App
 */
class MissedCallPayload {
    var userCallingId: String? = null
    var userReceivingCallId: String? = null
    var roomId: String? = null
    var notification_type: String? = null
    var badges: String? = null
}