package com.phdlabs.sungwon.a8chat_android.structure.contacts

import android.widget.RadioButton
import com.phdlabs.sungwon.a8chat_android.structure.core.BaseController
import com.phdlabs.sungwon.a8chat_android.structure.core.BaseView

/**
 * Created by paix on 2/13/18.
 * Contacts Interface [ContactsActivity] & [ContactsAController]
 */
interface ContactsContract {

    /*Contacts Activity -> Contacts currently in Eight*/

    interface EightFriends {

        interface View : BaseView<Controller> {

            /*Activity*/
            var activity: ContactsActivity

            /*UI*/
            fun updateContactSelector(string: String, contactCount: Int)

            fun updateChannelsSelector(string: String, channelCount: Int)

            /*Refresh data*/
            fun stopRefreshing()

        }

        interface Controller : BaseController {

            /*load contacts*/
            fun loadContactsCheckCache()
            fun loadContactsFromApi()
            /*load channels*/
            fun loadChannels()

            /*Permissions*/
            fun permissionResults(requestCode: Int, permissions: Array<out String>, grantResults: IntArray):Boolean

        }
    }


    /*Invite Friends Activity -> Invite phone contacts to Eight*/

    interface InviteContacts {

        interface View: BaseView<Controller> {

        }

        interface Controller: BaseController {

        }

    }



}