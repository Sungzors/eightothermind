package com.phdlabs.sungwon.a8chat_android.structure.camera

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import kotlinx.android.synthetic.main.activity_camera.*

/**
 * Created by paix on 12/28/17.
 */
class CameraActivity: CoreActivity(), CameraContract.View, TabLayout.OnTabSelectedListener {

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
        cam_view_pager.adapter = CameraPagerAdapter(supportFragmentManager)
        /*Tab Layout*/
        cam_tabLayout.addTab(cam_tabLayout.newTab().setText(getString(R.string.camera_left_tab)))
        cam_tabLayout.addTab(cam_tabLayout.newTab().setText(getString(R.string.camera_center_tab)))
        cam_tabLayout.addTab(cam_tabLayout.newTab().setText(getString(R.string.camera_right_tab)))
        cam_tabLayout.tabGravity = TabLayout.GRAVITY_FILL
        cam_view_pager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(cam_tabLayout))
        cam_tabLayout.addOnTabSelectedListener(this)
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
        controller.onTabReselected(tab)
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
        controller.onTabUnselected(tab)
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        controller.onTabSelected(tab, cam_view_pager)
    }

}