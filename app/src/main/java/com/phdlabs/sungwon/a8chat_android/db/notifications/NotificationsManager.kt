package com.phdlabs.sungwon.a8chat_android.db.notifications

import com.phdlabs.sungwon.a8chat_android.api.rest.Rest
import com.phdlabs.sungwon.a8chat_android.db.user.UserManager
import com.vicpin.krealmextensions.save
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


/**
 * Created by JPAM on 2/28/18.
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

    val disposable = CompositeDisposable()

    /**
     * [clearNotificationBadges]
     * Clear all notification badges once every time the application opens
     * */
    fun clearNotificationBadges() {
        UserManager.instance.getCurrentUser { success, user, token ->
            if (success) {
                user?.let {
                    token?.token?.let {
                        val call = Rest.getInstance().getmCallerRx().clearNotificationBadgeCount(it, user.id!!)
                        call.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({ response ->
                                    if (response.isSuccess) {
                                        response.message?.let {
                                            println(it)
                                        }
                                    } else if (response.isError) {
                                        response.message?.let {
                                            print(it)
                                        }
                                    }
                                    disposable.clear()
                                }, { throwable ->
                                    println("Could not clear notification badges:  + " + throwable.localizedMessage)
                                })
                    }
                }
            }
        }
    }

    /**
     * [changeGlobalNotificationSettings]
     * Change global notifications settings on user profile, with Query parameters targetting specific updates at once
     * @query messageNotifications
     * @query likeNotifications
     * @query commentNotifications
     * @query userAddedNotification
     * */
    fun changeGlobalNotificationSettings(messageNotifications: String?, likeNotifications: String?,
                                         commentNotifications: String?, userAddedNotification: String?) {
        UserManager.instance.getCurrentUser { success, user, token ->
            if (success) {
                user?.let {
                    token?.token?.let {
                        val call = Rest.getInstance().getmCallerRx().changeGlobalNotificationSettings(
                                it, user.id!!, messageNotifications, likeNotifications,
                                commentNotifications, userAddedNotification
                        )
                        call.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({ response ->
                                    if (response.isSuccess) {
                                        response.user?.let {
                                            it.save()
                                        }
                                    } else if (response.isError) {
                                        println("Error changing Global Notifications")
                                    }
                                    disposable.clear()
                                }, { throwable ->
                                    println("Error changing Global Notifications: " + throwable.localizedMessage)
                                })
                    }
                }
            }
        }
    }

}