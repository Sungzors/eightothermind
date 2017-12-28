package com.phdlabs.sungwon.a8chat_android.structure.camera

import android.os.Bundle
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import kotlinx.android.synthetic.main.activity_camera_controller.*

/**
 * Created by paix on 12/28/17.
 */
class CameraActivity: CoreActivity(), CameraContract.View {

    /*Controller*/
    override lateinit var controller: CameraContract.Controller

    /*User Interface*/
    override fun layoutId() = R.layout.activity_camera_controller

    /*User Interface container*/
    override fun contentContainerId() = 0 //TODO: set container for the camera swipe

    /*LifeCycle*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*View Pager Adapter*/
        cam_view_pager.adapter = CameraPagerAdapter(supportFragmentManager)

    }


}