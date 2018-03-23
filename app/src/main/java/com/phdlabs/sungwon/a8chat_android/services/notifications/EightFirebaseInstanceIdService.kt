package com.phdlabs.sungwon.a8chat_android.services.notifications

import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import com.phdlabs.sungwon.a8chat_android.db.user.UserManager

/**
 * Created by JPAM on 3/20/18.
 * [EightFirebaseMessagingService] makes use of [FirebaseInstanceIdService]
 * for handling creation, rotation, and updating registration tokens.
 * This is required for sending to specific devices of creating service groups.
 */
class EightFirebaseInstanceIdService : FirebaseInstanceIdService() {

    //Update Firebase Token
    override fun onTokenRefresh() {
        super.onTokenRefresh()
        FirebaseInstanceId.getInstance().token?.let {
            UserManager.instance.updateFirebaseToken(it)
        } ?: run {
            println("No Firebase token available")
        }
    }
}