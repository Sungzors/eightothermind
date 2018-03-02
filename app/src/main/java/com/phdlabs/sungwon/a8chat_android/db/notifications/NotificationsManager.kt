package com.phdlabs.sungwon.a8chat_android.db.notifications

/**
 * Created by paix on 2/28/18.
 * [NotificationsManager]
 * Used for [Notification] CRUD with API
 */
class NotificationsManager {

    /*Singleton*/
    private object Holder {
        val instance = NotificationsManager()
    }

    /*Companion*/
    companion object {
        val instance by lazy { Holder.instance }
    }

    /**
     * [setReceipt]
     * Turn on or off read receipts for every chat the user is a part of
     * */
    //TODO: RX call is ready
    //TODO: Warning -> Do not use the receipts call unitl Tomer Fixes the server crash
}