package com.phdlabs.sungwon.a8chat_android.structure.camera.fragments.handsFree

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.RelativeLayout
import com.otaliastudios.cameraview.*
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.structure.camera.fragments.CameraBaseFragment
import com.phdlabs.sungwon.a8chat_android.structure.camera.result.ResultHolder
import com.phdlabs.sungwon.a8chat_android.structure.camera.videoPreview.VideoPreviewActivity
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import com.phdlabs.sungwon.a8chat_android.utility.camera.ImageUtils
import java.io.File

/**
 * Created by JPAM on 12/28/17.
 * [HandsFreeFragment] records video on a single tap
 */
class HandsFreeFragment : CameraBaseFragment() {

    /*Required*/
    override fun cameraLayoutId(): Int = R.layout.fragment_camera_handsfree

    /*Properties*/
    var handsFreeCamera: CameraView? = null
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
        mRelativeLayout = root!!.findViewById(R.id.fch_camera_view_container)
    }

    /**
     * [setVideoLayout]
     * HandsFree camera setup , layout management & camera preview start
     * */
    private fun setVideoLayout() {
        handsFreeCamera = activity?.mCameraView
        handsFreeCamera?.layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT)
        setupVideoRecording(handsFreeCamera)
        if (handsFreeCamera?.parent != null) {
            val viewGroup: ViewGroup = handsFreeCamera?.parent as ViewGroup
            viewGroup.removeView(handsFreeCamera)
        }
        mRelativeLayout?.addView(handsFreeCamera)
        handsFreeCamera?.start()
    }

    /**
     * Should only show layout after fragment is visible
     * Showing Surface Views if screen is not visible to the user
     * consumes too much memory
     * */
    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        if (menuVisible && isResumed) {
            userVisibleHint = true
            setVideoLayout()
        } else {
            userVisibleHint = false
            handsFreeCamera?.stop()
        }
    }

    override fun onResume() {
        super.onResume()
        if (!userVisibleHint) {
            return
        }
        setVideoLayout()
    }

    override fun onPause() {
        super.onPause()
        //Flag
        wasVideoTaken = false
        //Camera View
        handsFreeCamera?.stop()
    }

    /**
     * [setupVideoRecording]
     * Setup [CameraView] for Video recording
     * */
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
        cameraView?.videoCodec = VideoCodec.H_264
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
        /*Video Result*/
        val callbackTime = System.currentTimeMillis()
        ResultHolder.dispose()
        ResultHolder.setResultVideo(videoFile)
        ResultHolder.setResultTimeToCallback(callbackTime)
        ResultHolder.setResultVideo(videoFile)
        /*Video Preview*/
        activity?.let {
            val intent = Intent(it, VideoPreviewActivity::class.java)
            it.startActivityForResult(intent, Constants.RequestCodes.VIDEO_PREVIEW_REQ_CODE)
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
            handsFreeCamera?.flash = Flash.TORCH
        } else {
            handsFreeCamera?.flash = Flash.OFF
        }
    }

}