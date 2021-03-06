package com.phdlabs.sungwon.a8chat_android.structure.camera

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.view.View
import com.otaliastudios.cameraview.CameraView
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.structure.camera.adapters.CameraPagerAdapter
import com.phdlabs.sungwon.a8chat_android.structure.camera.cameraControls.CameraCloseView
import com.phdlabs.sungwon.a8chat_android.structure.camera.cameraControls.CameraControlView
import com.phdlabs.sungwon.a8chat_android.structure.camera.videoPreview.VideoPreviewActivity
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import com.phdlabs.sungwon.a8chat_android.utility.camera.CameraControl
import kotlinx.android.synthetic.main.activity_camera.*
import kotlinx.android.synthetic.main.view_camera_control_close.*
import kotlinx.android.synthetic.main.view_camera_control_tabs.*

/**
 * Created by JPAM on 12/28/17.
 * CameraActivity for controlling CameraPagerAdapter with structure:
 * [Camera Roll - Normal - Hands Free]
 */
class CameraActivity : CoreActivity(), CameraContract.Camera.View, TabLayout.OnTabSelectedListener, View.OnClickListener {

    /*Controller*/
    override lateinit var controller: CameraContract.Camera.Controller

    /*User Interface*/
    override fun layoutId() = R.layout.activity_camera

    /*User Interface container*/
    override fun contentContainerId() = 0

    /*Properties*/
    override var activity: CameraActivity = this
    var mCameraView: CameraView? = null

    /*LifeCycle*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mCameraView = CameraView(this)
        CameraAController(this)
        setupUI()
    }

    override fun onStart() {
        super.onStart()
        controller.start()
    }

    override fun onResume() {
        super.onResume()
        controller.resume()
    }

    override fun onDestroy() {
        super.onDestroy()
        mCameraView?.destroy()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        /*Hide Status Bar*/
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()
        window.decorView.setOnSystemUiVisibilityChangeListener { visible ->
            when (visible) {
                0 -> print("System bars are visible")
            //TODO: Remove camera navigation
                else -> {
                    print("System bars are not visible")
                    //TODO: Setup camera navigation controllers
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        controller.pause()
    }

    override fun onStop() {
        super.onStop()
        controller.stop()
        setResult(Activity.RESULT_OK)
    }

    /*User Interface*/
    private fun setupUI() {
        /*View Pager Adapter*/
        cam_view_pager.adapter = CameraPagerAdapter(supportFragmentManager, this)
        cam_tabLayout_indicator.setupWithViewPager(cam_view_pager)
        /*Initial tab selection*/
        val tab = cam_tabLayout_indicator.getTabAt(Constants.CameraPager.NORMAL)
        tab?.select()
        /*Tab indicator & listeners*/
        cam_tabLayout_indicator.tabGravity = TabLayout.GRAVITY_FILL
        cam_view_pager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(cam_tabLayout_indicator))
        cam_tabLayout_indicator.addOnTabSelectedListener(this)
        /*Actions*/
        iv_camera_action.setOnClickListener(this)
        iv_camera_flash.setOnClickListener(this)
        iv_camera_flip.setOnClickListener(this)
        cc_close_back.setOnClickListener(this)
    }

    /*Tab Control*/
    override fun onTabReselected(tab: TabLayout.Tab?) {
        controller.onTabReselected(tab)
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
        controller.onTabUnselected(tab)
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        controller.onTabSelected(tab, cam_view_pager)
    }

    /*Camera Control*/
    override fun getCameraControl(): CameraControlView = cam_control

    override fun getCameraCloseControl(): CameraCloseView = cam_close_control

    /*Camera actions*/
    override fun onClick(p0: View?) {
        when (p0) {
            iv_camera_action -> {
                showProgress()
                controller.takePhoto(cam_view_pager)
            }
            iv_camera_flash -> {
                controller.manualFlash(cam_view_pager)
            }
            iv_camera_flip -> {
                /*Flip camera*/
                controller.cameraFlip(cam_view_pager)
            }
            cc_close_back -> {
                /*Finish camera activity*/
                onBackPressed()
            }
        }
    }

    /*Start photo preview activity*/
    fun getImageFilePath(filePath: String?) {
        controller.startPreviewActivity(filePath, false)
    }

    fun startVideoPreview() {
        val intent = Intent(this, VideoPreviewActivity::class.java)
        startActivityForResult(intent, Constants.RequestCodes.VIDEO_PREVIEW_REQ_CODE)
        //TODO: Reset controllers
    }


    /*Flash UI*/
    override fun flashFeedback(isFLashOn: Boolean) {
        if (isFLashOn) {
            iv_camera_flash.setImageDrawable(getDrawable(R.drawable.flash_on))
        } else {
            iv_camera_flash.setImageDrawable(getDrawable(R.drawable.flash_off))
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {

            /**Photo*/
                Constants.CameraIntents.EDITING_REQUEST_CODE -> {
                    if (cam_view_pager.adapter != null) {
                        val adapter = cam_view_pager.adapter as CameraPagerAdapter
                        adapter.refreshCameraRoll()
                    }
                }

            /**Video*/
                Constants.RequestCodes.VIDEO_PREVIEW_REQ_CODE -> {
                    val adapter = cam_view_pager.adapter as CameraPagerAdapter
                    adapter.refreshCameraRoll()
                }

                else -> {
                    super.onActivityResult(requestCode, resultCode, data)

                }

            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            super.onActivityResult(requestCode, resultCode, data)

        }
    }

}