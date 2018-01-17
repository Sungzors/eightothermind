package com.phdlabs.sungwon.a8chat_android.structure.camera.fragments.normal

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.*
import android.hardware.camera2.*
import android.hardware.camera2.params.StreamConfigurationMap
import android.media.Image
import android.media.ImageReader
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.HandlerThread
import android.support.v13.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Size
import android.util.SparseIntArray
import android.view.*
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.structure.camera.fragments.CameraBaseFragment
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import com.phdlabs.sungwon.a8chat_android.utility.camera.CameraControl
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit
import kotlin.Comparator
import kotlin.collections.ArrayList

/**
 * Created by paix on 12/28/17.
 * Camera Preview in Camera-Normal-Fragment with Camera API 2
 */
class NormalFragment : CameraBaseFragment() {

    /**
     * Camera Properties
     * */
    /**ID of the current [CameraDevice]
     */
    private lateinit var mCameraId: String

    /**[CameraCaptureSession] for camera preview
     */
    private var mCaptureSession: CameraCaptureSession? = null

    /**Reference to the opened [CameraDevice]
     */
    private var mCameraDevice: CameraDevice? = null

    /**The [android.uitl.size] of the camera preview
     */
    private lateinit var mPreviewSize: Size

    /**
     * Image Properties
     * */
    /*Additional thread to avoid blocking the UI*/
    private var mBackgroundThread: HandlerThread? = null

    /** A [Handler] for running tasks in the background
     */
    private var mBackgroundHandler: Handler? = null

    /**The [ImageReader] that handles still image capturing
     */
    private var mImageReader: ImageReader? = null

    /**Picture output [File]
     */
    private lateinit var mFile: File

    /** [CaptureRequest.Builder] for the camera preview
     * */
    private lateinit var mPreviewRequestBuilder: CaptureRequest.Builder

    /** [CaptureRequest] generated by [mPreviewCaptureRequestBuilder]
     * */
    private lateinit var mPreviewRequest: CaptureRequest

    /**The current state of the camera for taking pictures
     * @see mCaptureCallback
     * */
    var mState: Int = STATE_PREVIEW

    /** A [Semaphore] to prevent the app from exiting before closing the camera
     */
    private val mCameraOpenCloseLock: Semaphore = Semaphore(1)

    /*Whether the current camera supports flash or not */
    private var mFlashSupported: Boolean = false

    /*Orientation for the caemra sensor*/
    private var mSensorOrientation: Int = 0

    /**
     * An [AutoFitTextureView] for camera preview.
     */
    private lateinit var mTextureView: AutoFitTextureView

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.let {
            //Permissions
            if (ContextCompat.checkSelfPermission(it, Constants.AppPermissions.WRITE_EXTERNAL) != PackageManager.PERMISSION_GRANTED) {
                requestExternalStoragePermissions()
            } else {
                mFile = CameraControl.instance.temporaryFile()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        startBackgroundThread()
        /*When screen is turned on & off -> SurfaceTexture is already available & will not be
        * called. We can open a camera and start preview from [onResume] || we wait until the surface
        * is ready in the surfaceTextureListener*/
        if (mTextureView.isAvailable) {
            openCamera(mTextureView.width, mTextureView.height)
        } else {
            mTextureView.surfaceTextureListener = mSurfaceTextureListener
        }
    }

    override fun onPause() {
        closeCamera()
        stopBackgroundThread()
        super.onPause()
    }

    /*Layout*/
    override fun cameraLayoutId(): Int = R.layout.fragment_cameranormal

    override fun inOnCreateView(root: View?, container: ViewGroup?, savedInstanceState: Bundle?) {
        //If something needs to be added to the custom layout
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
        } else if (requestCode == Constants.PermissionsReqCode.WRITE_EXTERNAL_REQ_CODE) {
            if (grantResults.size != 1 || grantResults.get(0) != PackageManager.PERMISSION_GRANTED) {
                showError(getString(R.string.request_write_external_permission))
            } else {
                mFile = CameraControl.instance.temporaryFile()
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
                                      maxWidth: Int, maxHeight: Int, aspectRatio: Size): Size {
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

        override fun onOpened(p0: CameraDevice) {
            mCameraOpenCloseLock.release()
            this@NormalFragment.mCameraDevice = p0
            createCameraPreviewSession()
        }

        override fun onDisconnected(p0: CameraDevice) {
            mCameraOpenCloseLock.release()
            p0.close()
            this@NormalFragment.mCameraDevice = null
        }

        override fun onError(p0: CameraDevice, p1: Int) {
            onDisconnected(p0)
            this@NormalFragment.activity?.finish()
        }

    }

    /**
     * [mOnImageAvailableListener]
     * Callback object for the [ImageReader] which is called when a still image is ready to be saved
     * */
    private val mOnImageAvailableListener = ImageReader.OnImageAvailableListener {
        mBackgroundHandler?.post(ImageSaver(it.acquireNextImage(), mFile))
    }

    /**
     * [CameraCaptureSession.CaptureCallback] handling the events related to the JPEG capture
     * */

    private val mCaptureCallback = object : CameraCaptureSession.CaptureCallback() {

        fun process(captureResult: CaptureResult?) {
            when (mState) {

                STATE_PREVIEW -> Unit

                STATE_WAITING_LOCK -> {
                    /*AutoFocus -> can be null on some devices*/
                    val afState: Int? = captureResult?.get(CaptureResult.CONTROL_AE_STATE)
                    if (afState == null) {
                        captureStillPicture()
                    } else if (afState == CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED
                            || afState == CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED
                            || afState == CaptureResult.CONTROL_AF_STATE_PASSIVE_FOCUSED) {

                        val aeState = captureResult.get(CaptureResult.CONTROL_AE_STATE)

                        if (aeState == null || aeState == CaptureResult.CONTROL_AE_STATE_CONVERGED) {
                            mState = STATE_PICTURE_TAKEN
                            captureStillPicture()

                        } else {
                            runPrecaptureSequence()
                        }
                    }
                }

                STATE_WAITING_PRECAPTURE -> {
                    /*AutoExposure -> can be null on some devices*/
                    val aeState = captureResult?.get(CaptureResult.CONTROL_AE_STATE)
                    if (aeState == null ||
                            aeState == CaptureResult.CONTROL_AE_STATE_PRECAPTURE ||
                            aeState == CaptureRequest.CONTROL_AE_STATE_FLASH_REQUIRED) {
                        mState = STATE_WAITING_NON_PRECAPTURE
                    }
                }

                STATE_WAITING_NON_PRECAPTURE -> {
                    /*AutoFocus -> can be null on some devices*/
                    val aeState = captureResult?.get(CaptureResult.CONTROL_AE_STATE)
                    if (aeState == null || aeState != CaptureResult.CONTROL_AE_STATE_PRECAPTURE) {
                        mState = STATE_PICTURE_TAKEN
                        captureStillPicture()
                    }
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
                requestPermissions(whatPermissions, Constants.PermissionsReqCode.CAMERA_REQ_CODE)
            }
        }
    }

    /**External storage permissions
     * Request external storage permissions
     * */
    private fun requestExternalStoragePermissions() {
        //Required permissions
        val whatPermissions = arrayOf(Constants.AppPermissions.WRITE_EXTERNAL)
        context?.let {
            //Request Permissions
            if (ContextCompat.checkSelfPermission(it, whatPermissions.get(0)) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(whatPermissions, Constants.PermissionsReqCode.WRITE_EXTERNAL_REQ_CODE)

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
                        ImageFormat.JPEG, /*maxImages*/ 2).apply {
                    setOnImageAvailableListener(mOnImageAvailableListener, mBackgroundHandler)
                }

                /* Find out if we need to swap dimension to get the preview size relative to sensor coordinate.*/
                val displayRotation = activity?.windowManager?.defaultDisplay?.rotation
                val sensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION)
                var swappedDimensions = false
                when (displayRotation) {
                    Surface.ROTATION_0 -> {/*Do nothing*/
                    }
                    Surface.ROTATION_180 -> if (sensorOrientation == 90 || sensorOrientation == 270) {
                        swappedDimensions = true
                    }
                    Surface.ROTATION_90 -> {/*Do Nothing*/
                    }
                    Surface.ROTATION_270 -> if (sensorOrientation == 0 || sensorOrientation == 180) {
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

                // We fit the aspect ratio of TextureView to the size of preview we picked.
                val orientation = resources.configuration.orientation
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    mTextureView.setAspectRatio(
                            mPreviewSize.width, mPreviewSize.height)
                } else {
                    mTextureView.setAspectRatio(
                            mPreviewSize.height, mPreviewSize.width)
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
                mCameraOpenCloseLock.let {
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
            mCameraOpenCloseLock.acquire()
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
            mCameraOpenCloseLock.release()
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

            val rotation: Int = it.windowManager.defaultDisplay.rotation
            val matrix = Matrix()
            val viewRect = RectF(0.toFloat(), 0.toFloat(), width.toFloat(), height.toFloat())
            val bufferRect = RectF(0.toFloat(), 0.toFloat(), mPreviewSize.height.toFloat(), mPreviewSize.width.toFloat())
            val centerX: Float = viewRect.centerX()
            val centerY: Float = viewRect.centerY()

            when (rotation) {
                Surface.ROTATION_90 -> {
                    bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY())
                    val scale = Math.max(
                            height.toFloat() / mPreviewSize.height,
                            width.toFloat() / mPreviewSize.width
                    )
                    with(matrix) {
                        setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL)
                        postScale(scale, scale, centerX, centerY)
                        postRotate((90 * (rotation - 2)).toFloat(), centerX, centerY)
                    }
                }
                Surface.ROTATION_270 -> {
                    bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY())
                    val scale = Math.max(
                            height.toFloat() / mPreviewSize.height,
                            width.toFloat() / mPreviewSize.width
                    )
                    with(matrix) {
                        setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL)
                        postScale(scale, scale, centerX, centerY)
                        postRotate((90 * (rotation - 2)).toFloat(), centerX, centerY)
                    }
                }
                Surface.ROTATION_180 -> {
                    matrix.postRotate(180f, centerX, centerY)
                }
                else -> {/*Do nothing*/
                }
            }
            mTextureView.setTransform(matrix)
        }
    }


    /**
     * Create a new
     * @see CameraCaptureSession for camera preview
     * [createCameraPreviewSession]
     * */
    private fun createCameraPreviewSession() {
        try {
            val texture: SurfaceTexture = mTextureView.surfaceTexture
            //Configure the size of default buffer to be the size of camera preview we need
            mPreviewSize.let {
                texture.setDefaultBufferSize(it.width, it.height)
            }
            //Output surface we need to start preview
            val surface: Surface = Surface(texture)

            //Setup CaptureRequest.Builder with the output surface
            mCameraDevice?.let {
                mPreviewRequestBuilder = it.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            }
            mPreviewRequestBuilder.addTarget(surface)

            //Create CameraCaptureSession for camera preview
            mCameraDevice?.createCaptureSession(Arrays.asList(surface, mImageReader?.surface),
                    object : CameraCaptureSession.StateCallback() {

                        override fun onConfigureFailed(p0: CameraCaptureSession?) {
                            //TODO: Remove after testing -> Debug log
                            showToast("Camera Failed")
                        }

                        override fun onConfigured(p0: CameraCaptureSession?) {
                            //Camera is closed
                            if (mCameraDevice == null) return

                            //When the session is ready, start displaying the preview
                            mCaptureSession = p0

                            try {
                                //Auto-focus should be continuous for camera preview
                                //TODO: Auto-focus for video
                                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                                        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
                                setAutoFlash(mPreviewRequestBuilder)
                                //Start displaying the camera preview
                                mPreviewRequest = mPreviewRequestBuilder.build()
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
     * Set automatic flash
     * [setAutoFlahs]
     * @param CaptureRequest.Builder
     * */
    private fun setAutoFlash(requestBuilder: CaptureRequest.Builder?) {
        mFlashSupported.let {
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
    fun takePicture() {
        lockFocus()
    }

    /**
     * Lock focus as the first step for a still image capture
     * [lockFocus]
     * */
    private fun lockFocus() {
        try {
            //Lock Focus of the camera
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                    CaptureRequest.CONTROL_AF_TRIGGER_START)
            //Wait for the lock
            mState = STATE_WAITING_LOCK
            mCaptureSession?.capture(mPreviewRequestBuilder.build(), mCaptureCallback, mBackgroundHandler)
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
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER,
                    CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START)
            //Wait for pre-capture sequence to be set
            mState = STATE_WAITING_PRECAPTURE
            mCaptureSession?.capture(mPreviewRequestBuilder.build(), mCaptureCallback, mBackgroundHandler)
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
                if (mCameraDevice == null) return

                //Orientation
                val rotation = it.windowManager.defaultDisplay.rotation

                //The CaptureRequest.Builder used to take a picture
                val captureBuilder: CaptureRequest.Builder? = mCameraDevice?.
                        createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)?.apply {
                    addTarget(mImageReader?.surface)
                    /*Sensor orientation is 90 for most devices, or 270 for some devices*/
                    val rot = (ORIENTATIONS.get(rotation) + mSensorOrientation + 270) % 360
                    set(CaptureRequest.JPEG_ORIENTATION,
                            rot)
                    set(CaptureRequest.CONTROL_AF_MODE,
                            CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
                }?.also { setAutoFlash(it) }

                //Wait for capture
                val captureCallback = object : CameraCaptureSession.CaptureCallback() {

                    override fun onCaptureCompleted(session: CameraCaptureSession,
                                                    request: CaptureRequest,
                                                    result: TotalCaptureResult) {
                        /*Get file path to start preview activity*/
                        mFile.let {
                            /*Compress File*/
                            val displaySize: Pair<Int, Int> = CameraControl.instance.getScreenSize(activity!!)
                            val compressedFile = File(CameraControl.instance.compressFile(mFile.absolutePath, displaySize.first, displaySize.second))
                            activity?.getImageFilePath(compressedFile.absolutePath)
                        }
                        unlockFocus()
                    }
                }

                mCaptureSession?.apply {
                    stopRepeating()
                    abortCaptures()
                    capture(captureBuilder?.build(), captureCallback, null)
                }

            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    /**
     * Unlock focus
     * [unlockFocus] should be called when still image capture sequence is finished
     * */
    private fun unlockFocus() {
        try {
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                    CameraMetadata.CONTROL_AF_TRIGGER_CANCEL)
            setAutoFlash(mPreviewRequestBuilder)
            mCaptureSession?.capture(mPreviewRequestBuilder.build(), mCaptureCallback, mBackgroundHandler)
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
                output = FileOutputStream(mFile).apply {
                    write(bytes)
                }

            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                mImage.close()
                output?.let {
                    try {
                        it.close()
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
        mBackgroundThread = HandlerThread("CameraBackground").also { it.start() }
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
