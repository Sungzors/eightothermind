package com.phdlabs.sungwon.a8chat_android.structure.main

/**
 * Created by SungWon on 10/13/2017.
 */
class MainAController(val mView: MainContract.View): MainContract.Controller{

    //connects to MainActivity

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

    override fun showHome() {

    }

    override fun showCamera() {
    }

    override fun showProfile() {
    }
}