package com.phdlabs.sungwon.a8chat_android.structure.camera.preview

import android.content.pm.PackageManager
import android.os.Build
import android.support.v13.app.ActivityCompat
import android.support.v4.content.ContextCompat
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import com.phdlabs.sungwon.a8chat_android.utility.DeviceInfo
import com.phdlabs.sungwon.a8chat_android.utility.camera.CameraControl
import com.squareup.picasso.Picasso
import java.io.File

/**
 * Created by paix on 1/15/18.
 */
class PreviewActivityController(val mView: PreviewContract.View) : PreviewContract.Controller {

    /*LOG*/
    private val TAG = "Camera Preview"

    /*Properties*/
    lateinit var imageFilePath: String

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

    override fun requestStoragePermissions() {
        val whatPermissions = arrayOf(Constants.AppPermissions.WRITE_EXTERNAL)
        mView.getContext()?.let {
            //Request Permissions
            if (ContextCompat.checkSelfPermission(it, whatPermissions.get(0)) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(mView.getContext() as PreviewActivity,
                        whatPermissions, Constants.PermissionsReqCode.WRITE_EXTERNAL_REQ_CODE)
            }
        }
    }


    /*Load image preview*/
    override fun loadImagePreview(filePath: String?) {

        filePath?.let {
            imageFilePath = it
            if (DeviceInfo.INSTANCE.isWarningDevice(Build.MODEL)) {
                Picasso.with(mView.getContext())
                        .load(File(it))
                        .rotate(270f) //Full screen
                        .into(mView.getPreviewLayout())
            } else {
                Picasso.with(mView.getContext())
                        .load(File(it))
                        .rotate(90f) //Full screen
                        .into(mView.getPreviewLayout())
            }
        }
    }

    /*Save file*/
    override fun saveImageToGallery() {
        imageFilePath.let {
            mView.getContext()?.let {
                CameraControl.instance.addToGallery(it, imageFilePath)
                mView.feedback("photo saved to gallery")
            }
        }
    }

}