package com.phdlabs.sungwon.a8chat_android.structure.camera.fragments

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.phdlabs.sungwon.a8chat_android.R

/**
 * Created by paix on 12/28/17.
 */
class CameraHandsFreeFragment : CameraBaseFragment() {

    /*Companion*/
    companion object {
        fun create(): CameraHandsFreeFragment {
            return CameraHandsFreeFragment()
        }
    }

    /*Required*/
    override fun cameraLayoutId(): Int = R.layout.fragment_camerahandsfree

    override fun inOnCreateView(root: View?, container: ViewGroup?, savedInstanceState: Bundle?) {
    }

}