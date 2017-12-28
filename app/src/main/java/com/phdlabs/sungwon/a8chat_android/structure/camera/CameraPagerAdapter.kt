package com.phdlabs.sungwon.a8chat_android.structure.camera

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.phdlabs.sungwon.a8chat_android.structure.camera.fragments.CameraHandsFreeFragment
import com.phdlabs.sungwon.a8chat_android.structure.camera.fragments.CameraNormalFragment
import com.phdlabs.sungwon.a8chat_android.structure.camera.fragments.CameraRollFragment
import com.phdlabs.sungwon.a8chat_android.structure.camera.fragments.EmptyFragment

/**
 * Created by paix on 12/28/17.
 */
class CameraPagerAdapter(fm: FragmentManager): FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        /*Return desired camera fragment*/
        when(position) {
            0 -> return CameraRollFragment.create()
            1 -> return CameraNormalFragment.create()
            2 -> return CameraHandsFreeFragment.create()
            else -> {
                return EmptyFragment.create()
            }
        }
    }

    /*Camera fragment count*/
    override fun getCount(): Int = 3

}