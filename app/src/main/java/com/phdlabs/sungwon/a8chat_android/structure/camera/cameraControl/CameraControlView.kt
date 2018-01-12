package com.phdlabs.sungwon.a8chat_android.structure.camera.cameraControl

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.structure.camera.CameraContract
import com.phdlabs.sungwon.a8chat_android.structure.camera.fragments.normal.NormalFragment
import kotlinx.android.synthetic.main.view_camera_control_tabs.view.*

/**
 * Created by paix on 12/29/17.
 * Custom FrameLayout for camera controls
 */
class CameraControlView : FrameLayout {

    /*Override constructors with this() instead of super to actually call the last constructor with super
    * This will allow us to inflate it with our own layout*/
    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    /*Initialization*/
    fun init() {

        /*Inflate FrameLayout with custom view*/
        LayoutInflater.from(context).inflate(R.layout.view_camera_control_tabs, this)

    }

}