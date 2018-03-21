package com.phdlabs.sungwon.a8chat_android.structure.myProfile.notifications

import android.os.Bundle
import android.view.View
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import kotlinx.android.synthetic.main.activity_notifications_global.*

/**
 * Created by paix on 3/20/18.
 */
class NotificationsGlobalSettings : CoreActivity(), View.OnClickListener {

    /*Layout*/
    override fun layoutId(): Int = R.layout.activity_notifications_global

    override fun contentContainerId(): Int = 0

    /*LifeCycle*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Setup Clickers

        setupClickers()
    }

    /*On Click*/
    private fun setupClickers() {
        ang_message_notification_container.setOnClickListener(this)
        ang_like_notification_container.setOnClickListener(this)
        ang_comment_notification_container.setOnClickListener(this)
        ang_followed_notification_container.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0) {
        /*Message Notifications*/
            ang_message_notification_container -> {

            }
        /*Like Notifications*/
            ang_message_notification_container -> {

            }
        /*Comment Notifications*/
            ang_comment_notification_container -> {

            }
        /*Followed Notifications*/
            ang_followed_notification_container -> {

            }
        }
    }
}