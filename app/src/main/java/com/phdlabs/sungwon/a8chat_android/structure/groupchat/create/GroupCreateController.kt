package com.phdlabs.sungwon.a8chat_android.structure.groupchat.create

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.widget.Toast
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.api.data.GroupChatPostData
import com.phdlabs.sungwon.a8chat_android.api.rest.Rest
import com.phdlabs.sungwon.a8chat_android.db.user.UserManager
import com.phdlabs.sungwon.a8chat_android.model.user.User
import com.phdlabs.sungwon.a8chat_android.model.user.registration.Token
import com.phdlabs.sungwon.a8chat_android.structure.groupchat.GroupChatContract
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
 * Created by SungWon on 3/1/2018.
 */
class GroupCreateController(val mView: GroupChatContract.Create.View) : GroupChatContract.Create.Controller {

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

    override fun onPictureResult(requestCode: Int, resultCode: Int, data: Intent?) {
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
                                mView.showError("Could not update channel picture, try again later")
                                println("Error uploading channel picture: " + throwable.message)
                            })
                } ?: run {
                    /*User not available
                    * This should only hit on DebugMode
                    * */
                    mView.hideProgress()
                    Toast.makeText(mView.getContext(), "Please sign in to continue", Toast.LENGTH_SHORT).show()
                }

                //Set image in UI
                mView.setChannelImage(it)
            }
        }
    }

    /**
     * [requestCameraPermissions]
     * requests camera permissions so the camera option can be
     * added to the intent
     * Callback handled in Activitie's [onRequestPermissionsResult]
     * */
    private fun requestCameraPermissions() {
        val whatPermissions = arrayOf(Constants.AppPermissions.CAMERA)
        mView.getContext()?.let {
            if (ContextCompat.checkSelfPermission(it, whatPermissions.get(0)) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(mView.getActivity, whatPermissions, Constants.PermissionsReqCode.CAMERA_REQ_CODE)
            }
        }
    }

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
                "Choose a channel picture",
                CameraControl.instance.requestCode(),
                false)
    }

    override fun createGroupChat(data: GroupChatPostData) {
        val info: Triple<Token?, GroupChatPostData?, User?> = groupDataValidation(data)
        if(info.first?.token != null && info.second != null && info.third != null){
            mView.showProgress()

            val call = Rest.getInstance().getmCallerRx().createGroupChat(info.first?.token!!, info.second!!)
            call.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            {response ->
                                if (response.isSuccess) {
                                    response.room?.let {
                                        it.user = info.third
                                        it.save()
                                    }
                                    response.newChannelGroupOrEvent?.save()

                                    mView.hideProgress()
                                    mView.onCreateGroup(,, )
                                }
                            }
                    )
        }
    }

    private fun groupDataValidation(data: GroupChatPostData): Triple<Token?, GroupChatPostData?, User?>{

        var mToken: Token? = null
        var currentUser: User? = null

        if (data.name.isNullOrBlank() || data.adminId == null ||  data.userIds.isEmpty()){
            Toast.makeText(mView.getContext(), mView.getContext()?.getString(R.string.incomplete_information), Toast.LENGTH_SHORT).show()
            return Triple(null, null, null)
        } else if (data.mediaId == null) {
            Toast.makeText(mView.getContext(), mView.getContext()?.getString(R.string.add_channel_photo), Toast.LENGTH_SHORT).show()
            return Triple(null, null, null)
        }

        UserManager.instance.getCurrentUser { success, user, token ->
            if(success) {
                mToken = token
                currentUser = user
            }
        }

        return Triple(mToken, data, currentUser)
    }
}