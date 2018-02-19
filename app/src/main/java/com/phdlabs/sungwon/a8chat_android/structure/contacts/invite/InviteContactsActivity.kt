package com.phdlabs.sungwon.a8chat_android.structure.contacts.invite

import android.os.Bundle
import com.phdlabs.sungwon.a8chat_android.structure.contacts.ContactsContract
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity

/**
 * Created by paix on 2/19/18.
 * [InviteContactsActivity] used to invite firends to join Eight App
 * This activity will trigger an sms through the API to invite friends
 */
class InviteContactsActivity : CoreActivity(), ContactsContract.InviteContacts.View {
    override lateinit var controller: ContactsContract.InviteContacts.Controller

    override fun layoutId(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun contentContainerId(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        InviteContactsController(this)
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