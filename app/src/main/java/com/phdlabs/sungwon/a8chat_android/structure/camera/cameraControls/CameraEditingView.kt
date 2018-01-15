package com.phdlabs.sungwon.a8chat_android.structure.camera.cameraControls

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.phdlabs.sungwon.a8chat_android.R

/**
 * Created by paix on 1/14/18.
 * Custom [FrameLayout] for camera-photo editing controls

 */
class CameraEditingView : FrameLayout {

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
        LayoutInflater.from(context).inflate(R.layout.view_camera_control_editing, this)

    }

}