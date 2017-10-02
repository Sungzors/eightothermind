package com.phdlabs.sungwon.a8chat_android.structure.profile

/**
 * Created by SungWon on 10/2/2017.
 */
class ProfileAController(val mView: ProfileContract.View): ProfileContract.Controller{

    init {
        mView.controller = this
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