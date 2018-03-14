package com.phdlabs.sungwon.a8chat_android.structure.createnew.searchContacts

import com.phdlabs.sungwon.a8chat_android.model.contacts.Contact
import com.phdlabs.sungwon.a8chat_android.structure.createnew.CreateNewContract

/**
 * Created by paix on 3/13/18.
 */
class ContactSearchFragController(val mView: CreateNewContract.ContactSearch.View) :
        CreateNewContract.ContactSearch.Controller {

    init {
        mView.controller = this
    }

    /*LifeCycle*/
    override fun start() {
    }

    override fun resume() {
    }

    override fun pause() {
    }

    override fun stop() {
    }

    /*Contacts*/
    override fun getContactData() {
        mView.getAct.getContactData()
    }

    override fun pushContactInfoChanges(contacts: Pair<MutableList<Contact>?, MutableList<Contact>?>) {
        //All contacts
        contacts.first?.let {
            mView.updateAllContactsRecycler(it)
        }
        //Fav contacts
        contacts.second?.let {
            mView.updateFavContactsRecycler(it)
        }
    }

    override fun pushContactFilterChanges(p0: String?) {
        p0?.let {
            mView.filterContactsAdapter(it)
        } 
    }

}