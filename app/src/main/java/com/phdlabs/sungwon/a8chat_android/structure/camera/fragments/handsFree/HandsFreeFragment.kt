package com.phdlabs.sungwon.a8chat_android.structure.camera.fragments.handsFree

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.content.res.Configuration
import android.graphics.Matrix
import android.graphics.RectF
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.hardware.camera2.CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP
import android.hardware.camera2.CameraCharacteristics.SENSOR_ORIENTATION
import android.hardware.camera2.CameraDevice.TEMPLATE_PREVIEW
import android.hardware.camera2.CameraDevice.TEMPLATE_RECORD
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.support.v4.content.ContextCompat.checkSelfPermission
import android.util.Log
import android.util.Size
import android.util.SparseIntArray
import android.view.*
import android.widget.Button
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import com.otaliastudios.cameraview.*
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.structure.camera.cameraView.AutoFitTextureView
import com.phdlabs.sungwon.a8chat_android.structure.camera.cameraView.CompareSizesByArea
import com.phdlabs.sungwon.a8chat_android.structure.camera.fragments.CameraBaseFragment
import com.phdlabs.sungwon.a8chat_android.structure.camera.result.ResultHolder
import com.phdlabs.sungwon.a8chat_android.utility.Constants.AppPermissions.VIDEO_PERMISSIONS
import com.phdlabs.sungwon.a8chat_android.utility.Constants.PermissionsReqCode.REQUEST_VIDEO_PERMISSIONS
import com.phdlabs.sungwon.a8chat_android.utility.camera.ImageUtils
import com.phdlabs.sungwon.a8chat_android.utility.dialog.ConfirmationDialog
import com.phdlabs.sungwon.a8chat_android.utility.dialog.ErrorDialog
import kotlinx.android.synthetic.main.activity_camera_preview.*
import kotlinx.android.synthetic.main.fragment_camerahandsfree.*
import java.io.File
import java.io.IOException
import java.util.*
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit

/**
 * Created by JPAM on 12/28/17.
 */
class HandsFreeFragment : CameraBaseFragment() {

    /*Required*/
    override fun cameraLayoutId(): Int = R.layout.fragment_camerahandsfree

    /*Properties*/
    private var handsFreeCamera: CameraView? = null
    private lateinit var mTempFile: File

    /*Companion*/
    companion object {
        /**
         * Default constructor
         * Should only be used inside Hands Free Fragment companion object & pager adapter
         * [create]
         * @return [HandsFreeFragment]
         * */
        fun create(): HandsFreeFragment = HandsFreeFragment()
    }

    /*LifeCycle*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context?.let {
            mTempFile = ImageUtils.instance.getTemporalFile(it, System.currentTimeMillis().toString())
        }
        userVisibleHint = false
    }

    override fun inOnCreateView(root: View?, container: ViewGroup?, savedInstanceState: Bundle?) {
        //If something needs to be added to the custom layout
        handsFreeCamera = root!!.findViewById(R.id.fch_cameraView)
        handsFreeCamera?.mapGesture(Gesture.PINCH, GestureAction.ZOOM)
        handsFreeCamera?.mapGesture(Gesture.TAP, GestureAction.FOCUS_WITH_MARKER)
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        if (menuVisible && isResumed) {
            userVisibleHint = true
            handsFreeCamera?.start()
        } else {
            userVisibleHint = false
            handsFreeCamera?.stop()
            handsFreeCamera?.destroy()
        }
    }

    override fun onResume() {
        super.onResume()
        if (!userVisibleHint){
            return
        }
        handsFreeCamera?.start()
    }

    override fun onPause() {
        super.onPause()
        handsFreeCamera?.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        handsFreeCamera?.destroy()
    }

    /*Functionality*/
    fun startVideoRecording() {
        handsFreeCamera?.addCameraListener(object : CameraListener() {
            override fun onVideoTaken(video: File?) {
                video?.let {
                    //Result Callback
                    val callbackTime = System.currentTimeMillis()
                    ResultHolder.dispose()
                    ResultHolder.setResultVideo(it)
                    ResultHolder.setResultTimeToCallback(callbackTime)

                    //TODO: Start video preview activity
                    println("Video file path: ${it.absolutePath}")
                }
            }
        })
        //Start Recording || Stop Recording
        handsFreeCamera?.let {
            if (it.isCapturingVideo) it.stopCapturingVideo() else it.startCapturingVideo(mTempFile)
        }
    }

    /*Camera Facing control*/
    fun flipCamera() {
        if (handsFreeCamera?.facing == Facing.BACK) {
            handsFreeCamera?.facing = Facing.FRONT
        } else {
            handsFreeCamera?.facing = Facing.BACK
        }
    }

    /*Flash Control*/
    fun manualFlashSelection() {
        if (handsFreeCamera?.flash == Flash.OFF) {
            handsFreeCamera?.flash = Flash.ON
        } else {
            handsFreeCamera?.flash = Flash.OFF
        }
    }

}