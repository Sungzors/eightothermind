package com.phdlabs.sungwon.a8chat_android.structure.camera.fragments

import android.os.Bundle
import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreFragment
import javax.annotation.Nullable

/**
 * Created by paix on 12/28/17.
 */
abstract class CameraBaseFragment() : CoreFragment() {

    /*Root View*/
    private var mRoot: View? = null

    /*Layout*/
    @LayoutRes
    protected abstract fun cameraLayoutId(): Int

    override fun layoutId(): Int = cameraLayoutId()

    /*Container*/
    protected abstract fun inOnCreateView(root: View?, @Nullable container: ViewGroup?, @Nullable savedInstanceState: Bundle?)

    /*Root View management*/
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mRoot = inflater.inflate(cameraLayoutId(), container, false)
        inOnCreateView(mRoot, container, savedInstanceState)
        mRoot?.let {
            return it
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

}