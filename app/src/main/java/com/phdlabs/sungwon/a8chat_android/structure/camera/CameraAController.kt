package com.phdlabs.sungwon.a8chat_android.structure.camera

import android.content.Intent
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.view.View
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.structure.camera.fragments.normal.NormalFragment
import com.phdlabs.sungwon.a8chat_android.structure.camera.editing.EditingActivity
import com.phdlabs.sungwon.a8chat_android.structure.camera.fragments.handsFree.HandsFreeFragment
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import kotlinx.android.synthetic.main.view_camera_control_tabs.view.*

/**
 * Created by JPAM on 12/28/17.
 */
class CameraAController(val mView: CameraContract.Camera.View) : CameraContract.Camera.Controller {

    /*LOG*/
    private val TAG = "Camera Controller"

    /*Properties*/
    private var isFlashOn: Boolean = false
    private var isHandsFreeRecording: Boolean = false

    /*Initialization*/
    init {
        /*Ser controller*/
        mView.controller = this
    }

    /*LifeCycle*/
    override fun start() {}

    override fun resume() {}

    override fun pause() {}

    override fun stop() {
        mView.hideProgress()
    }


    /*Tab Selected Listener*/
    override fun onTabReselected(tab: TabLayout.Tab?) {

    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {

    }

    /*Tab selected visibility*/
    override fun onTabSelected(tab: TabLayout.Tab?, viewPager: ViewPager) {
        tab?.let {

            when (viewPager.currentItem) {
                Constants.CameraPager.NORMAL -> {
                    mView.getCameraControl().visibility = View.VISIBLE
                    mView.getCameraCloseControl().visibility = View.VISIBLE
                }

                Constants.CameraPager.HANDS_FREE -> {
                    mView.getCameraControl().visibility = View.VISIBLE
                    mView.getCameraCloseControl().visibility = View.VISIBLE
                }

                Constants.CameraPager.CAMERA_ROLL -> {
                    mView.getCameraControl().visibility = View.GONE
                    mView.getCameraCloseControl().visibility = View.GONE
                }

            }
        }
    }

    /*Take picture*/
    override fun takePhoto(viewPager: ViewPager) {

        when (viewPager.currentItem) {
        /*Normal Mode*/
            Constants.CameraPager.NORMAL -> {
                val normalFrag: NormalFragment = viewPager.adapter?.instantiateItem(viewPager, Constants.CameraPager.NORMAL) as NormalFragment
                normalFrag.takePicture()
            }
        /*Hands Free Mode*/
            Constants.CameraPager.HANDS_FREE -> {
                //UI
                mView.hideProgress()
                val handsFreeFragment = viewPager.adapter?.instantiateItem(viewPager, Constants.CameraPager.HANDS_FREE) as HandsFreeFragment
                handsFreeFragment.startVideoRecording()
                //UI
                isHandsFreeRecording = if (!isHandsFreeRecording) {
                    mView.getCameraControl().iv_camera_action.setImageResource(R.drawable.snap_stop)
                    true
                } else {
                    mView.getCameraControl().iv_camera_action.setImageResource(R.drawable.snap)
                    false
                }
            }

        }
    }

    /*Camera Flip -> Front Lens || Back Lens*/
    override fun cameraFlip(viewPager: ViewPager) {
        when (viewPager.currentItem) {
        /*Normal Mode*/
            Constants.CameraPager.NORMAL -> {
                val normalFrag: NormalFragment = viewPager.adapter?.instantiateItem(viewPager, Constants.CameraPager.NORMAL) as NormalFragment
                normalFrag.flipCamera()
            }
        /*HandsFree Mode*/
            Constants.CameraPager.HANDS_FREE -> {
                val handsFreeFragment = viewPager.adapter?.instantiateItem(viewPager, Constants.CameraPager.HANDS_FREE) as HandsFreeFragment
                handsFreeFragment.flipCamera()
            }
        }
    }

    /*Turn on or off manual flash_off*/
    override fun manualFlash(viewPager: ViewPager) {
        when (viewPager.currentItem) {
        /*Normal Mode*/
            Constants.CameraPager.NORMAL -> {
                val normalFrag: NormalFragment = viewPager.adapter?.instantiateItem(viewPager, Constants.CameraPager.NORMAL) as NormalFragment
                normalFrag.manualFlashSelection()

            }
        /*HandsFree Mode*/
            Constants.CameraPager.HANDS_FREE -> {
                val handsFreeFragment = viewPager.adapter?.instantiateItem(viewPager, Constants.CameraPager.HANDS_FREE) as HandsFreeFragment
                handsFreeFragment.manualFlashSelection()
            }
        }
        //Flash UI
        isFlashOn = !isFlashOn
        mView.flashFeedback(isFlashOn)
    }

    /*Start EditingActivity*/
    override fun startPreviewActivity(imageFilePath: String?, isFromCameraRoll: Boolean) {
        val intent = Intent(mView.getContext(), EditingActivity::class.java)
        intent.putExtra(Constants.CameraIntents.IMAGE_FILE_PATH, imageFilePath)
        intent.putExtra(Constants.CameraIntents.IS_FROM_CAMERA_ROLL, isFromCameraRoll)
        mView.activity.startActivityForResult(intent, Constants.CameraIntents.EDITING_REQUEST_CODE)
    }

}