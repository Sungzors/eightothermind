package com.phdlabs.sungwon.a8chat_android.structure.event.create

import android.app.Activity
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityCompat.requestPermissions
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.widget.Toast
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.api.data.EventPostData
import com.phdlabs.sungwon.a8chat_android.api.rest.Caller
import com.phdlabs.sungwon.a8chat_android.api.rest.Rest
import com.phdlabs.sungwon.a8chat_android.db.user.UserManager
import com.phdlabs.sungwon.a8chat_android.model.room.Room
import com.phdlabs.sungwon.a8chat_android.model.user.User
import com.phdlabs.sungwon.a8chat_android.model.user.registration.Token
import com.phdlabs.sungwon.a8chat_android.structure.event.EventContract
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import com.phdlabs.sungwon.a8chat_android.utility.camera.CameraControl
import com.vicpin.krealmextensions.queryFirst
import com.vicpin.krealmextensions.save
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

/**
 * Created by SungWon on 1/2/2018.
 * Updated by JPAM on 1/29/2018
 */
class EventCreateController(val mView: EventContract.Create.View) : EventContract.Create.Controller {

    /*Properties*/
    private var mCaller: Caller
    private var mLocation: Pair<String?, String?>? = null
    private var mLocationManager: LocationManager? = null

    /*Initialize*/
    init {
        mView.controller = this
        mCaller = Rest.getInstance().caller
        mLocationManager = mView.getContext()?.getSystemService(LOCATION_SERVICE) as LocationManager?
    }

    /*LifeCycle*/
    override fun start() {
        mView.showProgress()
        /*Permissions*/
        mView.getContext()?.let {
            /*Location permissions*/
            if (ActivityCompat.checkSelfPermission(
                    it, Constants.AppPermissions.FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(
                            it, Constants.AppPermissions.COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    ) {
                mView.hideProgress()
                requestLocationPermissions()
                return
            }
        }
        /*User Current Location*/
        try {
            mLocationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener)
        } catch (ex: SecurityException) {
            println("No location available: " + ex.message)
        }
        mView.hideProgress()
    }

    override fun resume() {
    }

    override fun pause() {
    }

    override fun stop() {
    }

    /**
     * [eventDataValidation] used for data form error handling
     * on [EventCreateActivity]
     * @return [Token] [EventPostData] [User] for @Posting new Event
     * */
    private fun eventDataValidation(eventPostData: EventPostData): Triple<Token?, EventPostData?, User?> {

        //Data to be returned
        var mToken: Token? = null
        val mEventPostData: EventPostData?
        var currentUser: User? = null

        /*Info Validation*/
        if (eventPostData.name.isNullOrBlank() ||
                eventPostData.location_name.isNullOrBlank()) {

            Toast.makeText(mView.getContext(), mView.getContext()?.getString(R.string.incomplete_information), Toast.LENGTH_SHORT).show()
            return Triple(null, null, null)

        } else if (eventPostData.mediaId.isNullOrBlank() || eventPostData.mediaId == "null") {

            /*Media Validation*/
            Toast.makeText(mView.getContext(), mView.getContext()?.getString(R.string.add_event_photo), Toast.LENGTH_SHORT).show()
            return Triple(null, null, null)

        } else if (eventPostData.lat.isNullOrBlank() && eventPostData.lng.isNullOrBlank()) {

            /*Location Validation*/
            val goToPermissionsAlert = AlertDialog.Builder(mView.getActivity).create()
            goToPermissionsAlert.setTitle("Events")
            goToPermissionsAlert.setMessage("Creating or viewing an event requires location permissions")
            goToPermissionsAlert.setButton(AlertDialog.BUTTON_POSITIVE, "OK", { _, _ ->
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package", mView.getActivity.packageName, null)
                intent.data = uri
                mView.getActivity.startActivity(intent)
            })
            goToPermissionsAlert.show()
            return Triple(null, null, null)
        }
        /*Location*/
        mEventPostData = eventPostData

        //Finish building data to create Event
        UserManager.instance.getCurrentUser { success, user, token ->
            if (success) {
                //Available data
                mToken = token
                currentUser = user
                mEventPostData.user_creator_id = user?.id
            }
        }

        return Triple(mToken, mEventPostData, currentUser)
    }

    /*Create Event*/
    override fun createEvent(eventPostData: EventPostData) {
        //Data validation
        val info: Triple<Token?, EventPostData?, User?> = eventDataValidation(eventPostData)
        if (info.first?.token != null && info.second != null && info.third != null) {

            mView.showProgress()

            /*Upload New Event Info -> media is available*/
            val call = Rest.getInstance().getmCallerRx().postEvents(info.first?.token!!, info.second!!)
            call.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { response ->

                                if (response.isSuccess) {

                                    /**
                                     * Save new [Room] to
                                     * @see Realm
                                     * */
                                    response.room?.let {
                                        it.user = info.third
                                        it.save()
                                    }

                                    /**
                                     * save new [EventsEight] to
                                     * @see Realm
                                     * */
                                    response.newChannelGroupOrEvent?.save()

                                    /*Transition*/
                                    mView.hideProgress()
                                    mView.onCreateEvent(response.newChannelGroupOrEvent)

                                } else if (response.isError) {
                                    mView.hideProgress()
                                    Toast.makeText(mView.getContext(),
                                            mView.getContext()?.getString(R.string.add_channel_photo),
                                            Toast.LENGTH_SHORT).show()
                                }

                            },
                            //Error
                            { throwable ->

                                mView.hideProgress()
                                mView.showError("Unable to create an event, try again later")
                                println("Error creating event: " + throwable.message)
                            }
                    )
        }
    }

    /**
     * [showPicture] defined in Controller interface
     * handles image picker intent using [CameraControl]
     * */
    override fun showPicture() {
        mView.getContext()?.let {
            if (ActivityCompat.checkSelfPermission(
                    it, Constants.AppPermissions.CAMERA) != PackageManager.PERMISSION_GRANTED
                    ) {
                requestCameraPermissions()
                return
            }
        }
        CameraControl.instance.pickImage(mView.getActivity,
                "Choose an event picture",
                CameraControl.instance.requestCode(),
                false)
    }

    /**
     * [onPictureResult] defined in Controller interface
     * Handles media upload
     * @param requestCode of the current onActivityResult
     * @param resultCode of the current onActivityResult
     * @param data from the onActivityResult [Intent]
     * */
    override fun onPictureResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //Change image if available
        if (resultCode != Activity.RESULT_CANCELED) {
            mView.showProgress()

            //Set image in UI
            val imageUrl = CameraControl.instance.getImagePathFromResult(mView.getActivity, requestCode, resultCode, data)
            imageUrl?.let {

                //Prepare image for uploading
                val file = File(it)
                val multipartBodyPart = MultipartBody.Part.createFormData(
                        "file",
                        file.name,
                        RequestBody.create(MediaType.parse("image/*"), file)
                )

                //Upload image
                Token().queryFirst()?.let {
                    val call = Rest.getInstance().getmCallerRx().uploadMedia(it.token!!, multipartBodyPart)
                    call.subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({ response ->

                                if (response.isSuccess) {
                                    response.mediaArray?.let {
                                        //Save temporary media
                                        mView.setMedia(it[0])
                                        mView.hideProgress()
                                    }

                                } else if (response.isError) {
                                    mView.showError("Couldn't upload picture, try again later")
                                    mView.hideProgress()
                                }

                            }, { throwable ->
                                mView.hideProgress()
                                mView.showError("Could not update event picture, try again later")
                                println("Error uploading event picture: " + throwable.message)
                            })
                } ?: run {
                    /*User not available
                    * This should only hit on DebugMode
                    * */
                    mView.hideProgress()
                    Toast.makeText(mView.getContext(), "Please sign in to continue", Toast.LENGTH_SHORT).show()
                }

                //Set image in UI
                mView.setEventImage(it)
            }
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
            mView.getLocation(Pair(
                    location.longitude.toString().trim(),
                    location.latitude.toString().trim())
            )
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
    private fun requestLocationPermissions() {
        val whatPermissions = arrayOf(Constants.AppPermissions.FINE_LOCATION,
                Constants.AppPermissions.COARSE_LOCATION)
        mView.getContext()?.let {
            //Request Permissions
            if (ContextCompat.checkSelfPermission(it, whatPermissions.get(0)) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(it, whatPermissions.get(1)) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(mView.getActivity, whatPermissions, Constants.PermissionsReqCode.LOCATION_REQ_CODE)
            }
        }
    }

    /**
     * [requestCameraPermisions]
     * Requests Camera permissions
     * Callback is handled on the Activity's [onRequestPermissionsResult]
     * */
    private fun requestCameraPermissions() {
        val whatPermissions = arrayOf(Constants.AppPermissions.CAMERA)
        mView.getContext()?.let {
            if (ContextCompat.checkSelfPermission(it, whatPermissions.get(0)) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(mView.getActivity, whatPermissions, Constants.PermissionsReqCode.CAMERA_REQ_CODE)
            }
        }
    }
}