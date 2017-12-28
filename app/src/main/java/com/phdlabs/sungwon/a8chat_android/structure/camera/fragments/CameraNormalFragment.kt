package com.phdlabs.sungwon.a8chat_android.structure.camera.fragments

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.phdlabs.sungwon.a8chat_android.R

/**
 * Created by paix on 12/28/17.
 */
class CameraNormalFragment : CameraBaseFragment() {

    /*Companion*/
    companion object {
        fun create(): CameraNormalFragment {
            return CameraNormalFragment()
        }
    }

    /*Required*/
    override fun cameraLayoutId(): Int = R.layout.fragment_cameranormal

    override fun inOnCreateView(root: View?, container: ViewGroup?, savedInstanceState: Bundle?) {
    }

}