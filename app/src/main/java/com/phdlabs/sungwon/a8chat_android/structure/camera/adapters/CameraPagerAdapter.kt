package com.phdlabs.sungwon.a8chat_android.structure.camera.adapters

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.structure.camera.fragments.CameraHandsFreeFragment
import com.phdlabs.sungwon.a8chat_android.structure.camera.fragments.CameraNormalFragment
import com.phdlabs.sungwon.a8chat_android.structure.camera.fragments.CameraRollFragment
import com.phdlabs.sungwon.a8chat_android.structure.camera.fragments.EmptyFragment
import com.phdlabs.sungwon.a8chat_android.utility.Constants

/**
 * Created by paix on 12/28/17.
 * Pager Adapter controlling CameraRoll (left) - Normal (center) - HandsFree (right)
 */
class CameraPagerAdapter(fm: FragmentManager, val context: Context): FragmentPagerAdapter(fm) {



    override fun getItem(position: Int): Fragment {
        /*Return desired camera fragment*/
        when(position) {
            Constants.CameraPager.CAMERA_ROLL -> return CameraRollFragment.create()
            Constants.CameraPager.NORMAL -> return CameraNormalFragment.create()
            Constants.CameraPager.HANDS_FREE -> return CameraHandsFreeFragment.create()
            else -> {
                return EmptyFragment.create()
            }
        }
    }

    /*Camera fragment count*/
    override fun getCount(): Int = 3

    /*Page title*/
    override fun getPageTitle(position: Int): CharSequence? {
        when(position) {
            Constants.CameraPager.CAMERA_ROLL -> return context.getString(R.string.camera_left_tab)
            Constants.CameraPager.NORMAL -> return context.getString(R.string.camera_center_tab)
            Constants.CameraPager.HANDS_FREE -> return context.getString(R.string.camera_right_tab)
            else -> {
                return ""
            }
        }
    }

}