package com.phdlabs.sungwon.a8chat_android.db.user

import com.phdlabs.sungwon.a8chat_android.api.rest.Rest
import com.phdlabs.sungwon.a8chat_android.model.user.settings.GlobalSettings
import com.vicpin.krealmextensions.queryFirst
import com.vicpin.krealmextensions.save
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


/**
 * Created by paix on 3/24/18.
 */
class SettingsManager {

    private object Holder {
        val INSTANCE = SettingsManager()
    }

    companion object {
        val instance: SettingsManager by lazy { Holder.INSTANCE }
    }


    /**
     *  [readUserSettings]
     *  Read & Cache saved user settings in API
     *  @param userId
     *  @param callback Pair(Settings, ErrorMessage)
     * */
    fun readUserSettings() {
        UserManager.instance.getCurrentUser { success, user, token ->
            if (success) {
                user?.let {
                    token?.token?.let {
                        val call = Rest.getInstance().getmCallerRx().readGlobalNotificationSettings(it, user.id!!)
                        call.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe ({ response ->
                                    if (response.isSuccess) {
                                        //Build Global Settings
                                        val globalSettings = GlobalSettings()
                                        globalSettings.id = user.id!!
                                        globalSettings.language = response.language
                                        globalSettings.readReceipts = response.readReceipts
                                        globalSettings.message_notifications = response.message_notifications
                                        globalSettings.like_notifications = response.like_notifications
                                        globalSettings.comment_notifications = response.comment_notifications
                                        globalSettings.user_added_notifications = response.user_added_notifications
                                        response?.blockedUsers?.let {
                                            for (contact in it) {
                                                globalSettings.blockedUsers?.add(contact)
                                            }
                                        }
                                        globalSettings.save()
                                    } else if (response.isError) {
                                        println("Could not sync global user settings")
                                    }
                                }, { throwable ->
                                    println(throwable.localizedMessage)
                                })
                    }
                }
            }
        }
    }

    /**
     *  [globalUserSettings]
     *  Query cached Global User Settings
     * */
    fun globalUserSettings(userId: Int): GlobalSettings? {
        return GlobalSettings().queryFirst { equalTo("id", userId) }
    }
}