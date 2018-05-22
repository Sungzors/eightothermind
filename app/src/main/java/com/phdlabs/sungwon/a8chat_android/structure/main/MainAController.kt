package com.phdlabs.sungwon.a8chat_android.structure.main

import android.app.LoaderManager
import android.content.Context
import android.content.Intent
import android.content.Loader
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.widget.Toast
import com.google.firebase.iid.FirebaseInstanceId
import com.phdlabs.sungwon.a8chat_android.api.data.ContactsPostData
import com.phdlabs.sungwon.a8chat_android.api.rest.Rest
import com.phdlabs.sungwon.a8chat_android.db.notifications.NotificationsManager
import com.phdlabs.sungwon.a8chat_android.db.user.SettingsManager
import com.phdlabs.sungwon.a8chat_android.db.user.UserManager
import com.phdlabs.sungwon.a8chat_android.model.contacts.Contact
import com.phdlabs.sungwon.a8chat_android.model.contacts.LocalContact
import com.phdlabs.sungwon.a8chat_android.services.googlePlay.CheckGPServices
import com.phdlabs.sungwon.a8chat_android.structure.camera.CameraActivity
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import com.phdlabs.sungwon.a8chat_android.utility.contacts.LocalContactsAsyncLoader
import com.vicpin.krealmextensions.queryAll
import com.vicpin.krealmextensions.saveAll
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * Created by SungWon on 10/13/2017.
 * Updated by JPAM on 02/12/2018
 */
class MainAController(val mView: MainContract.View) : MainContract.Controller, LoaderManager.LoaderCallbacks<List<LocalContact>> {

    /*Properties*/
    private var mLocationManager: LocationManager? = null
    private var mLocation: Pair<Double, Double> = Pair(0.0, 0.0)
    private var mUserManager: UserManager
    private var mLocalContacts: ArrayList<LocalContact> = ArrayList()

    /**
     * Loader manager gets called twice for contacts
     * This is a useful flag to avoid double network calls.
     * */
    private var hasRepeatedMethodCall: Boolean = false
    private var hasAskedToRetry: Boolean = false
    /**
     * [disposables]
     * List of disposables used for memory management during user navigation
     * */
    val disposables: MutableList<Disposable> = mutableListOf()

    /**
     * [clearDisposables]
     * Release API RX Call resources for memory management
     * */
    private fun clearDisposables() {
        for (disposable in disposables) {
            if (!disposable.isDisposed) {
                disposable.dispose()
            }
        }
        disposables.clear()
    }


    init {
        mView.controller = this
        mUserManager = UserManager.instance
        mLocationManager = mView.getContext()?.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
    }

    override fun onCreate() {
    }

    override fun start() {
        mView.getContext()?.let {
            /*Location permissions*/
            if (ActivityCompat.checkSelfPermission(it, Constants.AppPermissions.FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(it, Constants.AppPermissions.COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(it, Constants.AppPermissions.CONTACTS) != PackageManager.PERMISSION_GRANTED
            ) {
                //mView.hideProgress()
                requestLocationPermissions()
                return
            } else {
                try {
                    //Request Location
                    mLocationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener)
                    //Request Contacts if needed
                    initContactsLoader()
                } catch (ex: SecurityException) {
                    println("No location available: " + ex.message)
                }
            }
        }

    }

    override fun resume() {
        //Google Play Services -> Firebase + Notifications
        CheckGPServices.instance.isGooglePlayServicesAvailable(mView.activity)
    }

    override fun pause() {
    }

    override fun stop() {
        //Memory Management
        clearDisposables() //Contact calls
        mUserManager.clearDisposables()
    }

    override fun showHome() {
    }

    override fun showCamera() {
        mView.activity.let {
            it.startActivityForResult(Intent(it.context, CameraActivity::class.java),
                    Constants.CameraIntents.CAMERA_REQUEST_CODE)
        }
    }

    /**
     * Current user information
     * */
    override fun getUserId(callback: (Int?) -> Unit) {
        UserManager.instance.getCurrentUser { success, user, _ ->
            if (success) {
                user?.id.let {
                    callback(it)
                }
            }
        }
    }

    /**
     *  Update Firebase, Dwalla & other needed tokens to keep the cached user tokens updated
     * */
    override fun updateTokens() {
        //Notifications
        mUserManager.getCurrentUser { success, user, token ->
            if (success) {
                user?.let {
                    //Firebase
                    FirebaseInstanceId.getInstance().token?.let {
                        //Refresh token
                        UserManager.instance.updateFirebaseToken(it)
                    }
                    //TODO: Dwoalla
                }
            }
        }
    }

    /**
     *  Reset Global Notification badge count
     * */
    override fun updateNotificationBadges() {
        NotificationsManager.instance.clearNotificationBadges()
    }

    /**
     *  Global User Settings Refresh
     * */
    override fun readGlobalSettings() {
        SettingsManager.instance.readUserSettings()
    }

    /**
     * [updateLocationForEvents]
     * Triggers location update
     * */
    override fun updateLocationForEvents() {
        try {
            mLocationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener)
        } catch (ex: SecurityException) {
            Toast.makeText(mView.getContext(), "Enable location permissions", Toast.LENGTH_SHORT).show()
        }
    }


    /**
     * [locationListener] handles the current location update
     * once & then stops tracking location once the user leaves
     * the [EventCreateActivity] lifecycle
     * */
    private val locationListener: LocationListener = object : LocationListener {
        /*Current Location*/
        override fun onLocationChanged(location: Location) {

            if (mLocation.first - location.latitude > 0.001 || mLocation.second - location.longitude > 0.001) {
                mLocation = Pair(location.latitude, location.longitude)
                //Update events based on location
                mView.getEventsWithLocation(location)
            }
        }

        /*Not necessary to handle on single location update*/
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}

        /*Not necessary to handle on single location update*/
        override fun onProviderEnabled(provider: String) {}

        /*Not necessary to handle on single location update*/
        override fun onProviderDisabled(provider: String) {}
    }

    /**
     * [requestLocationPermissions]
     * Requests FineLocation & Includes CoarseLocation for battery saving
     * if the first is not available
     * Callback is handled on Activity's[onRequestPermissionsResult]
     * */
    override fun requestLocationPermissions() {
        val whatPermissions = arrayOf(
                Constants.AppPermissions.FINE_LOCATION,
                Constants.AppPermissions.COARSE_LOCATION,
                Constants.AppPermissions.CONTACTS
        )
        mView.getContext()?.let {

            if (ContextCompat.checkSelfPermission(it, whatPermissions[0]) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(it, whatPermissions[1]) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(it, whatPermissions[2]) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(mView.activity, whatPermissions, Constants.PermissionsReqCode.LOCATION_REQ_CODE)
            }
        }
    }

    /**CONTACTS*/
    override fun onCreateLoader(p0: Int, p1: Bundle?): Loader<List<LocalContact>> =
            LocalContactsAsyncLoader(mView.activity.context!!)

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

                Toast.makeText(mView.getContext(), "Scanning your contacts", Toast.LENGTH_SHORT).show()
                hasRepeatedMethodCall = !hasRepeatedMethodCall

            } else if (mLocalContacts.count() > 0 && hasRepeatedMethodCall) {
                hasRepeatedMethodCall = !hasRepeatedMethodCall
                getEightContacts(mLocalContacts)
            }
        }
    }

    override fun onLoaderReset(p0: Loader<List<LocalContact>>?) {
        mLocalContacts.clear()
    }

    override fun initContactsLoader() {
        if (Contact().queryAll().isEmpty()) {
            mView.activity.loaderManager.initLoader(0, null, this).forceLoad()
        }
    }

    /**
     * [getEightContacts]
     * Retrieves the contacts that are related with Eight through the API
     * */
    private fun getEightContacts(localContacts: ArrayList<LocalContact>) {
        mUserManager.getCurrentUser { success, user, token ->
            if (success) {
                token?.token?.let {
                    user?.id?.let {
                        val contactsPostData = ContactsPostData(localContacts.toArray())
                        val call = Rest.getInstance().getmCallerRx().getEightContactsPhoneNumbers(
                                token.token!!,
                                user.id!!,
                                contactsPostData.contactsArray
                        )
                        disposables.add(call.subscribeOn(Schedulers.io())
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

                                            } else if (response.isError) { //Error
                                                mView.activity.context?.let {
                                                    Toast.makeText(it, "didn't find local contacts", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        },
                                        //On error implementation
                                        { throwable ->
                                            println("Error downloading contacts: " + throwable.message)
                                        })
                        )
                    }
                }
            }
        }

    }

    /**
     * [getEightFriends]
     * Get & save matching contacts from eight application
     * */
    private fun getEightFriends() {
        mUserManager.getCurrentUser { success, user, token ->
            if (success) {
                token?.token?.let {
                    user?.id?.let {
                        val call = Rest.getInstance().getmCallerRx().getUserFriends(token.token!!, user.id!!)
                        disposables.add(call.subscribeOn(Schedulers.io())
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
                                                }

                                            } else if (response.isError) { //Error
                                                mView.activity.context?.let {
                                                    Toast.makeText(it, "didn't find contacts", Toast.LENGTH_SHORT).show()
                                                }

                                            }

                                        },

                                        //On error implementation
                                        { throwable ->
                                            println("Error downloading friends: " + throwable.message)
                                        })
                        )
                    }
                }
            }
        }
    }
}