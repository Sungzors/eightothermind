package com.phdlabs.sungwon.a8chat_android.structure.channel.createPost

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import com.andremion.louvre.Louvre
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
            //mView.activity.setResult(Activity.RESULT_OK)
            val intent = Intent()
            val filePathArrayList = mView.getPostData().second.map { it.toString() }
            intent.putStringArrayListExtra(Constants.IntentKeys.MEDIA_POST, filePathArrayList.toCollection(ArrayList()))
            intent.putExtra(Constants.IntentKeys.MEDIA_POST_MESSAGE, mView.getPostData().first)
            mView.activity.setResult(Activity.RESULT_OK, intent)
            mView.close()
        } else {
            mView.showError("This Post is empty!")
        }
    }
}