package com.phdlabs.sungwon.a8chat_android.utility.camera

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.AssetFileDescriptor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.hardware.camera2.CameraCharacteristics
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Parcelable
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.util.DisplayMetrics
import android.util.Log
import com.phdlabs.sungwon.a8chat_android.BuildConfig
import com.phdlabs.sungwon.a8chat_android.R
import kotlinx.android.synthetic.main.activity_camera_preview.*
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by JPAM on 11/8/17.
 * Camera Control written in Kotlin
 * A group of *methods* to control
 * camera & gallery intents & data
 *
 * INFO:
 * _private_constructor_ ensures this class can't be
 * initialized anywhere except inside of this class
 * -Singleton pattern
 *
 */

class CameraControl private constructor() {

    /*Singleton Companion object*/
    companion object {

        private val DEFAULT_REQUEST_CODE = 234

        private val DEFAULT_MIN_WIDTH_QUALITY = 400
        private val DEFAULT_MIN_HEIGHT_QUALITY = 400
        private val TAG = "CameraControlView"
        private val TEMP_IMAGE_NAME = "tempImage"

        private var minWidthQuality = DEFAULT_MIN_WIDTH_QUALITY
        private var minHeightQuality = DEFAULT_MIN_HEIGHT_QUALITY

        private var mChooserTitle: String? = null
        private var mPickImageRequestCode = DEFAULT_REQUEST_CODE
        private var mGalleryOnly = false

        //Singleton INSTANCE
        val instance: CameraControl by lazy { Holder.INSTANCE }

    }

    /*Initializer Log used for testing
     *
     * INFO:
     * init will be called when this class is initialized for
     * the first time (i.e. when calling CameraControlView.INSTANCE)
     * */
    init {
        println("CameraControlView ($this) is a Singleton")
    }

    /**Instance Holder for Singleton - Camera Control
     * val INSTANCE is the initialized variable
     *
     * INFO:
     * Holder object & lazy INSTANCE is used to ensure only one
     * INSTANCE of CameraControlView() is created
     * */
    private object Holder {
        val INSTANCE = CameraControl()
    }

    /**Request Code for Camera Control
     * [requestCode]
     * Returns Request Code - Used for onActivityResult()
     * */
    fun requestCode(): Int = DEFAULT_REQUEST_CODE


    /**
     * Launch a dialog to pick an image from camera/gallery apps with custom request code.
     *
     * @param activity which will launch the dialog.
     * @param requestCode request code that will be returned in result.
     */
    fun pickImage(activity: Activity, requestCode: Int) {
        pickImage(activity, activity.getString(R.string.pick_image_intent_text), requestCode, false)
    }

    /**
     * Launch a dialog to pick an image from camera/gallery apps with custom request code.
     *
     * @param activity which will launch the dialog.
     */
    fun pickImage(activity: Activity) {
        pickImage(activity, activity.getString(R.string.pick_image_intent_text), DEFAULT_REQUEST_CODE, false)
    }

    /**
     * Launch a dialog to pick an image from camera/gallery apps with custom request code.
     *
     * @param fragment which will launch the dialog.
     * @param requestCode request code that will be returned in result.
     */
    fun pickImage(fragment: Fragment, requestCode: Int) {
        pickImage(fragment, fragment.getString(R.string.pick_image_intent_text), requestCode, false)
    }

    /**
     * Launch a dialog to pick an image from camera/gallery apps with custom request code.
     *
     * @param fragment which will launch the dialog.
     */
    fun pickImage(fragment: Fragment) {
        pickImage(fragment, fragment.getString(R.string.pick_image_intent_text), DEFAULT_REQUEST_CODE, false)
    }

    /**
     * Launch a dialog to pick an image from gallery apps only with custom request code.
     *
     * @param activity which will launch the dialog.
     * @param requestCode request code that will be returned in result.
     */
    fun pickImageGalleryOnly(activity: Activity, requestCode: Int) {
        pickImage(activity, activity.getString(R.string.pick_image_intent_text), requestCode, true)

    }

    /**
     * Launch a dialog to pick an image from gallery apps only with custom request code.
     *
     * @param fragment which will launch the dialog.
     * @param requestCode request code that will be returned in result.
     */
    fun pickImageGalleryOnly(fragment: Fragment, requestCode: Int) {
        pickImage(fragment, fragment.getString(R.string.pick_image_intent_text), requestCode, true)
    }

    /**
     * Launch a dialog to pick an image from camera/gallery apps.
     *
     * @param activity     which will launch the dialog.
     * @param chooserTitle will appear on the picker dialog.
     */
    fun pickImage(activity: Activity, chooserTitle: String) {
        pickImage(activity, chooserTitle, DEFAULT_REQUEST_CODE, false)
    }

    /**
     * Launch a dialog to pick an image from camera/gallery apps.
     *
     * @param fragment     which will launch the dialog and will get the result in
     * onActivityResult()
     * @param chooserTitle will appear on the picker dialog.
     */
    fun pickImage(fragment: Fragment, chooserTitle: String) {
        pickImage(fragment, chooserTitle, DEFAULT_REQUEST_CODE, false)
    }

    /**
     * Launch a dialog to pick an image from camera/gallery apps.
     *
     * @param fragment     which will launch the dialog and will get the result in
     * onActivityResult()
     * @param chooserTitle will appear on the picker dialog.
     * @param requestCode request code that will be returned in result.
     */
    fun pickImage(fragment: Fragment, chooserTitle: String,
                  requestCode: Int, galleryOnly: Boolean) {
        mGalleryOnly = galleryOnly
        mPickImageRequestCode = requestCode
        mChooserTitle = chooserTitle
        startChooser(fragment)
    }

    /**
     * Launch a dialog to pick an image from camera/gallery apps.
     *
     * @param activity     which will launch the dialog and will get the result in
     * onActivityResult()
     * @param chooserTitle will appear on the picker dialog.
     */
    fun pickImage(activity: Activity, chooserTitle: String,
                  requestCode: Int, galleryOnly: Boolean) {
        mGalleryOnly = galleryOnly
        mPickImageRequestCode = requestCode
        mChooserTitle = chooserTitle
        startChooser(activity)
    }

    private fun startChooser(fragmentContext: Fragment) {
        fragmentContext.context?.let {
            val chooseImageIntent = getPickImageIntent(it, mChooserTitle)
            fragmentContext.startActivityForResult(chooseImageIntent, mPickImageRequestCode)
        }
    }

    private fun startChooser(activityContext: Activity) {
        val chooseImageIntent = getPickImageIntent(activityContext, mChooserTitle)
        activityContext.startActivityForResult(chooseImageIntent, mPickImageRequestCode)
    }

    /**
     * Get an Intent which will launch a dialog to pick an image from camera/gallery apps.
     *
     * @param context      context.
     * @param chooserTitle will appear on the picker dialog.
     * @return intent launcher.
     */
    fun getPickImageIntent(context: Context, chooserTitle: String?): Intent? {
        var chooserIntent: Intent? = null
        var intentList: MutableList<Intent> = ArrayList()

        val pickIntent = Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intentList = addIntentsToList(context, intentList, pickIntent)

        // Check is we want gallery apps only
        if (!mGalleryOnly) {
            // Camera action will fail if the app does not have permission, check before adding intent.
            // We only need to add the camera intent if the app does not use the CAMERA permission
            // in the androidManifest.xml
            // Or if the user has granted access to the camera.
            // See https://developer.android.com/reference/android/provider/MediaStore.html#ACTION_IMAGE_CAPTURE
            if (!appManifestContainsPermission(context, Manifest.permission.CAMERA) || hasCameraAccess(context)) {
                val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                takePhotoIntent.putExtra("return-data", true)
                //SDK versions for file providers
                if (isGreaterThanSDK_24()) {
                    takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider",
                                    ImageUtils.instance.getTemporalFile(context, mPickImageRequestCode.toString())))
                } else {
                    takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(ImageUtils.instance.getTemporalFile(context, mPickImageRequestCode.toString())))
                }
                intentList = addIntentsToList(context, intentList, takePhotoIntent)
            }
        }

        if (intentList.size > 0) {
            chooserIntent = Intent.createChooser(intentList.removeAt(intentList.size - 1),
                    chooserTitle)
            chooserIntent!!.putExtra(Intent.EXTRA_INITIAL_INTENTS,
                    intentList.toTypedArray<Parcelable>())
        }

        return chooserIntent
    }

    /**
     * Create a list of intents for the intent chooser
     * @param context context.
     * @param list list of intents to be presented inside the intent chooser
     * @param intent wrapper intent
     * */
    private fun addIntentsToList(context: Context, list: MutableList<Intent>, intent: Intent): MutableList<Intent> {
        Log.i(TAG, "Adding intents of type: " + intent.action!!)
        val resInfo = context.packageManager.queryIntentActivities(intent, 0)
        for (resolveInfo in resInfo) {
            val packageName = resolveInfo.activityInfo.packageName
            val targetedIntent = Intent(intent)
            targetedIntent.`package` = packageName
            list.add(targetedIntent)
            Log.i(TAG, "App package: " + packageName)
        }
        return list
    }

    /**
     * Checks if the current context has permission to access the camera.
     * @param context             context.
     */
    private fun hasCameraAccess(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(context,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Checks if the androidManifest.xml contains the given permission.
     * @param context             context.
     * @return Boolean, indicating if the permission is present.
     */
    private fun appManifestContainsPermission(context: Context, permission: String): Boolean {
        val pm = context.packageManager
        try {
            val packageInfo = pm.getPackageInfo(context.packageName, PackageManager.GET_PERMISSIONS)
            var requestedPermissions: Array<String>? = null
            if (packageInfo != null) {
                requestedPermissions = packageInfo.requestedPermissions
            }
            if (requestedPermissions == null) {
                return false
            }

            if (requestedPermissions.size > 0) {
                val requestedPermissionsList = Arrays.asList(*requestedPermissions)
                return requestedPermissionsList.contains(permission)
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        return false
    }

    /**
     * Called after launching the picker with the same values of Activity.getImageFromResult
     * in order to resolve the result and get the image.
     *
     * @param context             context.
     * @param requestCode         used to identify the pick image action.
     * @param resultCode          -1 means the result is OK.
     * @param imageReturnedIntent returned intent where is the image data.
     * @return image.
     */
    fun getImageFromResult(context: Context, requestCode: Int, resultCode: Int,
                           imageReturnedIntent: Intent?): Bitmap? {
        Log.i(TAG, "getImageFromResult() called with: resultCode = [$resultCode]")
        var bm: Bitmap? = null
        if (resultCode == Activity.RESULT_OK && requestCode == mPickImageRequestCode) {
            val imageFile = ImageUtils.instance.getTemporalFile(context, mPickImageRequestCode.toString())
            val selectedImage: Uri?
            val isCamera = (imageReturnedIntent == null
                    || imageReturnedIntent.data == null
                    || imageReturnedIntent.data!!.toString().contains(imageFile.toString()))
            if (isCamera) {
                /** CAMERA  */
                selectedImage = Uri.fromFile(imageFile)
            } else {
                /** ALBUM  */
                selectedImage = imageReturnedIntent!!.data
            }
            Log.i(TAG, "selectedImage: " + selectedImage!!)

            bm = decodeBitmap(context, selectedImage)
//            val rotation = ImageRotator.getRotation(context, selectedImage, isCamera)
//            bm = ImageRotator.rotate(bm, rotation)
        }
        return bm
    }

    /**
     * Return [Bitmap] from image file path
     * @param context
     * @param filePath as [String]
     * @return [Bitmap]
     * */
    fun getImageFromPath(context: Context?, filePath: String): Bitmap? {
        var bm: Bitmap? = null
        val imageFile = Uri.fromFile(File(filePath))
        context?.let {
            bm = decodeBitmap(it, imageFile)
        }
        return bm
    }

    /**
     * Called after launching the picker with the same values of Activity.getImageFromResult
     * in order to resolve the result and get the image path.
     *
     * @param context             context.
     * @param requestCode         used to identify the pick image action.
     * @param resultCode          -1 means the result is OK.
     * @param imageReturnedIntent returned intent where is the image data.
     * @return path to the saved image.
     */
    fun getImagePathFromResult(context: Context, requestCode: Int, resultCode: Int,
                               imageReturnedIntent: Intent?): String? {
        Log.i(TAG, "getImagePathFromResult() called with: resultCode = [$resultCode]")
        var selectedImage: Uri? = null
        if (resultCode == Activity.RESULT_OK && requestCode == mPickImageRequestCode) {
            val imageFile = ImageUtils.instance.getTemporalFile(context, mPickImageRequestCode.toString())
            val isCamera = (imageReturnedIntent == null
                    || imageReturnedIntent.data == null
                    || imageReturnedIntent.data!!.toString().contains(imageFile.toString()))
            if (isCamera) {
                return imageFile.getAbsolutePath()
            } else {
                selectedImage = imageReturnedIntent!!.data
            }
            Log.i(TAG, "selectedImage: " + selectedImage!!)
        }
        return if (selectedImage == null) {
            null
        } else getFilePathFromUri(context, selectedImage)
    }

    /**
     * Get stream, save the picture to the temp file and return path.
     *
     * @param context context
     * @param uri uri of the incoming file
     * @return path to the saved image.
     */
    fun getFilePathFromUri(context: Context, uri: Uri): String? {
        var `is`: InputStream? = null
        if (uri.authority != null) {
            try {
                `is` = context.contentResolver.openInputStream(uri)
                val bmp = BitmapFactory.decodeStream(`is`)
                return ImageUtils.instance.savePicture(context, bmp, uri.path.hashCode().toString())
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } finally {
                try {
                    `is`!!.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }
        return null
    }

    /**
     * Called after launching the picker with the same values of Activity.getImageFromResult
     * in order to resolve the result and get the input stream for the image.
     *
     * @param context             context.
     * @param requestCode         used to identify the pick image action.
     * @param resultCode          -1 means the result is OK.
     * @param imageReturnedIntent returned intent where is the image data.
     * @return stream.
     */
    fun getInputStreamFromResult(context: Context, requestCode: Int, resultCode: Int,
                                 imageReturnedIntent: Intent?): InputStream? {
        Log.i(TAG, "getFileFromResult() called with: resultCode = [$resultCode]")
        if (resultCode == Activity.RESULT_OK && requestCode == mPickImageRequestCode) {
            val imageFile = ImageUtils.instance.getTemporalFile(context, mPickImageRequestCode.toString())
            val selectedImage: Uri?
            val isCamera = (imageReturnedIntent == null
                    || imageReturnedIntent.data == null
                    || imageReturnedIntent.data!!.toString().contains(imageFile.toString()))
            if (isCamera) {
                /** CAMERA  */
                selectedImage = Uri.fromFile(imageFile)
            } else {
                /** ALBUM  */
                selectedImage = imageReturnedIntent!!.data
            }
            Log.i(TAG, "selectedImage: " + selectedImage!!)

            try {
                return if (isCamera) {
                    // We can just open the temporary file stream and return it
                    FileInputStream(imageFile)
                } else {
                    // Otherwise use the ContentResolver
                    context.contentResolver.openInputStream(selectedImage)
                }
            } catch (ex: FileNotFoundException) {
                Log.e(TAG, "Could not open input stream for: " + selectedImage)
                return null
            }

        }
        return null
    }

    /**
     * Loads a bitmap and avoids using too much memory loading big images (e.g.: 2560*1920)
     */
    private fun decodeBitmap(context: Context, theUri: Uri): Bitmap? {
        var outputBitmap: Bitmap? = null
        val fileDescriptor: AssetFileDescriptor?

        try {
            fileDescriptor = context.contentResolver.openAssetFileDescriptor(theUri, "r")

            // Get size of bitmap file
            val boundsOptions = BitmapFactory.Options()
            boundsOptions.inJustDecodeBounds = true
            BitmapFactory.decodeFileDescriptor(fileDescriptor!!.fileDescriptor, null, boundsOptions)

            // Get desired sample size. Note that these must be powers-of-two.
            val sampleSizes = intArrayOf(8, 4, 2, 1)
            var selectedSampleSize = 1 // 1 by default (original image)

            for (sampleSize in sampleSizes) {
                selectedSampleSize = sampleSize
                val targetWidth = boundsOptions.outWidth / sampleSize
                val targetHeight = boundsOptions.outHeight / sampleSize
                if (targetWidth >= minWidthQuality && targetHeight >= minHeightQuality) {
                    break
                }
            }

            // Decode bitmap at desired size
            val decodeOptions = BitmapFactory.Options()
            decodeOptions.inSampleSize = selectedSampleSize
            outputBitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor.fileDescriptor, null, decodeOptions)
            if (outputBitmap != null) {
                Log.i(TAG, "Loaded image with sample size " + decodeOptions.inSampleSize + "\t\t"
                        + "Bitmap width: " + outputBitmap.width
                        + "\theight: " + outputBitmap.height)
            }
            fileDescriptor.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return outputBitmap
    }

    /**
     * [rotatedBitmapCameraFrontLens] used for display purposes
     * @param facingLens of current camera
     * @param bitmapOptions bitmap options
     * @param filePath of current image used for displaying
     * */
    fun rotatedBitmapCameraFrontLens(facingLens: Int, bitmapOptions: BitmapFactory.Options, filePath: String): Bitmap {
        //Rotate Image
        val matrixPreRotateRight = Matrix()
        if (facingLens == CameraCharacteristics.LENS_FACING_BACK) {
            //Mirror matrix
            val mirrorY = floatArrayOf(-1f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f)
            val matrixRotateRight = Matrix()
            val matrixMirrorY = Matrix()
            matrixMirrorY.setValues(mirrorY)
            matrixPreRotateRight.postConcat(matrixMirrorY)
            matrixRotateRight.preRotate(270f) //Unwanted behavior
        }
        val bm: Bitmap = BitmapFactory.decodeFile(filePath, bitmapOptions)
        return Bitmap.createBitmap(bm, 0, 0, bm.width, bm.height, matrixPreRotateRight, true)
    }


    /**
     * Minimum image quality to be processed
     * Important to call before accessing CameraControlView class
     * Defaults is set to 400x400
     * @param _minWidthQuality minimum width image quality in pixels
     * @param _minHeightQuality minimum height image quality in pixels
     * */
    fun setMinQuality(_minWidthQuality: Int, _minHeightQuality: Int) {
        minWidthQuality = _minWidthQuality
        minHeightQuality = _minHeightQuality
    }


    /*App Version Uri control*/
    private fun isGreaterThanSDK_24(): Boolean {
        if (Build.VERSION.SDK_INT >= 24) {
            return true
        }
        return true
    }

    /**
     * [addToGallery]
     * Add photo to phone gallery
     * @param context is current context
     * @param filePath of the temporary file to be copied to the android gallery
     * */
    fun addToGallery(context: Context, filePath: String) {
        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val file = File(filePath)
        val contentUri = Uri.fromFile(file)
        intent.data = contentUri
        context.sendBroadcast(intent)
    }

    /**
     * [compressFile]
     * will compress the file for faster memory management & image resizing displayed
     * to the user
     * @param filepath of the temporary file that contains the photo
     * @param filePath of the compressed file
     * @see [ImageScaling]
     * */
    fun compressFile(filePath: String, desiredWidth: Int, desiredHeight: Int, facingLens: Int): String {
        val scaledBitmap: Bitmap?
        var imagePath: String? = null
        try {
            //Decode image
            val unscaledBitmap: Bitmap = ImageScaling.instance.decodeFileToBitmap(filePath, desiredWidth, desiredHeight, ImageScaling.ScalingLogic.FIT, facingLens)
            //Scale image
            if (!(unscaledBitmap.width <= 800 && unscaledBitmap.height <= 800)) {
                scaledBitmap = ImageScaling.instance.createScaledBitmap(unscaledBitmap, desiredWidth, desiredHeight, ImageScaling.ScalingLogic.FIT)
            } else {
                unscaledBitmap.recycle()
                return filePath
            }
            //Store to temporary file
            val file = temporaryFile()
            imagePath = file.absolutePath
            try {
                val fos = FileOutputStream(file)
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 70, fos)
                fos.flush()
                fos.close()
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            scaledBitmap.recycle()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        if (imagePath == null) {
            return filePath
        }
        return imagePath
    }

    /**
     * [temporaryFile] creates a temporary holding cached file with naming convention to store
     * the current photo taken by the user
     * */
    fun temporaryFile(): File {
        val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val file = File.createTempFile(
                mediaFileNaming(),
                ".jpeg",
                storageDir
        )
        return file
    }

    fun mediaFileNaming(): String {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        return imageFileName
    }

    /**
     * [getScreenSize] for the current device & provide full screen image preview
     * */
    fun getScreenSize(activity: Activity): Pair<Int, Int> {
        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        return Pair(displayMetrics.widthPixels, displayMetrics.heightPixels)
    }

}