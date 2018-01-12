package com.phdlabs.sungwon.a8chat_android.structure.camera

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.view.View
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.structure.camera.adapters.CameraPagerAdapter
import com.phdlabs.sungwon.a8chat_android.structure.camera.cameraControl.CameraControlView
import com.phdlabs.sungwon.a8chat_android.structure.camera.fragments.cameraRoll.CameraRollFragment
import com.phdlabs.sungwon.a8chat_android.structure.camera.fragments.handsFree.HandsFreeFragment
import com.phdlabs.sungwon.a8chat_android.structure.camera.fragments.normal.NormalFragment
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import kotlinx.android.synthetic.main.activity_camera.*

/**
 * Created by paix on 12/28/17.
 * CameraActivity for controlling CameraPagerAdapter with structure:
 * [Camera Roll - Normal - Hands Free]
 */
class CameraActivity : CoreActivity(), CameraContract.View, TabLayout.OnTabSelectedListener {

    /*Controller*/
    override lateinit var controller: CameraContract.Controller

    /*User Interface*/
    override fun layoutId() = R.layout.activity_camera

    /*User Interface container*/
    override fun contentContainerId() = 0 //TODO: set container for the camera swipe

    /*LifeCycle*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
    }

    /*User Interface*/
    private fun setupUI() {
        /*View Pager Adapter*/
        cam_view_pager.adapter = CameraPagerAdapter(supportFragmentManager, this)
        cam_tabLayout_indicator.setupWithViewPager(cam_view_pager)
        /*Initial tab selection*/
        val tab = cam_tabLayout_indicator.getTabAt(Constants.CameraPager.NORMAL)
        tab?.select()
        controller.currentFragment(cam_view_pager)
        /*Tab indicator & listeners*/
        cam_tabLayout_indicator.tabGravity = TabLayout.GRAVITY_FILL
        cam_view_pager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(cam_tabLayout_indicator))
        cam_tabLayout_indicator.addOnTabSelectedListener(this)
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

    override fun currentFragment(fragment: Fragment) {
        if (fragment::class == CameraRollFragment::class) {
            print("Camera Roll Frag")
        } else if (fragment::class == NormalFragment::class) {
            print("Normal Fragment")
        } else if (fragment::class == HandsFreeFragment::class) {
            print("Hands Free Fragment")
        }
    }

}