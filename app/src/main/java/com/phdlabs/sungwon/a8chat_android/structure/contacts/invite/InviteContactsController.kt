package com.phdlabs.sungwon.a8chat_android.structure.contacts.invite

import android.app.LoaderManager
import android.content.Loader
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.api.data.ContactsPostData
import com.phdlabs.sungwon.a8chat_android.api.rest.Rest
import com.phdlabs.sungwon.a8chat_android.db.user.UserManager
import com.phdlabs.sungwon.a8chat_android.model.contacts.LocalContact
import com.phdlabs.sungwon.a8chat_android.structure.contacts.ContactsContract
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import com.phdlabs.sungwon.a8chat_android.utility.contacts.LocalContactsAsyncLoader
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


/**
 * Created by paix on 2/19/18.
 * [InviteContactsController] for [InviteContactsActivity]
 */
class InviteContactsController(val mView: ContactsContract.InviteContacts.View) :
        ContactsContract.InviteContacts.Controller,
        LoaderManager.LoaderCallbacks<List<LocalContact>> {

    /*Properties*/
    private var mLocalContacts: ArrayList<LocalContact> = ArrayList()

    /*Initialize*/
    init {
        mView.controller = this
    }

    /*LifeCycle*/
    override fun start() {
        loadLocalContacts()
    }

    override fun resume() {
    }

    override fun pause() {
    }

    override fun stop() {
        mView.doneInvitingContacts()
    }

    /**
     * [loadLocalContacts]
     * Local contacts are not cached, therefore we need to query fresh added contacts from
     * the [ContactsFileProvider]
     * */
    override fun loadLocalContacts() {
        if (ContextCompat.checkSelfPermission(mView.activity, Constants.AppPermissions.CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestReadingContactsPermissions()
        } else { //Load local contacts
            mView.showProgress()
            mView.activity.loaderManager.initLoader(0, null, this).forceLoad()
        }
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
                ActivityCompat.requestPermissions(mView.activity, whatPermissions, Constants.PermissionsReqCode.CONTACTS_REQ_CODE)
            }
        }
    }

    override fun permissionResults(requestCode: Int, permissions: Array<out String>, grantResults: IntArray): Boolean {
        if (requestCode == Constants.PermissionsReqCode.CONTACTS_REQ_CODE) {
            if (grantResults.size != 1 || grantResults.get(0) != PackageManager.PERMISSION_GRANTED) {
                mView.showError(mView.activity.getString(R.string.request_read_contacts_permissions))
            } else {
                mView.showProgress()
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
            //Dev
            println(" CONTACT COUNT: " + mLocalContacts.count())
            //Process local contacts
            if (mLocalContacts.count() > 0) {
                mView.deliverLocalContacts(mLocalContacts)
                mView.hideProgress()
                mView.stopRefreshing()
            }
        }
    }

    override fun onLoaderReset(p0: Loader<List<LocalContact>>?) {
        mLocalContacts.clear()
    }

    /**
     * Notify Contacts
     * */
    override fun notifyContacts(invitedContacts: ArrayList<LocalContact>) {
        mView.showProgress()
        UserManager.instance.getCurrentUser { success, user, token ->
            if (success) {
                token?.token?.let {
                    user?.id?.let {
                        val contactsPostData = ContactsPostData(invitedContacts.toArray())
                        val call = Rest.getInstance().getmCallerRx().inviteContactsToEight(
                                token.token!!,
                                user.id!!,
                                contactsPostData.contactsArray
                        )
                        call.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                        { response ->
                                            if (response.isSuccess) {//Success
                                                //Invited
                                                response.successfullySentSMS?.let {
                                                    if (it.count() > 0){
                                                        mView.feedback(
                                                                "Successfully invited your contacts"
                                                        )
                                                        mView.hideProgress()
                                                        mView.doneInvitingContacts()
                                                    }
                                                }
                                                //Couldn't invite
                                                response.unsuccessfullySentSMS?.let {
                                                    if (it.count() > 0){
                                                        mView.showError(
                                                                "Check the phone numbers, Eight couldn't invite some of your contacts"
                                                        )
                                                        mView.hideProgress()
                                                        mView.doneInvitingContacts()
                                                    }
                                                }
                                            }else if(response.isError) {//Error

                                            }
                                        },
                                        //On error implementation
                                        { throwable ->
                                            mView.hideProgress()
                                            mView.doneInvitingContacts()
                                            println("Error inviting contacts: " + throwable.message)
                                        })
                    }
                }
            }
        }
    }

}