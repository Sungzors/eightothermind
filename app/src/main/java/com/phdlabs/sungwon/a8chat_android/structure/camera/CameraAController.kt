package com.phdlabs.sungwon.a8chat_android.structure.camera

import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.View
import com.phdlabs.sungwon.a8chat_android.utility.Constants

/**
 * Created by paix on 12/28/17.
 */
class CameraAController(val mView: CameraContract.View) : CameraContract.Controller {

    /*LOG*/
    private val TAG = "Camera Controller"

    /*Properties*/


    /*Initialization*/
    init {
        /*Ser controller*/
        mView.controller = this
    }

    /*LifeCycle*/
    override fun start() {}

    override fun resume() {}

    override fun pause() {}

    override fun stop() {}


    /*Tab Selected Listener*/
    override fun onTabReselected(tab: TabLayout.Tab?) {

    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {

    }

    /*Tab selected visibility*/
    override fun onTabSelected(tab: TabLayout.Tab?, viewPager: ViewPager) {
        tab?.let {
            /*View pager item position*/
            viewPager.setCurrentItem(it.position)
            /*Show || Hide Camera Controls depending on tab*/
            if (it.position.equals(Constants.CameraPager.NORMAL) ||
                    it.position.equals(Constants.CameraPager.HANDS_FREE)) {
                mView.getCameraControl().visibility = View.VISIBLE
            } else {
                mView.getCameraControl().visibility = View.GONE
            }
            /*Current Fragment*/
            currentFragment(viewPager)
        }
    }

    /*Current available fragment on screen*/
    override fun currentFragment(viewPager: ViewPager) {
        if (viewPager.currentItem == Constants.CameraPager.CAMERA_ROLL) {
            mView.currentFragment(viewPager.adapter?.instantiateItem(viewPager, Constants.CameraPager.CAMERA_ROLL) as Fragment)
        } else if (viewPager.currentItem == Constants.CameraPager.NORMAL) {
            mView.currentFragment(viewPager.adapter?.instantiateItem(viewPager, Constants.CameraPager.NORMAL) as Fragment)
        }else {
            mView.currentFragment(viewPager.adapter?.instantiateItem(viewPager, Constants.CameraPager.HANDS_FREE) as Fragment)
        }
    }

}