package com.phdlabs.sungwon.a8chat_android.structure.camera.preview

import android.content.Intent
import android.graphics.Point
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import kotlinx.android.synthetic.main.activity_camera_preview.*
import kotlinx.android.synthetic.main.view_camera_control_close.*

/**
 * Created by paix on 1/15/18.
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

    /*User Interface*/
    fun setupUI() {
        //Switch close & back controls
        iv_camera_close.visibility = View.GONE
        iv_camera_back.visibility = View.VISIBLE
        iv_camera_back.setOnClickListener(this)
    }

    fun restoreUI() {
        //Switch close & back controls
        iv_camera_close.visibility = View.VISIBLE
        iv_camera_back.visibility = View.GONE
    }


    /*Photo loading*/
    override fun getPreviewLayout(): ImageView = iv_camera_preview

    override fun getScreenSize(): Point {
        val displaySize = Point()
        this.windowManager.defaultDisplay.getSize(displaySize)
        return displaySize
    }

    override fun onClick(p0: View?) {
        when (p0) {
            iv_camera_back -> {
                finish()
            }
        }
    }


    //TODO: Add camera controls on top of the image preview


}