package com.phdlabs.sungwon.a8chat_android.structure.profile

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
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
import com.squareup.picasso.Picasso
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import java.io.ByteArrayOutputStream


/**
 * Created by SungWon on 10/2/2017.
 */
class ProfileAController(val mView: ProfileContract.View): ProfileContract.Controller{

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
        CameraControl.instance.startImagePicker(mView.getActivity)
    }

    override fun postProfile() {
        if(mView.nullChecker()){
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
        CameraControl.Companion.instance.getImageAsync(mView.getActivity, requestCode, resultCode, data,
                Procedure { result ->
                    if (result.getFile() != null){
                        circlePicture(result!!.getFile()!!.absolutePath)
                    }
                },
                Procedure { result ->
                    mView.showProgress()
                    if (result.getFile() != null) {
                        val bos = ByteArrayOutputStream()
                        result!!.getBitmap()!!.compress(Bitmap.CompressFormat.PNG, 0, bos)
                        val bitmapdata = bos.toByteArray()
//                        val bs = ByteArrayInputStream(bitmapdata)
//                        val buff = ByteArray(8000)
//                        var bytesRead = 0
//                        val bos2 = ByteArrayOutputStream()
//                        try {
//                            while (bs.read(buff) != -1){
//
//                                bos2.write(buff, 0, bytesRead)
//                            }
//                        } catch(e: IOException){
//                            e.printStackTrace()
//                        }
                        val pref = Preferences(mView.getContext()!!)
                        val formBody = MultipartBody.Builder().setType(MultipartBody.FORM)
                                .addFormDataPart("user_id", pref.getPreferenceInt(Constants.PrefKeys.USER_ID, -1).toString())
                                .addFormDataPart("file", "8chat" + System.currentTimeMillis(), RequestBody.create(MediaType.parse("image/png"), bitmapdata))
                                .build()
                        val call = Rest.getInstance().caller.userPicPost(pref.getPreferenceString(Constants.PrefKeys.TOKEN_KEY), formBody)
                        call.enqueue(object : Callback8<MediaResponse, MediaEvent>(EventBusManager.instance().mDataEventBus) {
                            override fun onSuccess(data: MediaResponse?) {
                                mView.hideProgress()
                                Toast.makeText(mView.getContext(), "Profile Picture Updated", Toast.LENGTH_SHORT).show()
                            }
                        })
                    }
                })
    }

    override fun circlePicture(pictureUrl: String) {
        Picasso.with(mView.getContext()).load(pictureUrl).transform(CircleTransform()).into(mView.getProfileImageView)
    }

    override fun circlePicture(pictureUrl: Uri) {
        Picasso.with(mView.getContext()).load(pictureUrl).transform(CircleTransform()).into(mView.getProfileImageView)
    }
}