package com.phdlabs.sungwon.a8chat_android.structure.contacts.fragments

import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreFragment

/**
 * Created by paix on 2/13/18.
 * [ContactsFragment]
 * - Will load local contacts & send phone numbers to server.
 * - Server will return id for users available in 8.
 * - Those users are the ones that will be shown.
 */
class ContactsFragment: CoreFragment()  {

    /*Layout*/
    override fun layoutId(): Int = R.layout.fragment_contacts


}