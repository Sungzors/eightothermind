package com.phdlabs.sungwon.a8chat_android.structure.main

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.widget.Toast
import com.github.nkzawa.socketio.client.Socket
import com.google.firebase.iid.FirebaseInstanceId
import com.phdlabs.sungwon.a8chat_android.db.notifications.NotificationsManager
import com.phdlabs.sungwon.a8chat_android.db.room.RoomManager
import com.phdlabs.sungwon.a8chat_android.db.user.SettingsManager
import com.phdlabs.sungwon.a8chat_android.db.user.UserManager
import com.phdlabs.sungwon.a8chat_android.services.googlePlay.CheckGPServices
import com.phdlabs.sungwon.a8chat_android.structure.camera.CameraActivity
import com.phdlabs.sungwon.a8chat_android.utility.Constants

/**
 * Created by SungWon on 10/13/2017.
 * Updated by JPAM on 02/12/2018
 */
class MainAController(val mView: MainContract.View) : MainContract.Controller {

    /*Properties*/
    private var mLocationManager: LocationManager? = null
    private var mLocation: Pair<Double, Double> = Pair(0.0, 0.0)


    init {
        mView.controller = this
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
        UserManager.instance.getCurrentUser { success, user, token ->
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
}