package com.phdlabs.sungwon.a8chat_android.structure.camera.fragments.normal

import android.os.Bundle
import android.view.*
import com.camerakit.CameraKitView
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

    override fun inOnCreateView(root: View?, container: ViewGroup?, savedInstanceState: Bundle?) {
        //If something needs to be added to the custom layout
        normalCamera = root!!.findViewById(R.id.fcn_cameraView)
    }

    /*Properties*/
    private lateinit var normalCamera: CameraKitView

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
    override fun onResume() {
        super.onResume()
        normalCamera.onResume()

    }

    override fun onPause() {
        normalCamera.onPause()
        super.onPause()
    }

    /*Camera Event Handling*/
    fun takePicture() {
        normalCamera.captureImage(object : CameraKitView.JpegBytesCallback {
            override fun onImage(p0: CameraKitView?, p1: ByteArray?) {
                p1?.let {
                    imageCaptured(p1)
                }
            }
        })
    }

    /*Image caching*/
    private fun imageCaptured(image: ByteArray) {
        //Result Callback
        val callbackTime = System.currentTimeMillis()
        ResultHolder.dispose()
        ResultHolder.setResultImage(image)
        ResultHolder.setResultTimeToCallback(callbackTime)

        //TODO: Transition to Editing Preview
        val act = activity as CameraActivity
        act.getImageFilePath(null)
    }

    /*Camera Facing control*/
    //TODO: facing options not working
    fun flipCamera() {
        if (normalCamera.facing == CameraKitView.FACING_BACK) {
            normalCamera.facing = CameraKitView.FACING_FRONT
        } else {
            normalCamera.facing = CameraKitView.FACING_BACK
        }
    }

    /*Flash Control*/
    //TODO: flash options not working
    fun manualFlashSelection() {
        if (normalCamera.flash == CameraKitView.FLASH_OFF) {
            normalCamera.flash = CameraKitView.FLASH_ON
        } else {
            normalCamera.flash = CameraKitView.FLASH_ON
        }
    }


}
