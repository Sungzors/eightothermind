package com.phdlabs.sungwon.a8chat_android.structure.profile

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import com.phdlabs.sungwon.a8chat_android.api.rest.Rest
import com.phdlabs.sungwon.a8chat_android.model.user.User
import com.phdlabs.sungwon.a8chat_android.model.user.registration.Token
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
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
 * Created by SungWon on 10/2/2017.
 * Updated by JPAM on 12/21/2017
 */
class ProfileAController(val mView: ProfileContract.View) : ProfileContract.Controller {

    /*Initialization*/
    init {
        mView.controller = this
    }

    /*LifeCycle*/
    override fun start() {
    }

    override fun resume() {
    }

    override fun pause() {
    }

    override fun stop() {
    }

    /*Camera Intent*/
    override fun showPicture(activity: CoreActivity) {
        CameraControl.instance.pickImage(mView.getActivity,
                "Choose a profile picture",
                CameraControl.instance.requestCode(),
                false)
    }

    /**@Patch user profile
     * Updates Realm
     * [postProfile]
     * */
    override fun postProfile() {
        /*Data validation*/
        if (mView.nullChecker()) {
            Toast.makeText(mView.getContext(), "Please enter a first and last name", Toast.LENGTH_SHORT).show()
            return
        }
        //TODO: Update with realm caching
        if (User().queryFirst()?.mediaId == null) {
            mView.showError("Don't forget your profile picture")
            return
        }

        mView.showProgress()

        val currentUser = User().queryFirst()
        val token = Token().queryFirst()

        token?.token?.let {
            currentUser?.id?.let {

                val userData = mView.getUserData
                userData.mediaId = currentUser.mediaId!!

                val call = Rest.getInstance().getmCallerRx().updateUser(token.token!!, it, userData)
                call.subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { response ->
                            mView.hideProgress()
                            if (response.isSuccess) {
                                /*Update user in Realm*/
                                response.user.save()
                                mView.startApp()
                                Toast.makeText(mView.getContext(), "Profile updated", Toast.LENGTH_SHORT).show()
                            } else if (response.isError) {
                                Toast.makeText(mView.getContext(), "Unable to update", Toast.LENGTH_SHORT).show()
                            }
                        }
            }
        }
    }

    /**@Post Media
     * Profile picture
     * [onPictureResult] handled in onActivityResult in ProfileActivity*/
    override fun onPictureResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //Change image if available
        if (resultCode != Activity.RESULT_CANCELED) {
            mView.showProgress()
            //Set image in UI
            val imageUrl = CameraControl.instance.getImagePathFromResult(mView.getActivity, requestCode, resultCode, data)
            imageUrl?.let {
                //Set image in UI
                mView.setProfileImageView(it)
                /*Upload image*/
                val file = File(it)
                val multipartBodyPart = MultipartBody.Part.createFormData(
                        "file",
                        file.name,
                        RequestBody.create(MediaType.parse("image/*"), file)
                )
                //Rx call
                Token().queryFirst()?.let {
                    val call = Rest.getInstance().getmCallerRx().uploadMedia(it.token!!, multipartBodyPart)
                    call.subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe { response ->
                                if (response.isSuccess) {
                                    response.mediaArray?.let {
                                        /*Cache media info in realm*/
                                        response.mediaArray?.get(0)?.save()
                                        /*Update Realm user with profile picture url*/
                                        User().queryFirst()?.let {
                                            it.mediaId = response.mediaArray?.get(0)?.id.toString()
                                            it.save()
                                        }
                                    }
                                }
                            }
                }
            }
            mView.hideProgress()
        }
    }
}