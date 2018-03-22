package com.phdlabs.sungwon.a8chat_android.structure.main

import android.content.Intent
import com.github.nkzawa.socketio.client.Socket
import com.phdlabs.sungwon.a8chat_android.db.user.UserManager
import com.phdlabs.sungwon.a8chat_android.services.googlePlay.CheckGPServices
import com.phdlabs.sungwon.a8chat_android.structure.camera.CameraActivity
import com.phdlabs.sungwon.a8chat_android.utility.Constants

/**
 * Created by SungWon on 10/13/2017.
 * Updated by JPAM on 02/12/2018
 */
class MainAController(val mView: MainContract.View) : MainContract.Controller {

    //connects to MainActivity
    //private val mSocket: Socket
    //private var isConnected: Boolean

    init {
        mView.controller = this
    }

    override fun onCreate() {
    }

    override fun start() {
    }

    override fun resume() {
        //Google Play Services -> Firebase + Notifications
        CheckGPServices.instance.isGooglePlayServicesAvailable(mView.activity)
    }

    override fun pause() {
    }

    override fun stop() {
    }

    override fun showHome() {

    }

    override fun showCamera() {
        mView.activity.let {
            it.startActivityForResult(Intent(it.context, CameraActivity::class.java),
                    Constants.CameraIntents.CAMERA_REQUEST_CODE)
        }
    }

    override fun getUserId(callback: (Int?) -> Unit) {
        UserManager.instance.getCurrentUser { success, user, _ ->
            if (success) {
                user?.id.let {
                    callback(it)
                }
            }
        }
    }

}