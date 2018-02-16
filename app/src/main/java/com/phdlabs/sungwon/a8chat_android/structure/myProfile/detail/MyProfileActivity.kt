package com.phdlabs.sungwon.a8chat_android.structure.myProfile.detail

import android.os.Bundle
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import com.phdlabs.sungwon.a8chat_android.structure.myProfile.ProfileContract

/**
 * Created by paix on 2/16/18.
 * [MyProfileActivity]  is used to access
 * - Calendars
 * - Payments
 * - Notification Settings
 * - Fav Messages
 * - Account
 * - Terms & Support
 * - Invite Friends
 */
class MyProfileActivity : CoreActivity(), ProfileContract.MyProfile.View {

    /*Controller*/
    override lateinit var controller: ProfileContract.MyProfile.Controller

    /*Layout*/
    override fun layoutId(): Int  = R.layout.activity_my_profile
    override fun contentContainerId(): Int = 0

    /*LifeCycle*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MyPrfofileAController(this)
    }

    override fun onStart() {
        super.onStart()
        controller.start()
    }

    override fun onResume() {
        super.onResume()
        controller.resume()
    }

    override fun onPause() {
        super.onPause()
        controller.pause()
    }

    override fun onStop() {
        super.onStop()
        controller.stop()
    }




}