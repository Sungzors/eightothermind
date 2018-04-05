package com.phdlabs.sungwon.a8chat_android.structure.createnew

import com.phdlabs.sungwon.a8chat_android.model.channel.Channel
import com.phdlabs.sungwon.a8chat_android.model.contacts.Contact
import com.phdlabs.sungwon.a8chat_android.structure.core.BaseController
import com.phdlabs.sungwon.a8chat_android.structure.core.BaseView

/**
 * Created by JPAM on 3/12/18.
 */
interface CreateNewContract {

    //Activity
    interface CreateNew {

        interface View : BaseView<Controller> {
            val getAct: CreateNewActivity
            //Default UI
            fun setupDefaultUI()

            //Contact Data Load
            fun getContactData()

        }

        interface Controller : BaseController {
            //Contacts
            fun queryContacts(callback: (Pair<MutableList<Contact>?, MutableList<Contact>?>) -> Unit)

            //Channels
            fun openChannel(channelId: Int?, channelName: String?, channelRoomId: Int?, channelCreatorId: Int?)
        }

    }

    //Contacts Fragment
    interface ContactSearch {
        interface View : BaseView<Controller> {
            var getAct: CreateNewActivity
            //Update Contact Data
            fun updateAllContactsRecycler(contacts: MutableList<Contact>?)

            fun updateFavContactsRecycler(contacts: MutableList<Contact>?)
            //Filter Contact Data (Search)
            fun filterContactsAdapter(p0: String?)
        }

        interface Controller : BaseController {
            //Query Contact Data
            fun getContactData()

            //Update Contact Data
            fun pushContactInfoChanges(contacts: Pair<MutableList<Contact>?, MutableList<Contact>?>)

            //Filter Contact Data
            fun pushContactFilterChanges(p0: String?)
        }
    }

}