package com.phdlabs.sungwon.a8chat_android.structure.camera

/**
 * Created by paix on 12/28/17.
 */
class CameraAController(val mView:CameraContract.View): CameraContract.Controller {

    /*LOG*/
    private val TAG = "Camera Controller"

    /*Initialization*/
    init {
        mView.controller = this
    }

    /*LifeCycle*/
    override fun start() {}

    override fun resume() {}

    override fun pause() {}

    override fun stop() {}


}