package com.phdlabs.sungwon.a8chat_android.structure.camera.filters

import android.app.Activity
import android.os.Bundle
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.structure.camera.CameraContract
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import com.phdlabs.sungwon.a8chat_android.utility.camera.CameraControl
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_camera_filters.*

/**
 * Created by JPAM on 5/9/18.
 * Image Filtering for Camera App
 */
class ImageFilterActivity : CoreActivity(), CameraContract.Filters.View {

    /*Controller*/
    override lateinit var controller: CameraContract.Filters.Controller

    /*Layout*/
    override fun layoutId(): Int = R.layout.activity_camera_filters

    override fun contentContainerId(): Int = 0

    /*Properties*/
    private var imgFilePath: String? = null

    /*LifeCycle*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ImageFilterActController(this)
        //Get Photo
        intent.getStringExtra(Constants.CameraIntents.IMAGE_FILE_PATH)?.let {
            imgFilePath = it
        } ?: run {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        imgFilePath?.let {
            setupPhoto(it)
        }
    }

    /**
     * [setupPhoto]
     * Setup Photo for applying filters
     * */
    private fun setupPhoto(imgFilePath: String) {
        Picasso.with(this)
                .load(imgFilePath)
                .resize(acf_photo_iv.width, acf_photo_iv.height)
                .centerCrop()
                .into(acf_photo_iv)
    }
}