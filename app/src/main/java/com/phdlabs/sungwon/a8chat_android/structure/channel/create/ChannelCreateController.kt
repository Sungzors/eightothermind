package com.phdlabs.sungwon.a8chat_android.structure.channel.create

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.api.data.PostChannelData
import com.phdlabs.sungwon.a8chat_android.api.rest.Caller
import com.phdlabs.sungwon.a8chat_android.api.rest.Rest
import com.phdlabs.sungwon.a8chat_android.db.UserManager
import com.phdlabs.sungwon.a8chat_android.model.channel.Channel
import com.phdlabs.sungwon.a8chat_android.model.room.Room
import com.phdlabs.sungwon.a8chat_android.model.user.User
import com.phdlabs.sungwon.a8chat_android.model.user.registration.Token
import com.phdlabs.sungwon.a8chat_android.structure.channel.ChannelContract
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
class ChannelCreateController(val mView: ChannelContract.Create.View) : ChannelContract.Create.Controller {

    /*Properties*/
    private lateinit var mCaller: Caller

    /*Initialization*/
    init {
        mView.controller = this
    }

    /*LifeCycle*/
    override fun start() {
        mCaller = Rest.getInstance().caller
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
     * @return [PostChannelData][Token] for @Posting new channel
     * */
    private fun channelDataValidation(postChannelData: PostChannelData): Triple<Token?, PostChannelData?, User?> {

        //Data to be returned
        var mToken: Token? = null
        val mPostChannelData: PostChannelData?
        var currentUser: User? = null

        /*Info Validation*/
        if (postChannelData.name.isNullOrBlank() ||
                postChannelData.unique_id.isNullOrBlank() ||
                postChannelData.description.isNullOrBlank()) {

            Toast.makeText(mView.getContext(), mView.getContext()?.getString(R.string.incomplete_information), Toast.LENGTH_SHORT).show()
            return Triple(null, null, null)

        } else if (postChannelData.mediaId.isNullOrBlank() || postChannelData.mediaId == "null") {

            //Channel picture missing
            Toast.makeText(mView.getContext(), mView.getContext()?.getString(R.string.add_channel_photo), Toast.LENGTH_SHORT).show()
            return Triple(null, null, null)

        }

        /*Media Validation*/
        mPostChannelData = postChannelData

        //Finish Building Data to create channel
        UserManager.instance.getCurrentUser { success, user, token ->
            if (success) {
                //Available data
                mToken = token
                currentUser = user
                mPostChannelData.user_creator_id = user?.id
            }
        }

        return Triple(mToken, mPostChannelData, currentUser)
    }

    /**
     * [createChannel] defined in Controller interface to @Post channel
     * */
    override fun createChannel(postChannelData: PostChannelData) {
        //Data Validation
        val info: Triple<Token?, PostChannelData?, User?> = channelDataValidation(postChannelData)
        if (info.first?.token != null && info.second != null) {

            mView.showProgress()
            /*Local channel data*/
            val currentChannel = Channel()
            var currentRoom = Room()
            currentChannel.unique_id = info.second?.unique_id
            currentChannel.description = info.second?.description
            currentChannel.add_to_profile = info.second?.add_to_profile

            /*Upload New Channel Info -> media is available*/
            val call = Rest.getInstance().getmCallerRx().postChannel(info.first?.token!!, info.second!!)
            call.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { response ->

                                if (response.isSuccess) {

                                    //Save Room to Realm
                                    response.room?.let {
                                        currentRoom = it
                                        currentRoom.user = info.third
                                        currentRoom.save()
                                    }

                                    //Create & Save Channel to Realm
                                    currentChannel.id = response.newChannelGroupOrEvent?.id
                                    currentChannel.name = response.newChannelGroupOrEvent?.name
                                    currentChannel.room_id = response.newChannelGroupOrEvent?.room_id
                                    currentChannel.user_creator_id = response.newChannelGroupOrEvent?.user_creator_id
                                    currentChannel.profile_picture_string = response.newChannelGroupOrEvent?.profile_picture_string
                                    currentChannel.avatar = response.newChannelGroupOrEvent?.avatar
                                    currentChannel.createdAt = response.newChannelGroupOrEvent?.createdAt
                                    currentChannel.save()

                                    /*Transition*/
                                    mView.hideProgress()
                                    mView.finishActivity(currentChannel.id, currentChannel.name, currentChannel.room_id)

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
                                        //Save temporry media
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
}