package com.phdlabs.sungwon.a8chat_android.structure.myProfile.notifications

import android.os.Bundle
import android.widget.CompoundButton
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.db.notifications.NotificationsManager
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import kotlinx.android.synthetic.main.activity_notifications_global.*

/**
 * Created by JPAM on 3/20/18.
 * Global Notification Management
 */
class NotificationsGlobalSettings : CoreActivity(), CompoundButton.OnCheckedChangeListener {

    /*Layout*/
    override fun layoutId(): Int = R.layout.activity_notifications_global

    override fun contentContainerId(): Int = 0

    /*Properties*/
    private var mNotMan: NotificationsManager = NotificationsManager.instance

    /*LifeCycle*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Setup
        setupToolbar()
        setNotificationPref()
        setupClickers()
    }

    /*Toolbar*/
    private fun setupToolbar() {
        setToolbarTitle(getString(R.string.notification_settings))
        showBackArrow(R.drawable.ic_back, true)
    }

    private fun setNotificationPref() {
        //TODO: Set switches to user preferences
    }

    /*On Click*/
    private fun setupClickers() {
        ang_msg_notif_switch.setOnCheckedChangeListener(this)
        ang_like_notif_switch.setOnCheckedChangeListener(this)
        achs_comment_notif_switch.setOnCheckedChangeListener(this)
        achs_invited_notif_switch.setOnCheckedChangeListener(this)
    }


    override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {

        when (p0) {
        /*Message Notifications*/
            ang_msg_notif_switch -> {
                mNotMan.changeGlobalNotificationSettings(p1.toString(), null, null, null)
            }
        /*Like Notifications*/
            ang_like_notif_switch -> {
                mNotMan.changeGlobalNotificationSettings(null, p1.toString(), null, null)
            }
        /*Comment Notifications*/
            achs_comment_notif_switch -> {
                mNotMan.changeGlobalNotificationSettings(null, null, p1.toString(), null)
            }
        /*Followed Notifications*/
            achs_invited_notif_switch -> {
                mNotMan.changeGlobalNotificationSettings(null, null, null, p1.toString())
            }
        }
    }
}