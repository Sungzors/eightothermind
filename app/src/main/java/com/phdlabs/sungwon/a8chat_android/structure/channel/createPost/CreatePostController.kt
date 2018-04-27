package com.phdlabs.sungwon.a8chat_android.structure.channel.createPost

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import com.andremion.louvre.Louvre
import com.phdlabs.sungwon.a8chat_android.api.data.SendMessageStringData
import com.phdlabs.sungwon.a8chat_android.api.rest.Rest
import com.phdlabs.sungwon.a8chat_android.db.user.UserManager
import com.phdlabs.sungwon.a8chat_android.structure.channel.ChannelContract
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import com.phdlabs.sungwon.a8chat_android.utility.camera.CameraControl
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream

/**
 * Created by JPAM on 3/5/18.
 * [CreatePostController] for [CreatePostActivity]
 */
class CreatePostController(val mView: ChannelContract.CreatePost.View) : ChannelContract.CreatePost.Controller {


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

    /*Request read external storage permissions*/
    override fun requestStoragePermissions() {
        val whatPermissions = arrayOf(Constants.AppPermissions.READ_EXTERNAL)
        mView.getContext()?.let {
            //Request Permissions
            if (ContextCompat.checkSelfPermission(it, whatPermissions.get(0)) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(mView.getContext() as CreatePostActivity,
                        whatPermissions, Constants.PermissionsReqCode.READ_EXTERNAL_STORAGE)
            } else {
                openMediaPicker()
            }
        }
    }

    override fun openMediaPicker() {
        Louvre.init(mView.activity)
                .setRequestCode(Constants.CameraIntents.OPEN_MEDIA_PICKER)
                .setMaxSelection(8)
                .setMediaTypeFilter(Louvre.IMAGE_TYPE_JPEG, Louvre.IMAGE_TYPE_PNG)
                .open()
    }

    override fun createPost() {
        if (mView.validatePost()) {

            mView.showProgress()

            //mView.activity.setResult(Activity.RESULT_OK)
            val intent = Intent()
            val filePathArrayList = mView.getPostData().second.map { it.toString() }
//            intent.putStringArrayListExtra(Constants.IntentKeys.MEDIA_POST, filePathArrayList.toCollection(ArrayList()))
//            intent.putExtra(Constants.IntentKeys.MEDIA_POST_MESSAGE, mView.getPostData().first)
//            mView.activity.setResult(Activity.RESULT_OK)
//            mView.activity.finish()

            //TODO: Create the post here and then retrieve chat history when we go back to the channel.

            UserManager.instance.getCurrentUser { isSuccess, user, token ->
                if (isSuccess) {
                    user?.let {
                        token?.token?.let {
                            val formBodyBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)
                                    .addFormDataPart("message", mView.getPostData().first)
                                    .addFormDataPart("userId", user.id!!.toString())
                                    .addFormDataPart("roomId", mView.getRoomId.toString())
                            //Create Media-Post BodyPart & Call
                            filePathArrayList?.let {
                                if (it.count() > 0) {
                                    for (mediaFile in it) {
                                        val imageBitmap = MediaStore.Images.Media.getBitmap(mView.activity.contentResolver, Uri.parse(mediaFile))
                                        imageBitmap?.let {
                                            val bos = ByteArrayOutputStream()
                                            it.compress(Bitmap.CompressFormat.PNG, 0, bos)
                                            val bitmapData = bos.toByteArray()
                                            formBodyBuilder.addFormDataPart("file[${filePathArrayList.indexOf(mediaFile)}]",
                                                    "8_" + System.currentTimeMillis(),
                                                    RequestBody.create(MediaType.parse("image/*"), bitmapData))
                                        }
                                    }
                                    val formBody = formBodyBuilder.build()
                                    val call = Rest.getInstance().getmCallerRx().postChannelMediaPost(token.token!!, formBody, true)
                                    call.subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe({ response ->
                                                if (response.isSuccess) {
                                                    println("Message: " + response.messageInfo?.message)
                                                    mView.hideProgress()
                                                    mView.activity.setResult(Activity.RESULT_OK)
                                                    mView.activity.finish()
                                                } else if (response.isError) {
                                                    mView.hideProgress()
                                                    mView.showError("Could not post")
                                                    mView.activity.setResult(Activity.RESULT_CANCELED)
                                                    mView.activity.finish()
                                                }
                                            }, { throwable ->
                                                mView.hideProgress()
                                                mView.showError(throwable.localizedMessage)
                                                mView.activity.setResult(Activity.RESULT_CANCELED)
                                                mView.activity.finish()
                                            })
                                } else {
                                    //Create String-Message Post
                                    val call = Rest.getInstance().getmCallerRx().postChannelMessagePost(
                                            token.token!!,
                                            SendMessageStringData(user.id!!, mView.getPostData().first, mView.getRoomId),
                                            true
                                    )
                                    call.subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe({ response ->
                                                if (response.isSuccess) {
                                                    println("Message: " + response.messageInfo?.message)
                                                    mView.hideProgress()
                                                    mView.activity.setResult(Activity.RESULT_OK)
                                                    mView.activity.finish()
                                                } else if (response.isError) {
                                                    mView.hideProgress()
                                                    mView.showError("Could not post")
                                                    mView.activity.setResult(Activity.RESULT_CANCELED)
                                                    mView.activity.finish()
                                                }
                                            }, { throwable ->
                                                mView.hideProgress()
                                                mView.showError(throwable.localizedMessage)
                                                mView.activity.setResult(Activity.RESULT_CANCELED)
                                                mView.activity.finish()
                                            })
                                }
                            }
                        }
                    }
                }
            }


        } else {
            mView.showError("This Post is empty!")
        }
    }
}