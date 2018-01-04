package com.phdlabs.sungwon.a8chat_android.structure.camera.fragments.normal

import android.hardware.Camera

/**
 * Created by paix on 1/1/18.
 * Camera Normal feature controller
 */
class NormalController(val mView: NormalContract.View) : NormalContract.Controller {


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


}