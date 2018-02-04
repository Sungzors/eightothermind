package com.phdlabs.sungwon.a8chat_android.structure.camera.preview

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Point
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.util.DisplayMetrics
import android.view.View
import android.widget.ImageView
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import kotlinx.android.synthetic.main.activity_camera_preview.*
import kotlinx.android.synthetic.main.view_camera_control_close.*
import kotlinx.android.synthetic.main.view_camera_control_save.*

/**
 * Created by paix on 1/15/18.
 * [PreviewActivity] shows the picture taken from the [NormalFragment]
 * or the picture selected from the [CameraRollFragment]
 */
class PreviewActivity : CoreActivity(), PreviewContract.View, View.OnClickListener {

    /*Controller*/
    override lateinit var controller: PreviewContract.Controller

    /*Layout*/
    override fun layoutId(): Int = R.layout.activity_camera_preview

    override fun contentContainerId(): Int = 0

    /*Properties*/
    private var imgFilePath: String? = null


    /*LifeCycle*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*Controller init*/
        PreviewActivityController(this)
        /*Load image*/
        imgFilePath = intent.extras.getString(Constants.CameraIntents.IMAGE_FILE_PATH)
        imgFilePath?.let {
            controller.loadImagePreview(it)
        }
    }

    override fun onResume() {
        super.onResume()
        controller.resume()
        setupUI()
    }

    override fun onStop() {
        super.onStop()
        controller.stop()
        restoreUI()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == Constants.PermissionsReqCode.WRITE_EXTERNAL_REQ_CODE) {
            if (grantResults.size != 1 || grantResults.get(0) != PackageManager.PERMISSION_GRANTED) {
                showError(getString(R.string.request_write_external_permission))
            } else {
                controller.saveImageToGallery()
            }
        } else {

            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    /*User Interface*/
    fun setupUI() {
        //Switch close & back controls
        iv_camera_close.visibility = View.GONE
        iv_camera_back.visibility = View.VISIBLE
        cc_close_back.setOnClickListener(this)
        iv_camera_save.setOnClickListener(this)
    }

    fun restoreUI() {
        //Switch close & back controls
        iv_camera_close.visibility = View.VISIBLE
        iv_camera_back.visibility = View.GONE
    }


    /*Photo loading*/
    override fun getPreviewLayout(): ImageView = iv_camera_preview

    override fun getScreenSize(): Pair<Int, Int> {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        iv_camera_preview.maxWidth = displayMetrics.widthPixels
        iv_camera_preview.maxHeight = displayMetrics.heightPixels
        return Pair(displayMetrics.widthPixels, displayMetrics.heightPixels)
    }

    override fun onClick(p0: View?) {
        when (p0) {
            cc_close_back -> {
                onBackPressed()
            }
            iv_camera_save -> {
                if (ContextCompat.checkSelfPermission(this,
                        Constants.AppPermissions.WRITE_EXTERNAL) != PackageManager.PERMISSION_GRANTED) {
                    controller.requestStoragePermissions()
                } else {
                    controller.saveImageToGallery()
                }
            }
        }
    }

    override fun feedback(message: String) {
        showToast(message)
    }


    //TODO: Add camera controls on top of the image preview


}