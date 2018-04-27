package com.phdlabs.sungwon.a8chat_android.structure.camera.fragments.handsFree

import android.os.Bundle
import android.view.*
import android.widget.RelativeLayout
import com.otaliastudios.cameraview.*
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.structure.camera.fragments.CameraBaseFragment
import com.phdlabs.sungwon.a8chat_android.structure.camera.result.ResultHolder
import com.phdlabs.sungwon.a8chat_android.utility.camera.ImageUtils
import java.io.File

/**
 * Created by JPAM on 12/28/17.
 * [HandsFreeFragment] records video on a single tap
 */
class HandsFreeFragment : CameraBaseFragment() {

    /*Required*/
    override fun cameraLayoutId(): Int = R.layout.fragment_cameraview

    /*Properties*/
    private var handsFreeCamera: CameraView? = null
    private lateinit var mTempFile: File
    private var mRelativeLayout: RelativeLayout? = null
    private var wasVideoTaken: Boolean = false


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
        //Create temporary cached video file
        context?.let {
            mTempFile = ImageUtils.instance.getTemporalFile(it, System.currentTimeMillis().toString())
        }
        userVisibleHint = false
    }

    override fun inOnCreateView(root: View?, container: ViewGroup?, savedInstanceState: Bundle?) {
        //If something needs to be added to the custom layout
        mRelativeLayout = root!!.findViewById(R.id.cameraViewRelativeLayout)
    }

    private fun setVideoLayout() {
        handsFreeCamera = activity?.mCameraView
        handsFreeCamera?.layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT)
        setupVideoRecording(handsFreeCamera)
        mRelativeLayout?.addView(handsFreeCamera)
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        if (menuVisible && isResumed) {
            userVisibleHint = true
            if (mRelativeLayout?.childCount == 0) {
                setVideoLayout()
                handsFreeCamera?.start()
            }
        } else {
            userVisibleHint = false
            handsFreeCamera?.stop()
            mRelativeLayout?.removeAllViews()

        }
    }

    override fun onResume() {
        super.onResume()
        if (!userVisibleHint) {
            return
        }
        if (mRelativeLayout?.childCount == 0) {
            setVideoLayout()
            handsFreeCamera?.start()
        }
    }

    override fun onPause() {
        super.onPause()
        //Flag
        wasVideoTaken = false
        //Camera View
        handsFreeCamera?.stop()
        mRelativeLayout?.removeAllViews()
    }

    /*Functionality*/
    private fun setupVideoRecording(cameraView: CameraView?) {
        //Camera Recording View setup
        cameraView?.keepScreenOn = true
        cameraView?.audio = Audio.ON
        cameraView?.cropOutput = false
        cameraView?.facing = Facing.BACK
        cameraView?.flash = Flash.OFF
        cameraView?.grid = Grid.OFF
        cameraView?.hdr = Hdr.OFF
        cameraView?.playSounds = true
        cameraView?.sessionType = SessionType.VIDEO
        cameraView?.videoCodec = VideoCodec.DEVICE_DEFAULT
        cameraView?.videoMaxDuration = 720000 // 12 min
        cameraView?.videoQuality = VideoQuality.MAX_2160P
        cameraView?.whiteBalance = WhiteBalance.AUTO
        //Gesture Control
        cameraView?.mapGesture(Gesture.PINCH, GestureAction.ZOOM)
        cameraView?.mapGesture(Gesture.TAP, GestureAction.FOCUS_WITH_MARKER)
    }

    fun startVideoRecording() {
        handsFreeCamera?.addCameraListener(object : CameraListener() {
            override fun onVideoTaken(video: File?) {
                video?.let {
                    //Result Callback
                    if (!wasVideoTaken) {
                        videoCaptured(it)
                        wasVideoTaken = true
                    }
                }
            }
        })
        //Start Recording || Stop Recording
        handsFreeCamera?.let {
            if (it.isCapturingVideo) it.stopCapturingVideo() else it.startCapturingVideo(mTempFile)
        }
    }

    fun videoCaptured(videoFile: File) {
        val callbackTime = System.currentTimeMillis()
        ResultHolder.dispose()
        ResultHolder.setResultVideo(videoFile)
        ResultHolder.setResultTimeToCallback(callbackTime)
        //TODO: Start video preview activity
        println("Video file path: ${videoFile.absolutePath}")
    }

    /*Camera Facing control*/
    //FIXME Not working for Android 8+
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
            handsFreeCamera?.flash = Flash.TORCH
        } else {
            handsFreeCamera?.flash = Flash.OFF
        }
    }

}