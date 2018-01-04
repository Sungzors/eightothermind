package com.phdlabs.sungwon.a8chat_android.structure.camera.cameraControl

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import com.phdlabs.sungwon.a8chat_android.R
import kotlinx.android.synthetic.main.view_camera_control_tabs.view.*

/**
 * Created by paix on 12/29/17.
 * Custom FrameLayout for camera controls
 */
class CameraControlView : FrameLayout, View.OnTouchListener {

    /*Override constructors with this() instead of super to actually call the last constructor with super
    * This will allow us to inflate it with our own layout*/
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    fun init() {
        /*Inflate FrameLayout with custom view*/
        LayoutInflater.from(context).inflate(R.layout.view_camera_control_tabs, this)
        /*Touch Events*/
        iv_camera_action.setOnTouchListener(this)
        iv_camera_flash.setOnTouchListener(this)
        iv_camera_flip.setOnTouchListener(this)
    }

    override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
        when(p0){
            iv_camera_action -> {
                Toast.makeText(context, "Take Picture", Toast.LENGTH_SHORT).show()
                return true
            }
            iv_camera_flash -> {
                Toast.makeText(context, "Setup Flash", Toast.LENGTH_SHORT).show()
                return true
            }
            iv_camera_flip -> {
                Toast.makeText(context, "Flip Camera", Toast.LENGTH_SHORT).show()
                return true
            }
        }
        return false
    }

}