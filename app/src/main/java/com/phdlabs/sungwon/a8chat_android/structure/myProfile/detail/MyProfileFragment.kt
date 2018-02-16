package com.phdlabs.sungwon.a8chat_android.structure.myProfile.detail

import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreFragment

/**
 * Created by paix on 2/16/18.
 * [MyProfileFragment]
 * managed from MainActivity Tab Bar
 */
class MyProfileFragment: CoreFragment() {

    /*Layout*/
    override fun layoutId(): Int = R.layout.fragment_my_profile

    /*Companion*/
    companion object {
       fun newInstance() = MyProfileFragment()
    }

}