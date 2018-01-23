package com.phdlabs.sungwon.a8chat_android.structure.event.create

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.support.v4.app.ActivityCompat
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.phdlabs.sungwon.a8chat_android.api.data.EventPostData
import com.phdlabs.sungwon.a8chat_android.api.event.Event
import com.phdlabs.sungwon.a8chat_android.api.response.EventPostResponse
import com.phdlabs.sungwon.a8chat_android.api.rest.Caller
import com.phdlabs.sungwon.a8chat_android.api.rest.Rest
import com.phdlabs.sungwon.a8chat_android.api.utility.Callback8
import com.phdlabs.sungwon.a8chat_android.db.EventBusManager
import com.phdlabs.sungwon.a8chat_android.model.user.registration.Token
import com.phdlabs.sungwon.a8chat_android.structure.event.EventContract
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import com.phdlabs.sungwon.a8chat_android.utility.Preferences
import com.phdlabs.sungwon.a8chat_android.utility.camera.CameraControl
import com.vicpin.krealmextensions.queryFirst
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.greenrobot.eventbus.EventBus
import java.io.ByteArrayOutputStream

/**
 * Created by SungWon on 1/2/2018.
 */
class EventCreateController(val mView: EventContract.Create.View) : EventContract.Create.Controller{

    private var mCaller: Caller
    private var mEventBus: EventBus
    private var mMediaId: Int = 2
    private var mFusedLocationClient: FusedLocationProviderClient

    private var mLat = ""
    private var mLng = ""

    init {
        mView.controller = this
        mCaller = Rest.getInstance().caller
        mEventBus = EventBusManager.instance().mDataEventBus
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mView.getContext()!!)
    }
    override fun start() {
        mView.showProgress()
        if (ActivityCompat.checkSelfPermission(mView.getContext()!!, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mView.getContext()!!, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            mView.hideProgress()
            Toast.makeText(mView.getContext()!!, "Please enable location permission to send your location (optional)", Toast.LENGTH_SHORT).show()
            return
        }
        mFusedLocationClient.lastLocation.addOnSuccessListener({ location ->
            if(location != null){
                mLat = location.latitude.toString()
                mLng = location.longitude.toString()
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

    override fun createEvent(name: String, lock: Boolean, location: String) {
        val call = mCaller.postEvents(Preferences(mView.getContext()!!).getPreferenceString(Constants.PrefKeys.TOKEN_KEY),
                EventPostData(
                        mMediaId.toString(),
                        location,
                        Preferences(mView.getContext()!!).getPreferenceInt(Constants.PrefKeys.USER_ID).toString(),
                        name,
                        lock
                ))
        call.enqueue(object: Callback8<EventPostResponse, Event>(mEventBus){
            override fun onSuccess(data: EventPostResponse?) {
                data?.let {
                    mView.onCreateEvent(it)
                }
            }
        })
    }

    override fun onPictureResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_CANCELED) {
            mView.showProgress()
            //Set image in UI
            val imageUrl = CameraControl.instance.getImagePathFromResult(mView.getContext()!!, requestCode, resultCode, data)
            //Upload Image
            val imageBitmap = CameraControl.instance.getImageFromResult(mView.getContext()!!, requestCode, resultCode, data)
            imageBitmap.let {
                val bos = ByteArrayOutputStream()
                it!!.compress(Bitmap.CompressFormat.PNG, 0, bos)
                val bitmapData = bos.toByteArray()
                val formBody = MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("file", "8chat" + System.currentTimeMillis(), RequestBody.create(MediaType.parse("image/png"), bitmapData))
                        .build()
                Token().queryFirst()?.let {
                    val call = Rest.getInstance().getmCallerRx().userPostPic(it.token!!, formBody)
                    call.subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe { response ->
                                mView.hideProgress()
                                if (response.isSuccess) {

                                    response.media?.let {
                                        for (media in it){
                                            print("*********************************************")
                                            print("MEDIA: " + media.id)
                                            print("MEDIA: " + media.user_id)
                                            print("MEDIA: " + media.media_file)
                                            print("**********************************************")
                                            mMediaId = media.id!!
                                            mView.showPicture(media.media_file!!)
                                        }
                                    }

                                    //TODO: Media is not being mapped

                                    //TODO: Cache media object

                                    //TODO: Update local user with media ID before patching

                                    /*Cache media info in realm*/
                                    //response.media?.save()
                                    /*Update Realm user with profile picture url*/
                                    //val updatedUser = currentUser
                                    //updatedUser.mediaId = response.media?.id?.toString()
                                    //print(updatedUser.mediaId)
                                    //updatedUser.save()
                                    //Toast.makeText(mView.getContext(), "Profile Picture Updated", Toast.LENGTH_SHORT).show()


                                } else if (response.isError) {
                                    /*Error*/
                                    mView.showError(response.message)
                                }
                            }
                }

            }
            mView.hideProgress()
        }
    }
}