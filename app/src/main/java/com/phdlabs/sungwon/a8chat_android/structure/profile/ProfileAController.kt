package com.phdlabs.sungwon.a8chat_android.structure.profile

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.widget.Toast
import com.phdlabs.sungwon.a8chat_android.api.event.MediaEvent
import com.phdlabs.sungwon.a8chat_android.api.event.UserPatchEvent
import com.phdlabs.sungwon.a8chat_android.api.response.MediaResponse
import com.phdlabs.sungwon.a8chat_android.api.response.UserDataResponse
import com.phdlabs.sungwon.a8chat_android.api.rest.Rest
import com.phdlabs.sungwon.a8chat_android.api.utility.Callback8
import com.phdlabs.sungwon.a8chat_android.db.EventBusManager
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import com.phdlabs.sungwon.a8chat_android.utility.*
import com.phdlabs.sungwon.a8chat_android.utility.camera.CameraControl
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import java.io.ByteArrayOutputStream


/**
 * Created by SungWon on 10/2/2017.
 */
class ProfileAController(val mView: ProfileContract.View) : ProfileContract.Controller {

    //connects to ProfileActivity

    init {
        mView.controller = this
    }

    override fun start() {
    }

    override fun resume() {
    }

    override fun pause() {
    }

    override fun stop() {
    }

    override fun showPicture(activity: CoreActivity) {
        CameraControl.instance.pickImage(mView.getActivity,
                "Choose a profile picture",
                CameraControl.instance.requestCode(),
                false)
    }

    override fun postProfile() {
        if (mView.nullChecker()) {
            Toast.makeText(mView.getContext(), "Please enter a first and last name", Toast.LENGTH_SHORT).show()
        }
        mView.showProgress()
        val pref = Preferences(mView.getContext()!!)
//        val v = pref.getPreferenceString(Constants.PrefKeys.TOKEN_KEY)
//        val w = UserManager.instance().user!!.id
//        val x = mView.getUserData
        val call = Rest.getInstance().caller.updateUser(pref.getPreferenceString(Constants.PrefKeys.TOKEN_KEY), pref.getPreferenceInt(Constants.PrefKeys.USER_ID), mView.getUserData)
        call.enqueue(object : Callback8<UserDataResponse, UserPatchEvent>(EventBusManager.instance().mDataEventBus) {
            override fun onSuccess(data: UserDataResponse?) {
                mView.hideProgress()
                EventBusManager.instance().mDataEventBus.post(UserPatchEvent())
                Toast.makeText(mView.getContext(), "Profile Updated", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<UserDataResponse>?, response: Response<UserDataResponse>?) {
                mView.hideProgress()
                super.onResponse(call, response)
            }

            override fun onError(response: Response<UserDataResponse>?) {
                mView.hideProgress()
                super.onError(response)
            }
        })
    }

    override fun onPictureResult(requestCode: Int, resultCode: Int, data: Intent?) {

        //Change image if available
        if (resultCode != Activity.RESULT_CANCELED) {
            mView.showProgress()
            //Set image in UI
            val imageUrl = CameraControl.instance.getImagePathFromResult(mView.getActivity, requestCode, resultCode, data)
            imageUrl.let {
                mView.setProfileImageView(it!!)
            }
            //Upload Image
            val imageBitmap = CameraControl.instance.getImageFromResult(mView.getActivity, requestCode, resultCode, data)
            imageBitmap.let {
                val bos = ByteArrayOutputStream()
                it!!.compress(Bitmap.CompressFormat.PNG, 0, bos)
                val bitmapData = bos.toByteArray()
                val pref = Preferences(mView.getContext()!!)
                val formBody = MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("user_id", pref.getPreferenceInt(Constants.PrefKeys.USER_ID, -1).toString())
                        .addFormDataPart("file", "8chat" + System.currentTimeMillis(), RequestBody.create(MediaType.parse("image/png"), bitmapData))
                        .build()
                val call = Rest.getInstance().caller.userPicPost(pref.getPreferenceString(Constants.PrefKeys.TOKEN_KEY), formBody)
                call.enqueue(object : Callback8<MediaResponse, MediaEvent>(EventBusManager.instance().mDataEventBus) {
                    override fun onSuccess(data: MediaResponse?) {
                        mView.hideProgress()
                        Toast.makeText(mView.getContext(), "Profile Picture Updated", Toast.LENGTH_SHORT).show()
                    }
                })

            }
            mView.hideProgress()
        }
    }

}