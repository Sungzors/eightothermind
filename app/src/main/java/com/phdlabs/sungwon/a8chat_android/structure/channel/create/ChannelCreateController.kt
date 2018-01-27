package com.phdlabs.sungwon.a8chat_android.structure.channel.create

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import com.phdlabs.sungwon.a8chat_android.api.data.PostChannelData
import com.phdlabs.sungwon.a8chat_android.api.rest.Caller
import com.phdlabs.sungwon.a8chat_android.api.rest.Rest
import com.phdlabs.sungwon.a8chat_android.db.UserManager
import com.phdlabs.sungwon.a8chat_android.model.user.registration.Token
import com.phdlabs.sungwon.a8chat_android.structure.channel.ChannelContract
import com.phdlabs.sungwon.a8chat_android.utility.camera.CameraControl
import com.vicpin.krealmextensions.queryFirst
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
    private val TAG = "ChannelCreateController"
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
    private fun channelDataValidation(postChannelData: PostChannelData): Pair<Token?, PostChannelData?> {

        //Data to be returned
        var mToken: Token? = null
        var mPostChannelData: PostChannelData? = null

        /*Info Validation*/
        if (postChannelData.name.isNullOrBlank() ||
                postChannelData.unique_id.isNullOrBlank() ||
                postChannelData.description.isNullOrBlank()) {

            Toast.makeText(mView.getContext(), "Incomplete information", Toast.LENGTH_SHORT).show()
            return Pair(null, null)

            //TODO: Talk with Tomer, backend data type inconsistency
        } else if (postChannelData.mediaId.isNullOrBlank() || postChannelData.mediaId == "null") {
            //Channel picture missing
            Toast.makeText(mView.getContext(), "Add channel photo", Toast.LENGTH_SHORT).show()
            return Pair(null, null)
        }

        /*Media Validation*/
        mPostChannelData = postChannelData
        //Build Data to create channel
        UserManager.instance.getCurrentUser { success, user, token ->
            if (success) {
                //Available data
                mToken = token
                mPostChannelData.user_creator_id = user?.id
            }
        }
        return Pair(mToken, mPostChannelData)
    }

    /**
     * [createChannel] defined in Controller interface to @Post channel
     * */
    override fun createChannel(postChannelData: PostChannelData) {
        //Data Validation
        val info: Pair<Token?, PostChannelData?> = channelDataValidation(postChannelData)
        if (info.first?.token != null && info.second != null) {
            /*Upload New Channel Info -> media is available*/
            val call = Rest.getInstance().getmCallerRx().postChannel(info.first?.token!!, info.second!!)
            call.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { response ->
                        if (response.isSuccess) {
                            //TODO: Save channels to Realm -> Make realm model

                        }
                    }

//        val call = mCaller.postChannel(Preferences(mView.getContext()!!).getPreferenceString(Constants.PrefKeys.TOKEN_KEY), data)
//        call.enqueue(object : Callback8<ChannelResponse, ChannelPostEvent>(mEventBus) {
//            override fun onSuccess(data: ChannelResponse?) {
//                val a = data
//                val chan = Channel(data!!.newNewChannelGroupOrEvent!!.id.toInt(), data.newNewChannelGroupOrEvent!!.name, data.newNewChannelGroupOrEvent!!.name, /*data.newNewChannelGroupOrEvent!!.roomId*/ data.newNewChannelGroupOrEvent!!.room_id)
//                chan.user_creator_id = data.newNewChannelGroupOrEvent!!.user_creator_id
//                chan.profile_picture_string = data.newNewChannelGroupOrEvent!!.profile_picture_string
//                chan.isRead = true
//                TemporaryManager.instance.mChannelList.add(chan) //TODO: add realm
//                //TODO: lead to channel screen
//                mView.finishActivity(data!!.newNewChannelGroupOrEvent!!.id, data.newNewChannelGroupOrEvent!!.name, data.newNewChannelGroupOrEvent!!.room_id.toInt())
//            }
//        })
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
                //Set image in UI
                mView.setChannelImage(it)
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
                    call.subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe { response ->
                                if (response.isSuccess) {
                                    response.mediaArray?.let {
                                        mView.getMedia(it[0])
                                        mView.hideProgress()
                                    }
                                } else if (response.isError) {
                                    mView.showError("Couldn't upload picture, try again later")
                                    mView.hideProgress()
                                }
                            }
                }
            }
        }
    }
}