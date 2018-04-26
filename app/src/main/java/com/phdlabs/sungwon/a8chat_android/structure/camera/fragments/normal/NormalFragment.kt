package com.phdlabs.sungwon.a8chat_android.structure.camera.fragments.normal

import android.os.Bundle
import android.view.*
import com.otaliastudios.cameraview.*
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.structure.camera.CameraActivity
import com.phdlabs.sungwon.a8chat_android.structure.camera.fragments.CameraBaseFragment
import com.phdlabs.sungwon.a8chat_android.structure.camera.result.ResultHolder


/**
 * Created by JPAM on 12/28/17.
 * Camera Preview in Camera-Normal-Fragment with Camera API 2
 */
class NormalFragment : CameraBaseFragment() {


    /*Layout*/
    override fun cameraLayoutId(): Int = R.layout.fragment_cameranormal

    /*Properties*/
    private lateinit var normalCamera: CameraView
    private var wasPictureTaken: Boolean = false

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
    override fun inOnCreateView(root: View?, container: ViewGroup?, savedInstanceState: Bundle?) {
        //If something needs to be added to the custom layout
        normalCamera = root!!.findViewById(R.id.fcn_cameraView)
        normalCamera.mapGesture(Gesture.PINCH, GestureAction.ZOOM)
        normalCamera.mapGesture(Gesture.TAP, GestureAction.FOCUS_WITH_MARKER)
    }


    override fun onResume() {
        super.onResume()
        normalCamera.start()

    }

    override fun onPause() {
        super.onPause()
        normalCamera.stop()
        wasPictureTaken = false
    }

    override fun onDestroy() {
        super.onDestroy()
        normalCamera.destroy()
    }

    /*Camera Event Handling*/
    fun takePicture() {
        normalCamera.addCameraListener(object : CameraListener() {
            override fun onPictureTaken(jpeg: ByteArray?) {
                jpeg?.let {
                    if (!wasPictureTaken) {
                        imageCaptured(jpeg)
                        wasPictureTaken = true
                    }
                }
            }
        })
        normalCamera.capturePicture()
    }

    /*Image caching*/
    private fun imageCaptured(image: ByteArray) {
        //Result Callback
        val callbackTime = System.currentTimeMillis()
        ResultHolder.dispose()
        ResultHolder.setResultImage(image)
        ResultHolder.setResultTimeToCallback(callbackTime)

        val act = activity as CameraActivity
        act.getImageFilePath(null)
    }

    /*Camera Facing control*/
    fun flipCamera() {
        if (normalCamera.facing == Facing.BACK) {
            normalCamera.facing = Facing.FRONT
        } else {
            normalCamera.facing = Facing.BACK
        }
    }

    /*Flash Control*/
    fun manualFlashSelection() {
        if (normalCamera.flash == Flash.OFF) {
            normalCamera.flash = Flash.ON
        } else {
            normalCamera.flash = Flash.OFF
        }
    }


}
