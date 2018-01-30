package com.phdlabs.sungwon.a8chat_android.structure.event.create

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityCompat.requestPermissions
import android.support.v4.content.ContextCompat
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.phdlabs.sungwon.a8chat_android.api.data.EventPostData
import com.phdlabs.sungwon.a8chat_android.api.rest.Caller
import com.phdlabs.sungwon.a8chat_android.api.rest.Rest
import com.phdlabs.sungwon.a8chat_android.model.user.User
import com.phdlabs.sungwon.a8chat_android.model.user.registration.Token
import com.phdlabs.sungwon.a8chat_android.structure.event.EventContract
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import com.phdlabs.sungwon.a8chat_android.utility.camera.CameraControl
import com.vicpin.krealmextensions.queryFirst
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
    private var mFusedLocationClient: FusedLocationProviderClient? = null

    //TODO: Transpose to custom Location Object -> Position
    private var mLat = ""
    private var mLng = ""

    /*Initialize*/
    init {
        mView.controller = this
        mCaller = Rest.getInstance().caller
        mView.getContext()?.let {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(it)
        }
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
        mFusedLocationClient?.lastLocation?.addOnSuccessListener({ location ->
            if (location != null) {
                //TODO: Transpose to custom location object
                mLat = location.latitude.toString()
                mLng = location.longitude.toString()
                //TODO: Update current user position
            }
        })
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
        //TODO: Event Data validation
        return Triple(null, null, null)
    }

    /*Create Event*/
    override fun createEvent(eventPostData: EventPostData) {
        //Data validation
        val info: Triple<Token?, EventPostData?, User?> = eventDataValidation(eventPostData)
        if (info.first?.token != null && info.second != null && info.third != null) {

            //TODO: Create Event & save to Realm with successful callback


        }

//        val call = mCaller.postEvents(Preferences(mView.getContext()!!).getPreferenceString(Constants.PrefKeys.TOKEN_KEY),
//                EventPostData(
//                        mMediaId.toString(),
//                        location,
//                        Preferences(mView.getContext()!!).getPreferenceInt(Constants.PrefKeys.USER_ID).toString(),
//                        name,
//                        lock
//                ))
//        call.enqueue(object : Callback8<EventPostResponse, Event>(mEventBus) {
//            override fun onSuccess(data: EventPostResponse?) {
//                data?.let {
//                    mView.onCreateEvent(it)
//                }
//            }
//        })

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
                                        mView.getMedia(it[0])
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


    override fun getCurrentLocation() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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