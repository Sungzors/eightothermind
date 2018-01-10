package com.phdlabs.sungwon.a8chat_android.structure.camera.fragments.normal

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.*
import android.hardware.camera2.*
import android.hardware.camera2.params.StreamConfigurationMap
import android.media.Image
import android.media.ImageReader
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.support.v13.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Size
import android.util.SparseIntArray
import android.view.*
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.structure.camera.CameraContract
import com.phdlabs.sungwon.a8chat_android.structure.camera.fragments.CameraBaseFragment
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit
import kotlin.Comparator
import kotlin.collections.ArrayList

/**
 * Created by paix on 12/28/17.
 * Camera Preview in Camera-Normal-Fragment with Camera API 2
 */
class NormalFragment : CameraBaseFragment(), CameraContract.CameraActions {

    /**
     * Camera Properties
     * */
    /*ID of the current camera device*/
    var mCameraId: String? = null
    /*Capture session for camera preview*/
    var mCaptureSession: CameraCaptureSession? = null
    /*Camera device*/
    var mCameraDevice: CameraDevice? = null
    /*Camera peview size*/
    var mPreviewSize: Size? = null

    /**
     * Image Properties
     * */
    /*Additional thread to avoid blocking the UI*/
    var mBackgroundThread: HandlerThread? = null
    /*Handler for running tasks in the background*/
    var mBackgroundHandler: Handler? = null
    /*Image reader that handles still image capturing*/
    var mImageReader: ImageReader? = null
    /*Picture output file*/
    var mFile: File? = null
    /*Capture request builder for the camera preview*/
    var mPreviewRequestBuilder: CaptureRequest.Builder? = null
    /*Capture request generated by [mPreviewCaptureRequestBuilder]*/
    var mPreviewRequest: CaptureRequest? = null
    /*The current state of the camera for taking pictures*/
    var mState: Int = STATE_PREVIEW
    /*Semaphore to prevent the app from exiting before closing the camera*/
    var mCameraOpenCloseLock: Semaphore? = Semaphore(1)
    /*Whether the current camera supports flash or not*/
    var mFlashSupported: Boolean? = null
    /*Orientation for the caemra sensor*/
    var mSensorOrientation: Int? = null
    /**
     * An [AutoFitTextureView] for camera preview.
     */
    private var mTextureView: AutoFitTextureView? = null

    /*LifeCycle*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mFile = File(activity?.getExternalFilesDir(null), "pic.jpg")
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        startBackgroundThread()
        /*When screen is turned on & off -> SurfaceTexture is already available & will not be
        * called. We can open a camera and start preview from [onResume] || we wait until the surface
        * is ready in the surfaceTextureListener*/
        if (mTextureView!!.isAvailable) {
            openCamera(mTextureView!!.width, mTextureView!!.height)
        } else {
            mTextureView!!.surfaceTextureListener = mSurfaceTextureListener
        }
    }

    override fun onPause() {
        super.onPause()
        closeCamera()
        stopBackgroundThread()
    }


    override fun onStop() {
        super.onStop()
    }

    /*Layout*/
    override fun cameraLayoutId(): Int = R.layout.fragment_cameranormal

    override fun inOnCreateView(root: View?, container: ViewGroup?, savedInstanceState: Bundle?) {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mTextureView = findById(R.id.texture) as AutoFitTextureView
    }

    /*Results*/
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == Constants.PermissionsReqCode.CAMERA_REQ_CODE) {
            if (grantResults.size != 1 || grantResults.get(0) != PackageManager.PERMISSION_GRANTED) {
                showError(getString(R.string.request_camera_permission))
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }


    /**
     * Companion
     * */
    companion object {

        /*instance*/
        fun create(): NormalFragment = NormalFragment()

        /*Screen Rotation to JPEG conversion*/
        var ORIENTATIONS: SparseIntArray = SparseIntArray()

        fun addOrientations() {
            ORIENTATIONS.append(Surface.ROTATION_0, 90)
            ORIENTATIONS.append(Surface.ROTATION_90, 0)
            ORIENTATIONS.append(Surface.ROTATION_180, 270)
            ORIENTATIONS.append(Surface.ROTATION_270, 180)
        }

        init {
            /*Screen rotation to JPEG rotation*/
            addOrientations()
        }

        /**
         * Camera States
         * */
        private val STATE_PREVIEW: Int = 0 //Showing camera preview
        private val STATE_WAITING_LOCK: Int = 1 // Waiting for the focus to be locked
        private val STATE_WAITING_PRECAPTURE: Int = 2 // Waiting for the exposure to be in pre-capture state
        private val STATE_WAITING_NON_PRECAPTURE: Int = 3 // Waiting for the exposure state to be anything but pre-capture
        private val STATE_PICTURE_TAKEN: Int = 4 // Picture was taken

        //TODO: Not used for now
        private val MAX_PREVIEW_WIDTH = 1920 // Max preview width guaranteed by camera API-2
        private val MAX_PREVIEW_HEIGHT = 1080 // Max preview height guaranteed by camera API -2


        /**
         * [chooseOptimalSize]
         * Resolutions for the available SurfaceView
         * From the given choices supported by the camera, choose the smallest one that is at least as
         * large as the texture view size, and as large as the respective maximum size. If such size
         * doesn't exist, it will choose the largest one that is at most as large as the respective
         * maximum size, and matches with the aspect ratio of the specified value
         * @param choices
         * @param textureViewWidth
         * @param textureViewHeight
         * @param maxWidth
         * @param maxHeight
         * @param aspectRatio
         * @return optimal size or an arbitrary one if none were big enough
         * *****Important: Second method is used for testing*****
         * */
        private fun chooseOptimalSize(choices: Array<out Size>, textureViewWidth: Int, textureViewHeight: Int,
                                      maxWidth: Int, maxHeight: Int, aspectRatio: Size): Size? {
            //collect supported resolutions that are at least as big as the preview surface
            val bigEnough: ArrayList<Size> = ArrayList<Size>()
            //collect supported resolutions that are smaller than the preview surface
            val smallEnough: ArrayList<Size> = ArrayList<Size>()
            //Width & Height
            val width: Int = aspectRatio.width
            val height: Int = aspectRatio.height
            choices
                    .filter { it.width <= maxWidth && it.height <= maxHeight && it.height == (it.width * height / width) }
                    .forEach {
                        if (it.width >= textureViewWidth &&
                                it.height >= textureViewHeight) {
                            bigEnough.add(it)
                        } else {
                            smallEnough.add(it)
                        }
                    }
            //Pick the smallest for the smallEnough
            if (bigEnough.size > 0) {
                return Collections.min(bigEnough, CompareSizesByArea())
            } else if (smallEnough.size > 0) {
                return Collections.max(smallEnough, CompareSizesByArea())
            } else {
                return choices[0]
            }
        }

        //USED FOR TESTING
        private fun chooseOptimalSize(choices: Array<out Size>, width: Int, height: Int): Size {
            val preferedRatio: Double = (height / width.toDouble())
            var currentOptimalSize = choices[0]
            var currentOptimalRatio: Double = currentOptimalSize.width / currentOptimalSize.height.toDouble()
            for (size in choices) {
                val currentRatio: Double = size.width / size.height.toDouble()
                if (Math.abs(preferedRatio - currentRatio) < Math.abs(preferedRatio - currentOptimalRatio)) {
                    currentOptimalSize = size
                    currentOptimalRatio = currentRatio
                }
            }

            return currentOptimalSize
        }

    }//end companion

    /**
     * Compares two sizes based on their areas
     * [CompareSizesByArea]
     * */
    class CompareSizesByArea : Comparator<Size> {
        override fun compare(p0: Size?, p1: Size?): Int {
            //Cast to avoid overflow with multipliers
            var compare: Int = 0
            p0?.let {
                p1?.let {
                    compare = java.lang.Long.signum(p0.width.toLong() * p0.height -
                            p1.width.toLong() * p1.height)
                }
            }
            return compare
        }

    }

    /**
     * [mSurfaceTextureListener]
     * SurfaceTextureListener for handling lifecycle events on the TextureView
     * @Link TextureView
     * */
    private val mSurfaceTextureListener = object : TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureSizeChanged(p0: SurfaceTexture?, p1: Int, p2: Int) {
            configureTransform(p1, p2) //width, height
        }

        override fun onSurfaceTextureUpdated(p0: SurfaceTexture?) {
        }

        override fun onSurfaceTextureDestroyed(p0: SurfaceTexture?): Boolean = true

        override fun onSurfaceTextureAvailable(p0: SurfaceTexture?, p1: Int, p2: Int) {
            openCamera(p1, p2) //width, height
        }
    }


    /**
     * [mStateCallback]
     * Camera state callback changes when the Camera device changes state
     * */
    private val mStateCallback = object : CameraDevice.StateCallback() {

        override fun onOpened(p0: CameraDevice?) {
            mCameraOpenCloseLock?.release()
            mCameraDevice = p0
            createCameraPreviewSession()
        }

        override fun onDisconnected(p0: CameraDevice?) {
            mCameraOpenCloseLock?.release()
            p0?.close()
            mCameraDevice = null
        }

        override fun onError(p0: CameraDevice?, p1: Int) {
            mCameraOpenCloseLock?.release()
            p0?.close()
            mCameraDevice = null
            activity?.finish()
        }

    }

    /**
     * [mOnImageAvailableListener]
     * Callback object for the [ImageReader] which is called when a still image is ready to be saved
     * */
    private val mOnImageAvailableListener = ImageReader.OnImageAvailableListener { reader ->
        mBackgroundHandler?.post(ImageSaver(reader.acquireNextImage(), mFile!!))
    }

    /**
     * [CameraCaptureSession.CaptureCallback] handling the events related to the JPEG capture
     * */

    private val mCaptureCallback = object : CameraCaptureSession.CaptureCallback() {

        fun process(captureResult: CaptureResult?) {
            when (mState) {

                STATE_PREVIEW -> {
                    /*Do nothing when camera preview is working*/
                    return
                }

                STATE_WAITING_LOCK -> {
                    /*AutoFocus*/
                    val afState: Int? = captureResult?.get(CaptureResult.CONTROL_AF_STATE)
                    afState?.let {
                        if (it == (CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED) ||
                                it == (CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED)) {
                            /*AutoExposure -> Can be null on some devices*/
                            val aeState: Int? = captureResult.get(CaptureResult.CONTROL_AE_STATE)
                            if (aeState == null || aeState == CaptureResult.CONTROL_AE_STATE_CONVERGED) {
                                mState = STATE_PICTURE_TAKEN
                                captureStillPicture()
                            } else {
                                runPrecaptureSequence()
                            }
                        }
                    } ?: run {
                        captureStillPicture()
                    }
                    return
                }

                STATE_WAITING_PRECAPTURE -> {
                    /*AutoFocus -> can be null on some devices*/
                    val aeState: Int? = captureResult?.get(CaptureResult.CONTROL_AE_STATE)
                    if (aeState == null ||
                            aeState == CaptureResult.CONTROL_AE_STATE_PRECAPTURE ||
                            aeState == CaptureRequest.CONTROL_AE_STATE_FLASH_REQUIRED) {
                        mState = STATE_WAITING_NON_PRECAPTURE
                    }
                    return
                }

                STATE_WAITING_NON_PRECAPTURE -> {
                    /*AutoFocus -> can be null on some devices*/
                    val aeState: Int? = captureResult?.get(CaptureResult.CONTROL_AE_STATE)
                    if (aeState == null ||
                            aeState != CaptureResult.CONTROL_AE_STATE_PRECAPTURE) {
                        mState = STATE_PICTURE_TAKEN
                        captureStillPicture()
                    }
                    return
                }

            }
        }

        /*required*/
        override fun onCaptureProgressed(session: CameraCaptureSession?, request: CaptureRequest?, partialResult: CaptureResult?) {
            process(partialResult)
        }

        /*required*/
        override fun onCaptureCompleted(session: CameraCaptureSession?, request: CaptureRequest?, result: TotalCaptureResult?) {
            process(result)
        }

    }

    /**Camera permissions
     * [requestCameraPermissions]
     * //TODO: Add permissions for Audio Recording & Video Recording
     * Request Camera, Audio & Video
     * */
    private fun requestCameraPermissions() {
        //Required permissions
        val whatPermissions = arrayOf(Constants.AppPermissions.CAMERA)
        context?.let {
            //Request Permissions
            if (ContextCompat.checkSelfPermission(it, whatPermissions.get(0)) != PackageManager.PERMISSION_GRANTED) {
                activity?.let {
                    ActivityCompat.requestPermissions(it, whatPermissions, Constants.PermissionsReqCode.CAMERA_REQ_CODE)
                }
            }
        }
    }

    /**Setup member variables for the camera
     * <FullScreen>
     * [setupCameraOutputs]
     * @param width The width of available size for camera preview
     * @param height The height of available size for camera preview
     * */
    private fun setupCameraOutputs(width: Int, height: Int) {

        val manager = activity?.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            for (cameraId in manager.cameraIdList) {
                val characteristics = manager.getCameraCharacteristics(cameraId)

                // We don't use a front facing camera in this sample.
                val facing = characteristics.get(CameraCharacteristics.LENS_FACING)
                if (facing == CameraCharacteristics.LENS_FACING_FRONT) {
                    continue
                }

                val map: StreamConfigurationMap = characteristics.get(
                        CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP) ?: continue

                // For still image captures, we use the largest available size.
                val largest = Collections.max(
                        Arrays.asList(*map.getOutputSizes(ImageFormat.JPEG)),
                        CompareSizesByArea())
                mImageReader = ImageReader.newInstance(largest.width, largest.height,
                        ImageFormat.JPEG, /*maxImages*/2)
                mImageReader?.setOnImageAvailableListener(
                        mOnImageAvailableListener, mBackgroundHandler)

                // Find out if we need to swap dimension to get the preview size relative to sensor
                // coordinate.
                val displayRotation = activity?.windowManager?.defaultDisplay?.rotation
                val sensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION)!!
                var swappedDimensions = false
                when (displayRotation) {
                    Surface.ROTATION_0, Surface.ROTATION_180 -> if (sensorOrientation == 90 || sensorOrientation == 270) {
                        swappedDimensions = true
                    }
                    Surface.ROTATION_90, Surface.ROTATION_270 -> if (sensorOrientation == 0 || sensorOrientation == 180) {
                        swappedDimensions = true
                    }
                }

                val displaySize = Point()
                activity?.windowManager?.defaultDisplay?.getSize(displaySize)

                var rotatedPreviewWidth = width
                var rotatedPreviewHeight = height
                var maxPreviewWidth = displaySize.x
                var maxPreviewHeight = displaySize.y

                if (swappedDimensions) {
                    rotatedPreviewWidth = height
                    rotatedPreviewHeight = width
                    maxPreviewWidth = displaySize.y
                    maxPreviewHeight = displaySize.x
                }

                if (maxPreviewWidth > MAX_PREVIEW_WIDTH) {
                    maxPreviewWidth = MAX_PREVIEW_WIDTH
                }

                if (maxPreviewHeight > MAX_PREVIEW_HEIGHT) {
                    maxPreviewHeight = MAX_PREVIEW_HEIGHT
                }

                /*Danger, W.R.! Attempting to use too large a preview size could  exceed the camera
                bus' bandwidth limitation, resulting in gorgeous previews but the storage of
                garbage capture data.*/

                mPreviewSize = chooseOptimalSize(map.getOutputSizes<SurfaceTexture>(SurfaceTexture::class.java),
                        rotatedPreviewWidth, rotatedPreviewHeight, maxPreviewWidth,
                        maxPreviewHeight, largest)

//                mPreviewSize = chooseOptimalSize(map.getOutputSizes<SurfaceTexture>(SurfaceTexture::class.java), displaySize.x, displaySize.y)

                // We fit the aspect ratio of TextureView to the size of preview we picked.
                val orientation = resources.configuration.orientation
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    mTextureView!!.setAspectRatio(
                            mPreviewSize!!.width, mPreviewSize!!.height)
                } else {
                    mTextureView!!.setAspectRatio(
                            mPreviewSize!!.height, mPreviewSize!!.width)
                }

                mCameraId = cameraId
                return
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        } catch (e: NullPointerException) {
            // Currently an NPE is thrown when the Camera2API is used but not supported on the
            // device this code runs.
            //ErrorDialog.newInstance(getString(R.string.camera_error)).show(childFragmentManager, FRAGMENT_DIALOG)
        }

    }

    /**
     * Opens the camera specified by
     * @see NormalFragment [mCameraId]
     * [openCamera]
     * @param width
     * @param height
     * */
    private fun openCamera(width: Int, height: Int) {
        activity?.let {
            //Permissions
            if (ContextCompat.checkSelfPermission(it, Constants.AppPermissions.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestCameraPermissions()
                return
            }

            setupCameraOutputs(width, height)
            configureTransform(width, height)

            //Open
            val manager = it.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            try {
                mCameraOpenCloseLock?.let {
                    if (!it.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                        throw RuntimeException("Timeout waiting to lock camera opening")
                    }
                    manager.openCamera(mCameraId, mStateCallback, mBackgroundHandler)
                }
            } catch (e: CameraAccessException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                throw RuntimeException("Interrupted while trying to lock camera opening", e)
            }

        }
    }

    /**
     * Close the current [closeCamera]
     * @see CameraDevice
     * */
    private fun closeCamera() {
        try {
            mCameraOpenCloseLock?.acquire()
            mCaptureSession?.let {
                it.close()
                mCaptureSession = null
            }
            mCameraDevice?.let {
                it.close()
                mCameraDevice = null
            }
            mImageReader?.let {
                it.close()
                mImageReader = null
            }
        } catch (e: InterruptedException) {
            throw RuntimeException("Interrupted while trying to lock camera closing", e)
        } finally {
            mCameraOpenCloseLock?.release()
        }
    }


    /**
     * Configures the necessary matrix transformation to
     * @property mTextureView
     * @see android.graphics.Matix
     * [configureTransform]should be called after the camera preview size is determined in
     * [setupCameraOutputs] and also de size of
     * @property mTextureView is fixed.
     *
     * @param viewWidth -> Width of the TextureView
     * @param viewHeight -> Height of the TextureView
     * */
    private fun configureTransform(width: Int, height: Int) {
        activity?.let {
            if (mTextureView == null || mPreviewSize == null) {
                return
            }
            val rotation: Int = it.windowManager.defaultDisplay.rotation
            val matrix: Matrix = Matrix()
            val viewRect: RectF = RectF(0.toFloat(), 0.toFloat(), width.toFloat(), height.toFloat())
            var bufferRect: RectF? = null
            mPreviewSize?.let {
                bufferRect = RectF(0.toFloat(), 0.toFloat(), it.height.toFloat(), it.width.toFloat())
            }
            val centerX: Float = viewRect.centerX()
            val centerY: Float = viewRect.centerY()
            when (rotation) {
                Surface.ROTATION_90 -> {
                    bufferRect?.let {
                        it.offset(centerX - it.centerX(), centerY - it.centerY())
                        matrix.setRectToRect(viewRect, it, Matrix.ScaleToFit.FILL)
                        mPreviewSize?.let {
                            val scale: Float = Math.max(height.toFloat() / it.height,
                                    width.toFloat() / it.width)
                            matrix.postScale(scale, scale, centerX, centerY)
                            matrix.postRotate((90 * (rotation - 2)).toFloat(), centerX, centerY)

                        }
                    }
                }
                Surface.ROTATION_270 -> {
                    bufferRect?.let {
                        it.offset(centerX - it.centerX(), centerY - it.centerY())
                        matrix.setRectToRect(viewRect, it, Matrix.ScaleToFit.FILL)
                        mPreviewSize?.let {
                            val scale: Float = Math.max(height.toFloat() / it.height,
                                    width.toFloat() / it.width)
                            matrix.postScale(scale, scale, centerX, centerY)
                            matrix.postRotate((90 * (rotation - 2)).toFloat(), centerX, centerY)

                        }
                    }
                }
                Surface.ROTATION_180 -> {
                    matrix.postRotate(180.toFloat(), centerX, centerY)
                }
                else -> {/*Do nothing*/
                }
            }
            mTextureView!!.setTransform(matrix)
        }
    }


    /**
     * Create a new
     * @see CameraCaptureSession for camera preview
     * [createCameraPreviewSession]
     * */
    private fun createCameraPreviewSession() {
        try {
            val texture: SurfaceTexture = mTextureView!!.surfaceTexture
            //Configure the size of default buffer to be the size of camera preview we need
            mPreviewSize?.let {
                texture.setDefaultBufferSize(it.width, it.height)
            }
            //Output surface we need to start preview
            val surface: Surface = Surface(texture)
            //Setup CaptureRequest.Builder with the output surface
            mPreviewRequestBuilder = mCameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            mPreviewRequestBuilder?.addTarget(surface)
            //Create CameraCaptureSession for camera preview
            mCameraDevice?.createCaptureSession(Arrays.asList(surface, mImageReader?.surface),
                    object : CameraCaptureSession.StateCallback() {

                        override fun onConfigureFailed(p0: CameraCaptureSession?) {
                            //TODO: Remove after testing -> Debug log
                            showToast("Camera Failed")
                        }

                        override fun onConfigured(p0: CameraCaptureSession?) {
                            //Camera is closed
                            if (mCameraDevice == null) {
                                return
                            }

                            //When the session is ready, start displaying the preview
                            mCaptureSession = p0
                            try {

                                //Auto-focus should be continuous for camera preview
                                //TODO: Auto-focus for video
                                mPreviewRequestBuilder?.set(CaptureRequest.CONTROL_AF_MODE,
                                        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
                                setAutoFlash(mPreviewRequestBuilder)
                                //Start displaying the camera preview
                                mPreviewRequest = mPreviewRequestBuilder?.build()
                                mCaptureSession?.setRepeatingRequest(mPreviewRequest,
                                        mCaptureCallback, mBackgroundHandler)

                            } catch (e: CameraAccessException) {
                                e.printStackTrace()
                            }
                        }
                    }, null)

        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }

    }

    /**
     * //TODO: Handle manual flash
     * Set automatic flash
     * [setAutoFlahs]
     * @param CaptureRequest.Builder
     * */
    private fun setAutoFlash(requestBuilder: CaptureRequest.Builder?) {
        mFlashSupported?.let {
            if (it) {
                requestBuilder?.set(CaptureRequest.CONTROL_AE_MODE,
                        CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH)
            }
        }
    }

    /**
     * Initiate a still Image capture
     * Overridden method from CameraContract.CameraActions
     * [takePicture]
     * */
    override fun takePicture() {
        lockFocus()
    }

    /**
     * Lock focus as the first step for a still image capture
     * [lockFocus]
     * */
    private fun lockFocus() {
        try {
            //Lock Focus of the camera
            mPreviewRequestBuilder?.set(CaptureRequest.CONTROL_AF_TRIGGER,
                    CaptureRequest.CONTROL_AF_TRIGGER_START)
            //Wait for the lock
            mState = STATE_WAITING_LOCK
            mCaptureSession?.capture(mPreviewRequestBuilder?.build(), mCaptureCallback, mBackgroundHandler)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    /**
     * Start the pre-capture sequence for capturing a still image.
     * This method should be called when we get a response in
     * @property mCaptureCallback from
     * [lockFocus] [runPrecaptureSequence]
     * */
    private fun runPrecaptureSequence() {
        try {
            //Trigger Camera (Auto Exposure)
            mPreviewRequestBuilder?.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER,
                    CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START)
            //Wait for precapture sequence to be set
            mState = STATE_WAITING_PRECAPTURE
            mCaptureSession?.capture(mPreviewRequestBuilder?.build(), mCaptureCallback, mBackgroundHandler)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    /**
     * Capture a still picture
     * This method should be called when we get a response in
     * @property mCaptureCallback
     * from
     * [lockFocus] [captureStillPicture]
     * */
    private fun captureStillPicture() {
        try {
            activity?.let {
                if (mCameraDevice == null) {
                    return
                }
                //The CaptureRequest.Builder used to take a picture
                val captureBuilder: CaptureRequest.Builder? = mCameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
                captureBuilder?.addTarget(mImageReader?.surface)
                //Use the same AutoExposure & AutoFocus modes as the preview
                captureBuilder?.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
                setAutoFlash(captureBuilder)
                //Orientation
                val rotation = it.windowManager.defaultDisplay.rotation
                captureBuilder?.set(CaptureRequest.JPEG_ORIENTATION, getOrientation(rotation))
                //Wait for capture
                val captureCallback = object : CameraCaptureSession.CaptureCallback() {
                    override fun onCaptureCompleted(session: CameraCaptureSession?, request: CaptureRequest?, result: TotalCaptureResult?) {
                        showToast("Saved: " + mFile!!)
                        unlockFocus()
                    }
                }
                mCaptureSession?.stopRepeating()
                mCaptureSession?.capture(captureBuilder?.build(), captureCallback, null)
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    /**
     * Retrieve the JPEG orientation from the specified screen rotation
     * @param rotation -> screen rotation
     * @return the JPEG orientation -> one of -> (0,90,270,360)
     * [getOrientation]
     * Sensor orientation is 90 for most devices, or 270 for some devices (Nexus 5X)
     * This is important to rotate the image properly
     * For devices with orientation of 90, we simply return our mapping from ORIENTATIONS
     * For devices with orientation of 270, we need to rotate the JPEG 180 degrees
     * */
    private fun getOrientation(rotation: Int): Int =
            (ORIENTATIONS.get(rotation) + mSensorOrientation!! + 270) % 360

    /**
     * Unlock focus
     * [unlockFocus] should be called when still image capture sequence is finished
     * */
    private fun unlockFocus() {
        try {
            mPreviewRequestBuilder?.set(CaptureRequest.CONTROL_AF_TRIGGER,
                    CameraMetadata.CONTROL_AF_TRIGGER_CANCEL)
            setAutoFlash(mPreviewRequestBuilder)
            mCaptureSession?.capture(mPreviewRequestBuilder?.build(), mCaptureCallback, mBackgroundHandler)
            //Camera goes back to normal state of preview
            mState = STATE_PREVIEW
            mCaptureSession?.setRepeatingRequest(mPreviewRequest, mCaptureCallback, mBackgroundHandler)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    /**Save a JPEG [Image] into the specified [File]*/
    private class ImageSaver(
            /**
             * The JPEG image
             */
            private val mImage: Image,
            /**
             * The file we save the image into.
             */
            private val mFile: File) : Runnable {

        override fun run() {
            val buffer = mImage.planes[0].buffer
            val bytes = ByteArray(buffer.remaining())
            buffer.get(bytes)
            var output: FileOutputStream? = null
            try {
                output = FileOutputStream(mFile)
                output.write(bytes)
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                mImage.close()
                if (null != output) {
                    try {
                        output.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }
            }
        }

    }

    /*Threads*/

    /**
     * Start a background thread with it's
     * @see Handler
     * [startBackgroundThread]
     * */
    private fun startBackgroundThread() {
        mBackgroundThread = HandlerThread("CameraBackground")
        mBackgroundThread?.start()
        mBackgroundHandler = Handler(mBackgroundThread?.looper)
    }

    /**
     * Stops the background thread with it's
     * @see Handler
     * [stopBackgroundThread]
     * */
    private fun stopBackgroundThread() {
        mBackgroundThread?.quitSafely()
        try {
            mBackgroundThread?.join()
            mBackgroundThread = null
            mBackgroundHandler = null
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }


}
