package com.phdlabs.sungwon.a8chat_android.structure.createnew

import com.phdlabs.sungwon.a8chat_android.model.contacts.Contact
import com.vicpin.krealmextensions.query
import com.vicpin.krealmextensions.queryAll

/**
 * Created by JPAM on 3/12/18.
 */
class CreateNewAController(val mView: CreateNewContract.CreateNew.View) : CreateNewContract.CreateNew.Controller {

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

    override fun queryContacts(callback: (Pair<MutableList<Contact>?, MutableList<Contact>?>) -> Unit) {
        callback(Pair(
                Contact().queryAll().toMutableList(),
                Contact().query { equalTo("isFavorite", true) }.toMutableList())
        )
    }

}