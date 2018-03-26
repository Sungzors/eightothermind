package com.phdlabs.sungwon.a8chat_android.structure.camera.share

import android.os.Bundle
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.structure.camera.CameraContract
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import com.phdlabs.sungwon.a8chat_android.utility.Constants

/**
 * Created by paix on 3/26/18.
 * Used for sharing camera media to Channels, Events & Private Conversations.
 */
class ShareCameraMedia : CoreActivity(), CameraContract.Share.View {


    /*Controller*/
    override lateinit var controller: CameraContract.Share.Controller

    /*Properties*/
    private var currentContainer: Int = 0

    /*Layout*/
    override fun layoutId(): Int = R.layout.activity_share_camera

    override fun contentContainerId(): Int = currentContainer

    override fun swapContainer(contentContainer: Int): Int {
        currentContainer = contentContainer
        return currentContainer
    }

    /*LifeCycle*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ShareCameraMediaController(this)
        controller.onCreate()
        processIntent()
    }

    override fun onStart() {
        super.onStart()
        controller.start()
    }

    override fun onPause() {
        super.onPause()
        controller.pause()
    }

    override fun onResume() {
        super.onResume()
        controller.resume()
    }

    override fun onStop() {
        super.onStop()
        controller.stop()
    }

    /*Process Intent Information*/
    private fun processIntent() {
        intent.getStringExtra(Constants.CameraIntents.IMAGE_FILE_PATH)?.let {
            println("Image File Path: $it")
        }
    }

}