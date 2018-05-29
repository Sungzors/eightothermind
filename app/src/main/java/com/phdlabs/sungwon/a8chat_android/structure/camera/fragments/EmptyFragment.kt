package com.phdlabs.sungwon.a8chat_android.structure.camera.fragments

import android.support.v4.app.Fragment

/**
 * Created by JPAM on 12/28/17.
 * Empty Fragment used for Camera Pager Adapter
 * It needs to exist for the pager to exist
 */
class EmptyFragment : Fragment() {

    /*Companion*/
    companion object {
        fun create(): EmptyFragment {
            return EmptyFragment()
        }
    }

}