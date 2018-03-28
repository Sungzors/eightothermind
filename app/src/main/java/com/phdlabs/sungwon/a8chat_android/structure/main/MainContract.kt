package com.phdlabs.sungwon.a8chat_android.structure.main

import com.phdlabs.sungwon.a8chat_android.structure.application.Application
import com.phdlabs.sungwon.a8chat_android.structure.core.BaseController
import com.phdlabs.sungwon.a8chat_android.structure.core.BaseView

/**
 * Created by SungWon on 10/13/2017.
 */
interface MainContract {

    interface View : BaseView<Controller> {
        val activity: MainActivity
    }

    interface Controller : BaseController {
        /*LifeCycle*/
        fun onCreate()

        fun showHome()
        fun showCamera()
        fun getUserId(callback: (Int?) -> Unit)
        /*Firebase & Dwolla Tokens*/
        fun updateTokens()

        /*Notifications*/
        fun updateNotificationBadges()

        /*Global Settings*/
        fun readGlobalSettings()
    }
}