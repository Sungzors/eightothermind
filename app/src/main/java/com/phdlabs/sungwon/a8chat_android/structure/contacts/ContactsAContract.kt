package com.phdlabs.sungwon.a8chat_android.structure.contacts

import android.widget.RadioButton
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

        /*UI*/
        fun updateContactSelector(string: String, contactCount: Int)

        fun updateChannelsSelector(string: String, channelCount: Int)

    }

    interface Controller : BaseController {

        /*LifeCycle*/
        fun onCreate()

        /*load contacts*/
        fun loadContacts()

        /*load channels*/
        fun loadChannels()

        /*Permissions*/
        fun permissionResults(requestCode: Int, permissions: Array<out String>, grantResults: IntArray):Boolean

    }

}