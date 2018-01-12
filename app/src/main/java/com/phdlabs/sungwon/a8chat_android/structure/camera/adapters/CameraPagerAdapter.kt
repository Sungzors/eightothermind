package com.phdlabs.sungwon.a8chat_android.structure.camera.adapters

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.structure.camera.fragments.handsFree.HandsFreeFragment
import com.phdlabs.sungwon.a8chat_android.structure.camera.fragments.normal.NormalFragment
import com.phdlabs.sungwon.a8chat_android.structure.camera.fragments.cameraRoll.CameraRollFragment
import com.phdlabs.sungwon.a8chat_android.structure.camera.fragments.EmptyFragment
import com.phdlabs.sungwon.a8chat_android.utility.Constants

/**
 * Created by paix on 12/28/17.
 * Pager Adapter controlling CameraRoll (left) - Normal (center) - HandsFree (right)
 */
class CameraPagerAdapter(fm: FragmentManager, val context: Context) : FragmentPagerAdapter(fm) {

    /*Return desired camera fragment*/
    override fun getItem(position: Int): Fragment =
            when (position) {
                Constants.CameraPager.CAMERA_ROLL ->
                    CameraRollFragment.create()

                Constants.CameraPager.NORMAL ->
                    NormalFragment.create()


                Constants.CameraPager.HANDS_FREE ->
                    HandsFreeFragment.create()

                else -> {
                    EmptyFragment.create()
                }
            }

    /*Camera fragment count*/
    override fun getCount(): Int = 3

    /*Page title*/
    override fun getPageTitle(position: Int): CharSequence? =
            when (position) {
                Constants.CameraPager.CAMERA_ROLL -> context.getString(R.string.camera_left_tab)
                Constants.CameraPager.NORMAL -> context.getString(R.string.camera_center_tab)
                Constants.CameraPager.HANDS_FREE -> context.getString(R.string.camera_right_tab)
                else -> {
                    ""
                }
            }

}