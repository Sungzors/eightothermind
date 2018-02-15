package com.phdlabs.sungwon.a8chat_android.structure.contacts

import android.app.LoaderManager
import android.content.Loader
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat.requestPermissions
import android.support.v4.content.ContextCompat
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.api.data.ContactsPostData
import com.phdlabs.sungwon.a8chat_android.api.rest.Rest
import com.phdlabs.sungwon.a8chat_android.db.UserManager
import com.phdlabs.sungwon.a8chat_android.model.contacts.Contact
import com.phdlabs.sungwon.a8chat_android.model.contacts.LocalContact
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import com.phdlabs.sungwon.a8chat_android.utility.contacts.LocalContactsAsyncLoader
import com.vicpin.krealmextensions.saveAll
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

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
        //Contact permissions
        if (ContextCompat.checkSelfPermission(mView.activity, Constants.AppPermissions.CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestReadingContactsPermissions()
        } else {
            mView.showProgress()
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
        //Check permissions
        if (ContextCompat.checkSelfPermission(mView.activity, Constants.AppPermissions.CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestReadingContactsPermissions()
        } else {
            mView.showProgress()
            //Load manager will inhibit if it's already going on
            mView.activity.loaderManager.initLoader(0, null, this).forceLoad()
        }
    }

    override fun loadChannels() {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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
            mView.hideProgress()
            //Dev
            println(" CONTACT COUNT: " + mLocalContacts.count())
            //Process contacts with API
            if (mLocalContacts.count() > 0) {
                getEightContacts(mLocalContacts)
            }
        }
    }

    override fun onLoaderReset(p0: Loader<List<LocalContact>>?) {
        mLocalContacts.clear()
    }

    /*NETWORK-API*/

    /**
     * [getEightContacts]
     * Retrieves the contacts that are related with Eight through the API
     * */
    private fun getEightContacts(localContacts: ArrayList<LocalContact>) {
        mView.showProgress()
        UserManager.instance.getCurrentUser { success, user, token ->
            if (success) {
                token?.token?.let {
                    user?.id?.let {
                        val contactsPostData = ContactsPostData(localContacts.toArray())
                        val call = Rest.getInstance().getmCallerRx().getEightContactsPhoneNumbers(
                                token.token!!,
                                user.id!!,
                                contactsPostData.contactsArray
                        )
                        call.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                        { response ->
                                            if (response.isSuccess) { //Success
                                                //Successfull Accounts

                                                response.userContactsWithEightAccount?.let {
                                                    val validContacts: ArrayList<String> = ArrayList()
                                                    for (phone in it) {
                                                        validContacts.add(phone)
                                                    }
                                                    //Get friends info
                                                    if (validContacts.count() > 0) {
                                                        getEightFriends()
                                                    }
                                                    //Dev
                                                    println("Successfull number of accounts: " + validContacts.count())
                                                }

                                                //Unsuccessfull accounts
                                                response.undeterminedPhoneNumbers?.let {
                                                    val invalidContacts: ArrayList<String> = ArrayList()
                                                    for (phone in it) {
                                                        invalidContacts.add(phone)
                                                    }
                                                    //Dev
                                                    println("Unsuccessfull number of accounts: " + invalidContacts.count())
                                                }

                                                getEightFriends()

                                                mView.hideProgress()
                                            } else if (response.isError) { //Error
                                                mView.hideProgress()
                                                mView.showError(response.message)
                                            }
                                        },
                                        //On error implementation
                                        { throwable ->
                                            println("Error downloading contacts: " + throwable.message)
                                        })
                    }
                }
            }
        }

    }

    private fun getEightFriends() {
        UserManager.instance.getCurrentUser { success, user, token ->
            if (success) {
                token?.token?.let {
                    user?.id?.let {
                        var call = Rest.getInstance().getmCallerRx().getUserFriends(token.token!!, user.id!!)
                        call.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                        { response ->

                                            if (response.isSuccess) { //Success

                                                response.users?.let {

                                                    /**
                                                     * Cache Friends or Eight[Contact] in
                                                     * @see Realm
                                                     * */
                                                    it.saveAll()

                                                    /*Update UI*/
                                                    mView.updateContactSelector("Contacts (" + it.count() + ")",
                                                            it.count())
                                                }

                                            } else if (response.isError) { //Error
                                                mView.hideProgress()
                                                mView.showError(response.message)
                                            }

                                        },

                                        //On error implementation
                                        { throwable ->
                                            println("Error downloading friends: " + throwable.message)
                                        })

                    }
                }
            }
        }
    }


}