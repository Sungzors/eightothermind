package com.phdlabs.sungwon.a8chat_android.structure.camera.share

import com.phdlabs.sungwon.a8chat_android.structure.camera.CameraContract

/**
 * Created by paix on 3/26/18.
 * [ShareCameraMedia] Controller
 */
class ShareCameraMediaController(val mView: CameraContract.Share.View) : CameraContract.Share.Controller {

    init {
        mView.controller = this
    }

    /*Properties*/

    /*LifeCycle*/
    override fun onCreate() {
    }

    override fun start() {
    }

    override fun resume() {
    }

    override fun pause() {
    }

    override fun stop() {
    }

}