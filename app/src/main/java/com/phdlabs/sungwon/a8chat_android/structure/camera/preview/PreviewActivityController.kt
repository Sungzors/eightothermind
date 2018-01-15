package com.phdlabs.sungwon.a8chat_android.structure.camera.preview

import com.squareup.picasso.Picasso
import java.io.File

/**
 * Created by paix on 1/15/18.
 */
class PreviewActivityController(val mView: PreviewContract.View) : PreviewContract.Controller {

    /*LOG*/
    private val TAG = "Camera Preview"

    /*Properties*/

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


    /*Load image preview*/
    override fun loadImagePreview(filePath: String?) {
        var measure = mView.getScreenSize()
        Picasso.with(mView.getContext())
                .load(File(filePath))
                .resize(mView.getScreenSize().x, mView.getScreenSize().y)
                .centerInside()
                .rotate(90f) //Full screen
                .into(mView.getPreviewLayout())
    }


}