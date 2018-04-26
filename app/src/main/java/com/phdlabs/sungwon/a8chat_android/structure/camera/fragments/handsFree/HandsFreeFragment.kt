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
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.structure.camera.cameraView.AutoFitTextureView
import com.phdlabs.sungwon.a8chat_android.structure.camera.cameraView.CompareSizesByArea
import com.phdlabs.sungwon.a8chat_android.structure.camera.fragments.CameraBaseFragment
import com.phdlabs.sungwon.a8chat_android.utility.Constants.AppPermissions.VIDEO_PERMISSIONS
import com.phdlabs.sungwon.a8chat_android.utility.Constants.PermissionsReqCode.REQUEST_VIDEO_PERMISSIONS
import com.phdlabs.sungwon.a8chat_android.utility.dialog.ConfirmationDialog
import com.phdlabs.sungwon.a8chat_android.utility.dialog.ErrorDialog
import kotlinx.android.synthetic.main.fragment_camerahandsfree.*
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

    override fun inOnCreateView(root: View?, container: ViewGroup?, savedInstanceState: Bundle?) {
        //If something needs to be added to the custom layout
        //TODO maybe swap buttons for Normal & Hands Free
    }

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

    //TODO: Setup Video Preview
}