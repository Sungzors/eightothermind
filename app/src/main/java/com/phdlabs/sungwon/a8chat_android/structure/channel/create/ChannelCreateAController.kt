package com.phdlabs.sungwon.a8chat_android.structure.channel.create

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.widget.Toast
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.api.data.ChannelPostData
import com.phdlabs.sungwon.a8chat_android.api.rest.Caller
import com.phdlabs.sungwon.a8chat_android.api.rest.Rest
import com.phdlabs.sungwon.a8chat_android.db.UserManager
import com.phdlabs.sungwon.a8chat_android.model.channel.Channel
import com.phdlabs.sungwon.a8chat_android.model.room.Room
import com.phdlabs.sungwon.a8chat_android.model.user.User
import com.phdlabs.sungwon.a8chat_android.model.user.registration.Token
import com.phdlabs.sungwon.a8chat_android.structure.channel.ChannelContract
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
 * Created by SungWon on 11/30/2017.
 * Updated by jpam on 01/25/2018
 */
class ChannelCreateAController(val mView: ChannelContract.Create.View) : ChannelContract.Create.Controller {

    /*Properties*/
    private var mCaller: Caller

    /*Initialization*/
    init {
        mView.controller = this
        mCaller = Rest.getInstance().caller
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

    /**
     * [channelDataValidation] used for data form error handling
     * on [ChannelCreateActivity]
     * @return [Token] [ChannelPostData] [User] for @Posting new channel
     * */
    private fun channelDataValidation(channelPostData: ChannelPostData): Triple<Token?, ChannelPostData?, User?> {


        //Data to be returned
        var mToken: Token? = null
        val mChannelPostData: ChannelPostData?
        var currentUser: User? = null

        /*Info Validation*/
        if (channelPostData.name.isNullOrBlank() ||
                channelPostData.unique_id.isNullOrBlank() ||
                channelPostData.description.isNullOrBlank()) {

            Toast.makeText(mView.getContext(), mView.getContext()?.getString(R.string.incomplete_information), Toast.LENGTH_SHORT).show()
            return Triple(null, null, null)

        } else if (channelPostData.mediaId.isNullOrBlank() || channelPostData.mediaId == "null") {

            /*Media Validation*/
            Toast.makeText(mView.getContext(), mView.getContext()?.getString(R.string.add_channel_photo), Toast.LENGTH_SHORT).show()
            return Triple(null, null, null)

        }

        mChannelPostData = channelPostData
        //Finish Building Data to create channel
        UserManager.instance.getCurrentUser { success, user, token ->
            if (success) {
                //Available data
                mToken = token
                currentUser = user
                mChannelPostData.user_creator_id = user?.id
            }
        }

        return Triple(mToken, mChannelPostData, currentUser)
    }

    /**
     * [createChannel] defined in Controller interface to @Post channel
     * */
    override fun createChannel(channelPostData: ChannelPostData) {
        //Data Validation
        val info: Triple<Token?, ChannelPostData?, User?> = channelDataValidation(channelPostData)
        if (info.first?.token != null && info.second != null && info.third != null) {

            mView.showProgress()

            /*Upload New Channel Info -> media is available*/
            val call = Rest.getInstance().getmCallerRx().postChannel(info.first?.token!!, info.second!!)
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
                                     * save new [Channel] to
                                     * @see Realm
                                     * */
                                    response.newChannelGroupOrEvent?.save()

                                    /*Transition*/
                                    mView.hideProgress()
                                    mView.onCreateChannel(
                                            response.newChannelGroupOrEvent?.id,
                                            response.newChannelGroupOrEvent?.name,
                                            response.newChannelGroupOrEvent?.room_id,
                                            response.newChannelGroupOrEvent?.user_creator_id
                                    )

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
                                mView.showError("Unable to create a channel, try again later")
                                println("Error creating channel: " + throwable.message)
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
                "Choose a channel picture",
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

    override fun getUserId(callback: (Int?) -> Unit) {
        UserManager.instance.getCurrentUser { success, user, _ ->
            if (success) {
                user?.id.let {
                    callback(it)
                }
            }
        }
    }
}