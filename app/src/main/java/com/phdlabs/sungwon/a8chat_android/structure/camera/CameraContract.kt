package com.phdlabs.sungwon.a8chat_android.structure.camera

import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import com.phdlabs.sungwon.a8chat_android.structure.camera.cameraControl.CameraControlView
import com.phdlabs.sungwon.a8chat_android.structure.core.BaseController
import com.phdlabs.sungwon.a8chat_android.structure.core.BaseView

/**
 * Created by paix on 12/28/17.
 */
interface CameraContract {

    interface View:BaseView<Controller>{
        fun getCameraControl(): CameraControlView
    }

    interface Controller: BaseController {

        /*Tab selection*/
        fun onTabReselected(tab: TabLayout.Tab?)
        fun onTabUnselected(tab: TabLayout.Tab?)
        fun onTabSelected(tab: TabLayout.Tab?, viewPager: ViewPager)
    }

}