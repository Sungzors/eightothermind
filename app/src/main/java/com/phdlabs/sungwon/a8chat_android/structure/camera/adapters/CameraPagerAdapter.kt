package com.phdlabs.sungwon.a8chat_android.structure.camera.adapters

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.PagerAdapter
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
class CameraPagerAdapter(val fm: FragmentManager, val context: Context) : FragmentPagerAdapter(fm) {

    /*Properties*/
    var normalFragment: NormalFragment = NormalFragment.create()

    /*Return desired camera fragment*/
    override fun getItem(position: Int): Fragment =
            when (position) {
                Constants.CameraPager.CAMERA_ROLL ->
                    CameraRollFragment.create()

                Constants.CameraPager.NORMAL ->
                    normalFragment

                Constants.CameraPager.HANDS_FREE ->
                    HandsFreeFragment.create()

                else -> {
                    EmptyFragment.create()
                }
            }

    /*Only reaload fragment if it's a NormalFragment instance*/
    override fun getItemPosition(`object`: Any): Int {
        when (`object`) {
            is NormalFragment -> {
                normalFragment.flipCamera()
                return PagerAdapter.POSITION_NONE
            }
            else -> {
                return PagerAdapter.POSITION_UNCHANGED
            }
        }
    }

    /**Camera fragment count
     * [CameraRollFragment]
     * [NormalFragment]
     * [HandsFreeFragment]
     * */
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