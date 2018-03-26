package com.phdlabs.sungwon.a8chat_android.structure.camera

import android.support.annotation.IdRes
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import com.phdlabs.sungwon.a8chat_android.structure.camera.cameraControls.CameraCloseView
import com.phdlabs.sungwon.a8chat_android.structure.camera.cameraControls.CameraControlView
import com.phdlabs.sungwon.a8chat_android.structure.core.BaseController
import com.phdlabs.sungwon.a8chat_android.structure.core.BaseView

/**
 * Created by JPAM on 12/28/17.
 */
interface CameraContract {

    /*Camera Functionality*/
    interface Camera {

        interface View : BaseView<Controller> {
            /*Camera Control View*/
            fun getCameraControl(): CameraControlView

            /*Camera Close View Control*/
            fun getCameraCloseControl(): CameraCloseView

            /*Flash UI*/
            fun flashFeedback(isFLashOn: Boolean)

            /*Activity*/
            var activity: CameraActivity
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

    /*Sharing Functionality*/
    interface Share {

        interface View : BaseView<Controller> {
            //Fragment Management
            fun swapContainer(@IdRes contentContainer: Int): Int
        }

        interface Controller : BaseController {
            fun onCreate()
        }
    }

}