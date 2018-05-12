package com.phdlabs.sungwon.a8chat_android.structure.camera.fragments.normal

import android.os.Bundle
import android.view.*
import android.widget.RelativeLayout
import com.otaliastudios.cameraview.*
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.structure.camera.CameraActivity
import com.phdlabs.sungwon.a8chat_android.structure.camera.fragments.CameraBaseFragment
import com.phdlabs.sungwon.a8chat_android.structure.camera.result.ResultHolder


/**
 * Created by JPAM on 12/28/17.
 * [NormalFragment] takes still picture with continuous focus mode
 */
class NormalFragment : CameraBaseFragment() {

    /*Layout*/
    override fun cameraLayoutId(): Int = R.layout.fragment_camera_normal

    /*Properties*/
    private var normalCamera: CameraView? = null
    private var wasPictureTaken: Boolean = false
    private var mRelativeLayout: RelativeLayout? = null

    /**
     * Companion
     * */
    companion object {
        /*instances*/
        /**
         * Default constructor
         * Should only be used inside Normal Fragment companion object & pager adapter
         * [create]
         * @return [NormalFragment]
         * */
        fun create(): NormalFragment = NormalFragment()
    }

    /*LifeCycle*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userVisibleHint = false
    }

    override fun inOnCreateView(root: View?, container: ViewGroup?, savedInstanceState: Bundle?) {
        //If something needs to be added to the custom layout
        mRelativeLayout = root!!.findViewById(R.id.fcn_camera_view_container)
    }

    /**
     * [setCameraLayout]
     * Normal camera setup , layout management & camera preview start
     * */
    private fun setCameraLayout() {
        normalCamera = activity?.mCameraView
        normalCamera?.layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT)
        setupCamera(normalCamera)
        if (normalCamera?.parent != null) {
            val viewGroup: ViewGroup = normalCamera?.parent as ViewGroup
            viewGroup.removeView(normalCamera)
        }
        mRelativeLayout?.addView(normalCamera)
        normalCamera?.start()
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
            setCameraLayout()
        } else {
            userVisibleHint = false
            normalCamera?.stop()
        }
    }

    override fun onResume() {
        super.onResume()
        setCameraLayout()
    }

    override fun onPause() {
        super.onPause()
        //Flag
        wasPictureTaken = false
        //Camera View
        normalCamera?.stop()
    }

    /**
     * [setupCamera]
     * Setup Normal Camera for pictures
     * */
    private fun setupCamera(cameraView: CameraView?) {
        //Camera View setup
        cameraView?.keepScreenOn = true
        cameraView?.audio = Audio.OFF
        cameraView?.cropOutput = false
        cameraView?.facing = Facing.BACK
        cameraView?.flash = Flash.OFF
        cameraView?.grid = Grid.OFF
        cameraView?.hdr = Hdr.OFF
        cameraView?.jpegQuality = 100
        cameraView?.playSounds = true
        cameraView?.sessionType = SessionType.PICTURE
        cameraView?.whiteBalance = WhiteBalance.AUTO
        //Gesture control
        normalCamera?.mapGesture(Gesture.PINCH, GestureAction.ZOOM)
        normalCamera?.mapGesture(Gesture.TAP, GestureAction.FOCUS_WITH_MARKER)
    }

    /*Camera Event Handling*/
    fun takePicture() {
        normalCamera?.addCameraListener(object : CameraListener() {
            override fun onPictureTaken(jpeg: ByteArray?) {
                jpeg?.let {
                    if (!wasPictureTaken) {
                        imageCaptured(jpeg)
                        wasPictureTaken = true
                    }
                }
            }
        })
        normalCamera?.capturePicture()
    }

    /*Image caching*/
    private fun imageCaptured(image: ByteArray) {
        //TODO: Rotate selfie before setting result image
        //Result Callback
        val callbackTime = System.currentTimeMillis()
        ResultHolder.dispose()
        ResultHolder.setResultImage(image)
        ResultHolder.setResultTimeToCallback(callbackTime)
        //Transition to editing activity
        val act = activity as CameraActivity
        act.getImageFilePath(null)
    }

    /*Camera Facing control*/
    fun flipCamera() {
        if (normalCamera?.facing == Facing.BACK) {
            normalCamera?.facing = Facing.FRONT
        } else {
            normalCamera?.facing = Facing.BACK
        }
    }

    /*Flash Control*/
    fun manualFlashSelection() {
        if (normalCamera?.flash == Flash.OFF) {
            normalCamera?.flash = Flash.ON
        } else {
            normalCamera?.flash = Flash.OFF
        }
    }


}
