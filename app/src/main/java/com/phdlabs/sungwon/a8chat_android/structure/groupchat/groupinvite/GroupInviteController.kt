package com.phdlabs.sungwon.a8chat_android.structure.groupchat.groupinvite

import android.app.AlertDialog
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
import com.phdlabs.sungwon.a8chat_android.model.contacts.Contact
import com.phdlabs.sungwon.a8chat_android.model.contacts.LocalContact
import com.phdlabs.sungwon.a8chat_android.structure.groupchat.GroupChatContract
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import com.phdlabs.sungwon.a8chat_android.utility.contacts.LocalContactsAsyncLoader
import com.vicpin.krealmextensions.queryAll
import com.vicpin.krealmextensions.saveAll
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by SungWon on 3/6/2018.
 */
class GroupInviteController(val mView: GroupChatContract.Invite.View) : GroupChatContract.Invite.Controller, LoaderManager.LoaderCallbacks<List<LocalContact>>{

    /*Properties*/
    private var mLocalContacts: ArrayList<LocalContact> = ArrayList()

    /**
     * Loader manager gets called twice for contacts
     * This is a useful flag to avoid double network calls.
     * */
    private var hasRepeatedMethodCall: Boolean = false
    private var hasAskedToRetry: Boolean = false

    init {
        mView.controller = this
    }

    override fun start() {
    }

    override fun resume() {
        loadContactsCheckCache()
    }

    override fun pause() {
    }

    override fun stop() {
    }

    /*CONTRACT*/

    /**
     * [loadContactsCheckCache]
     * - This function will check for permission & cached contacts before making an API call
     * */
    override fun loadContactsCheckCache() {
        //Check permissions
        if (ContextCompat.checkSelfPermission(mView.activity, Constants.AppPermissions.CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestReadingContactsPermissions()
        } else {  //Check for cached contacts
            val mEightFriends = Contact().queryAll()
            if (mEightFriends.count() == 0) { //Load contacts from API
                //Load manager will inhibit if it's already going on
                mView.showProgress()
                mView.activity.loaderManager.initLoader(0, null, this).forceLoad()
            }
        }
    }

    /**
     * [loadContactsFromApi]
     * - This method will check for permissions & load contacts from API
     * */
    private fun loadContactsFromApi() {
        if (ContextCompat.checkSelfPermission(mView.activity, Constants.AppPermissions.CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestReadingContactsPermissions()
        } else {
            //Load manager will inhibit if it's already going on
            mView.showProgress()
            mView.activity.loaderManager.initLoader(0, null, this).forceLoad()
        }
    }

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

            //Process contacts with API
            if (mLocalContacts.count() == 0 && !hasRepeatedMethodCall && !hasAskedToRetry) {

                val dialogBuilder = AlertDialog.Builder(mView.activity)
                hasRepeatedMethodCall = !hasRepeatedMethodCall
                hasAskedToRetry = !hasAskedToRetry
                dialogBuilder.setMessage("You don't have any more Eight contacts")
                        .setPositiveButton("ok") { _, _ ->
                        }
                        .setNegativeButton("retry", { _, _ ->
                            loadContactsFromApi()
                        })
                dialogBuilder.create().show()
                mView.hideProgress()

            } else if (mLocalContacts.count() > 0 && hasRepeatedMethodCall) {
                hasRepeatedMethodCall = !hasRepeatedMethodCall
                getEightContacts(mLocalContacts)
                mView.hideProgress()
            }
        }
    }

    override fun onLoaderReset(p0: Loader<List<LocalContact>>?) {
        mLocalContacts.clear()
    }

    /**
     * [getEightContacts]
     * Retrieves the contacts that are related with Eight through the API
     * */
    private fun getEightContacts(localContacts: ArrayList<LocalContact>) {
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
                                                    } else {
                                                        mView.hideProgress()
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

                                            } else if (response.isError) { //Error
                                                mView.hideProgress()
                                                mView.showError(response.message)
                                            }
                                        },
                                        //On error implementation
                                        { throwable ->
                                            mView.hideProgress()
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
                                                    hasAskedToRetry = !hasAskedToRetry
                                                    hasRepeatedMethodCall = !hasRepeatedMethodCall


                                                    mView.hideProgress()
                                                }

                                            } else if (response.isError) { //Error

                                                mView.hideProgress()
                                                mView.showError(response.message)

                                            }

                                        },

                                        //On error implementation
                                        { throwable ->
                                            mView.hideProgress()
                                            println("Error downloading friends: " + throwable.message)
                                        })

                    }
                }
            }
        }
    }
}