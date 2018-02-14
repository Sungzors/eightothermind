package com.phdlabs.sungwon.a8chat_android.structure.contacts

import com.phdlabs.sungwon.a8chat_android.structure.core.BaseController
import com.phdlabs.sungwon.a8chat_android.structure.core.BaseView

/**
 * Created by paix on 2/13/18.
 * Contacts Interface [ContactsActivity] & [ContactsAController]
 */
interface ContactsAContract {

    interface View : BaseView<Controller> {

        /*Activity*/
        var activity: ContactsActivity

    }

    interface Controller : BaseController {

        fun onCreate()

        /*load contacts*/
        fun loadContacts()

        /*load channels*/
        fun loadChannels()

        /*Permissions*/
        fun permissionResults(requestCode: Int, permissions: Array<out String>, grantResults: IntArray):Boolean

    }

}