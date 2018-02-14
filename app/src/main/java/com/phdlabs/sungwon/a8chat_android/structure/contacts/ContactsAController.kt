package com.phdlabs.sungwon.a8chat_android.structure.contacts

import android.app.LoaderManager
import android.content.ContentResolver
import android.content.Loader
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.support.v4.app.ActivityCompat.requestPermissions
import android.support.v4.content.ContextCompat
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.model.contacts.LocalContact
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import com.phdlabs.sungwon.a8chat_android.utility.contacts.LocalContactsAsyncLoader

/**
 * Created by paix on 2/13/18.
 * ContractsActivityController for [ContactsActivity]
 */
class ContactsAController(val mView: ContactsAContract.View) :
        ContactsAContract.Controller,
        LoaderManager.LoaderCallbacks<List<LocalContact>> {

    /*Properties*/
    private var mLocalContacts: ArrayList<LocalContact> = ArrayList()

    /*Initializer*/
    init {

        //Controller
        mView.controller = this

    }

    /*LifeCycle*/
    override fun onCreate() {
        //TODO: Check for permissions
        if (ContextCompat.checkSelfPermission(mView.activity, Constants.AppPermissions.CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestReadingContactsPermissions()
        } else {
            mView.activity.loaderManager.initLoader(0, null, this).forceLoad()
        }
    }

    override fun start() {
    }

    override fun resume() {
    }

    override fun pause() {
    }

    override fun stop() {
    }

    /*CONTRACT*/
    override fun loadContacts() {
        //TODO: load local contacts
        //loadLocalContacts()

    }

    override fun loadChannels() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /*PERMISSIONS*/
    /**External contact permissions
     * */
    private fun requestReadingContactsPermissions() {
        //Required permissions
        val whatPermissions = arrayOf(Constants.AppPermissions.CONTACTS)
        mView.activity.context?.let {
            //Request Permissions
            if (ContextCompat.checkSelfPermission(it, whatPermissions.get(0)) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(mView.activity, whatPermissions, Constants.PermissionsReqCode.CONTACTS_REQ_CODE)
            }
        }
    }

    override fun permissionResults(requestCode: Int, permissions: Array<out String>, grantResults: IntArray): Boolean {
        if (requestCode == Constants.PermissionsReqCode.CONTACTS_REQ_CODE) {
            if (grantResults.size != 1 || grantResults.get(0) != PackageManager.PERMISSION_GRANTED) {
                mView.showError(mView.activity.getString(R.string.request_read_contacts_permissions))
            } else {
                mView.activity.loaderManager.initLoader(0, null, this).forceLoad()
            }
            return true
        } else {
            return false
        }
    }

    /**
     * [LoaderManager.LoaderCallbacks]
     * - Async Contact list loading
     * */
    override fun onCreateLoader(p0: Int, p1: Bundle?): Loader<List<LocalContact>> = LocalContactsAsyncLoader(mView.activity.context!!)

    override fun onLoadFinished(p0: Loader<List<LocalContact>>?, p1: List<LocalContact>?) {
        mLocalContacts.clear()
        p1?.let {
            for (contact in it) {
                mLocalContacts.add(contact)
            }
            //TODO: Get count to display in Selector
            println(" CONTACT COUNT: " + mLocalContacts.count())

            //TODO: Process

            //TODO: setupRecycler()
        }
    }

    override fun onLoaderReset(p0: Loader<List<LocalContact>>?) {
        mLocalContacts.clear()
    }


}