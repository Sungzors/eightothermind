package com.phdlabs.sungwon.a8chat_android.structure.camera

import android.content.Intent
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.view.View
import com.phdlabs.sungwon.a8chat_android.structure.camera.adapters.CameraPagerAdapter
import com.phdlabs.sungwon.a8chat_android.structure.camera.fragments.normal.NormalFragment
import com.phdlabs.sungwon.a8chat_android.structure.camera.preview.PreviewActivity
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
            /*View pager item position*/
            viewPager.currentItem = it.position
            /*Show || Hide Camera Controls depending on tab*/
            if (it.position.equals(Constants.CameraPager.NORMAL) ||
                    it.position.equals(Constants.CameraPager.HANDS_FREE)) {
                mView.getCameraControl().visibility = View.VISIBLE
            } else {
                mView.getCameraControl().visibility = View.GONE
            }
        }
    }

    /*Take picture*/
    override fun takePhoto(viewPager: ViewPager) {
        if (viewPager.currentItem == Constants.CameraPager.NORMAL) {
            val normalFrag: NormalFragment = viewPager.adapter?.instantiateItem(viewPager, Constants.CameraPager.NORMAL) as NormalFragment
            normalFrag.takePicture()
        }
    }

    /*Camera Flip -> Front Lens || Back Lens*/
    override fun cameraFlip(viewPager: ViewPager) {
        /*NormalCamera Fragment*/
        if (viewPager.currentItem == Constants.CameraPager.NORMAL) {
            viewPager.adapter?.notifyDataSetChanged()
        }
    }

    /*Turn on or off manual flash*/
    override fun manualFlash(viewPager: ViewPager) {
        /*NormalCamera Fragment*/
        if (viewPager.currentItem == Constants.CameraPager.NORMAL) {
            val normalFrag: NormalFragment = viewPager.adapter?.instantiateItem(viewPager, Constants.CameraPager.NORMAL) as NormalFragment
            normalFrag.manualFlashSelection()
        }
    }

    /*Start PreviewActivity*/
    override fun startPreviewActivity(imageFilePath: String?) {
        val intent = Intent(mView.getContext(), PreviewActivity::class.java)
        intent.putExtra(Constants.CameraIntents.IMAGE_FILE_PATH, imageFilePath)
        mView.getContext()?.startActivity(intent)
    }

}