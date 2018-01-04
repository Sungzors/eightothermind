package com.phdlabs.sungwon.a8chat_android.structure.camera.fragments.cameraRoll

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.structure.camera.fragments.CameraBaseFragment


/**
 * Created by paix on 12/28/17.
 */
class CameraRollFragment: CameraBaseFragment() {

    /*Companion*/
    companion object {
        fun create(): CameraRollFragment {
            return CameraRollFragment()
        }
    }

    /*Required*/
    override fun cameraLayoutId(): Int = R.layout.fragment_cameraroll

    override fun inOnCreateView(root: View?, container: ViewGroup?, savedInstanceState: Bundle?) {
    }
}