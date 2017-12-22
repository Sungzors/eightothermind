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
import com.phdlabs.sungwon.a8chat_android.model.user.User
import com.phdlabs.sungwon.a8chat_android.model.user.registration.Token
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import com.phdlabs.sungwon.a8chat_android.utility.*
import com.phdlabs.sungwon.a8chat_android.utility.camera.CameraControl
import com.vicpin.krealmextensions.query
import com.vicpin.krealmextensions.queryFirst
import com.vicpin.krealmextensions.save
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import java.io.ByteArrayOutputStream


/**
 * Created by SungWon on 10/2/2017.
 * Updated by JPAM on 12/21/2017
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
        /*Data validation*/
        if (mView.nullChecker()) {
            Toast.makeText(mView.getContext(), "Please enter a first and last name", Toast.LENGTH_SHORT).show()
            return
        }
        if (User().queryFirst()?.mediaId == null) {
            mView.showError("Don't forget your profile picture")
            return
        }
        mView.showProgress()
        val currentUser = User().queryFirst()
        val token = Token().queryFirst()
        token?.token?.let {
            currentUser?.id?.let {
                val call = Rest.getInstance().getmCallerRx().updateUser(token.token!!, it, mView.getUserData)
                call.subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { response ->
                            mView.hideProgress()
                            if (response.isSuccess) {
                                /*Update user in Realm*/
                                response.user.save()
                                Toast.makeText(mView.getContext(), "Profile updated", Toast.LENGTH_SHORT).show()
                            } else if (response.isError) {
                                Toast.makeText(mView.getContext(), "Unable to update", Toast.LENGTH_SHORT).show()
                            }
                        }

            }
        }
    }

    override fun onPictureResult(requestCode: Int, resultCode: Int, data: Intent?) {

        //Change image if available
        if (resultCode != Activity.RESULT_CANCELED) {
            mView.showProgress()
            //Set image in UI
            val imageUrl = CameraControl.instance.getImagePathFromResult(mView.getActivity, requestCode, resultCode, data)
            imageUrl?.let {
                mView.setProfileImageView(it)
            }
            //Upload Image
            val imageBitmap = CameraControl.instance.getImageFromResult(mView.getActivity, requestCode, resultCode, data)
            imageBitmap.let {
                val bos = ByteArrayOutputStream()
                it!!.compress(Bitmap.CompressFormat.PNG, 0, bos)
                val bitmapData = bos.toByteArray()
                val currentUser = User().queryFirst()
                currentUser?.let {
                    val formBody = MultipartBody.Builder().setType(MultipartBody.FORM)
                            .addFormDataPart("user_id", it.id.toString())
                            .addFormDataPart("file", "8chat" + System.currentTimeMillis(), RequestBody.create(MediaType.parse("image/png"), bitmapData))
                            .build()
                    Token().queryFirst()?.let {
                        val call = Rest.getInstance().getmCallerRx().userPostPic(it.token!!, formBody)
                        call.subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe { response ->
                                    mView.hideProgress()
                                    if (response.isSuccess) {

                                        //TODO: Media is not being mapped

                                        //TODO: Cache media object

                                        //TODO: Update local user with media ID before patching

                                        /*Cache media info in realm*/
                                        response.media?.save()
                                        /*Update Realm user with profile picture url*/
                                        val updatedUser = currentUser
                                        updatedUser.mediaId = response.media?.id?.toString()
                                        print(updatedUser.mediaId)
                                        updatedUser.save()
                                        Toast.makeText(mView.getContext(), "Profile Picture Updated", Toast.LENGTH_SHORT).show()


                                    } else if (response.isError) {
                                        /*Error*/
                                        mView.showError(response.message)
                                    }
                                }
                    }
                }
            }
            mView.hideProgress()
        }
    }

}