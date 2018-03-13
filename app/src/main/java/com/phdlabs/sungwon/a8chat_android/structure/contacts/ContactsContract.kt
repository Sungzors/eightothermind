package com.phdlabs.sungwon.a8chat_android.structure.contacts

import android.widget.RadioButton
import com.phdlabs.sungwon.a8chat_android.model.contacts.LocalContact
import com.phdlabs.sungwon.a8chat_android.structure.contacts.invite.InviteContactsActivity
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
            /*Activity*/
            var activity: InviteContactsActivity
            /*Refresh data*/
            fun stopRefreshing()
            /*Deliver contacts*/
            fun deliverLocalContacts(localContacts: ArrayList<LocalContact>)
            /*Feedback*/
            fun feedback(message: String)
            /*Clear Recycler View selections*/
            fun doneInvitingContacts()
        }

        interface Controller: BaseController {
            /*Load local contacts*/
            fun loadLocalContacts()
            /*Permissions*/
            fun permissionResults(requestCode: Int, permissions: Array<out String>, grantResults: IntArray):Boolean
            /*Notify Contacts*/
            fun notifyContacts(invitedContacts: ArrayList<LocalContact>)
        }

    }



}