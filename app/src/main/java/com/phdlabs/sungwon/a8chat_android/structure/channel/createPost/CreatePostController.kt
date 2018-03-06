package com.phdlabs.sungwon.a8chat_android.structure.channel.createPost

import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import com.andremion.louvre.Louvre
import com.phdlabs.sungwon.a8chat_android.structure.channel.ChannelContract
import com.phdlabs.sungwon.a8chat_android.utility.Constants

/**
 * Created by paix on 3/5/18.
 * [CreatePostController] for [CreatePostActivity]
 */
class CreatePostController(val mView: ChannelContract.CreatePost.View): ChannelContract.CreatePost.Controller {


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
            }else {
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
}