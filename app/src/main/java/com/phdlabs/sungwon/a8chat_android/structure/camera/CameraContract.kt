package com.phdlabs.sungwon.a8chat_android.structure.camera

import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import com.phdlabs.sungwon.a8chat_android.structure.camera.cameraControls.CameraControlView
import com.phdlabs.sungwon.a8chat_android.structure.core.BaseController
import com.phdlabs.sungwon.a8chat_android.structure.core.BaseView

/**
 * Created by paix on 12/28/17.
 */
interface CameraContract {

    interface View : BaseView<Controller> {
        /*Control View*/
        fun getCameraControl(): CameraControlView
        /*Flash UI*/
        fun flashFeedback(isFLashOn: Boolean)
    }

    interface Controller : BaseController {

        /*Tab selection*/
        fun onTabReselected(tab: TabLayout.Tab?)

        fun onTabUnselected(tab: TabLayout.Tab?)
        fun onTabSelected(tab: TabLayout.Tab?, viewPager: ViewPager)
        /*Camera Actions*/
        fun takePhoto(viewPager: ViewPager)

        /*Camera Flip*/
        fun cameraFlip(viewPager: ViewPager)

        /*Manual Flash*/
        fun manualFlash(viewPager: ViewPager)

        /*Start Preview Activity*/
        fun startPreviewActivity(imageFilePath: String?)

    }

}