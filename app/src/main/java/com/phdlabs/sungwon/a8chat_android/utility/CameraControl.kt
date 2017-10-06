package com.phdlabs.sungwon.a8chat_android.utility

/**
 * Created by SungWon on 10/2/2017.
 */
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.content.res.AssetFileDescriptor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

//TODO: Convert to a single Context
//TODO: Add camera rotation

/**
 * Created by paix on 9/25/17.
 * Camera Control written in Kotlin
 * A group of *methods* to control
 * camera & gallery intents & data
 *
 * INFO:
 * _private_constructor_ ensures this class can't be
 * initialized anywhere except inside of this class
 * -Singleton pattern
 * -Written to be used within Fragments & Activities
 * TODO: Needs refactoring for a single context entry
 */

class CameraControl private constructor() {

    /*Initializer Log used for testing
    *
    * INFO:
    * init will be called when this class is initialized for
    * the first time (i.e. when calling CameraControl.INSTANCE)
    * */
    init {
        println("CameraControl ($this) is a Singleton")
    }

    /**Instance Holder for Singleton - Camera Control
     * val instance is the initialized variable
     *
     * INFO:
     * Holder object & lazy instance is used to ensure only one
     * instance of CameraControl() is created
     * */
    private object Holder {
        val INSTANCE = CameraControl()
    }

    /**Request Code for Camera Control
     * [requestCode]
     * Returns Request Code - Used for onActivityResult()
     * */
    private fun requestCode(): Int = REQUEST_CODE_IMAGE

    /**Image Result
     * Class to handle image data inside onActivityResult
     * Includes optional Bitmap & optional File
     * */
    class ImageResult {

        private object Holder {
            val INSTANCE = ImageResult()
        }

        fun getFile() = file
        fun getBitmap() = bitmap
        fun setFile(newFile: File?) {
            file = newFile
        }

        fun setBitmap(newBitmap: Bitmap?) {
            bitmap = newBitmap
        }

        companion object {
            var bitmap: Bitmap? = null
            var file: File? = null
            //Singleton instance
            val instance: ImageResult by lazy { Holder.INSTANCE }
        }

    }

    /**Start Image Picker with Camera Control instance
     *[startImagePicker]
     * @param fragment - where the Camera control is initialized
     * Will start image picker if the returned intent by [pictureIntent] is valid
     * */
    fun startImagePicker(fragment: Fragment) {
        val intent: Intent? = pictureIntent(fragment)
        if (intent != null) {
            fragment.startActivityForResult(intent, requestCode())
        }
    }

    /**Start Image Picker with Camera Control instance
     *[startImagePicker]
     * @param activity - where the Camera control is initialized
     * Will start image picker if the returned intent by [pictureIntent] is valid
     * */
    fun startImagePicker(activity: Activity) {
        val intent: Intent? = pictureIntent(activity)
        if (intent != null) {
            activity.startActivityForResult(intent, requestCode())
        }
    }

    /**Camera & Gallery Intent Query
     * [pictureIntent]
     * @param fragment - current fragment for showing image picker
     * */
    private fun pictureIntent(fragment: Fragment): Intent? {
        var chooserIntent: Intent? = null
        var intentList: MutableList<Intent> = ArrayList()
        val galleryIntent = Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intentList.add(galleryIntent)
        if (!checkCameraPermission(fragment.activity, android.Manifest.permission.CAMERA) ||
                hasCameraAccess(fragment.activity)) {
            val camIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            var photoFile = getTemporaryFile(fragment.activity, true)
            var photoUri: Uri = FileProvider.getUriForFile(fragment.context, "com.example.android.referaway.fileprovider", photoFile)
            //camIntent.putExtra("return-data", true)
            //camIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getTemporaryFile(fragment.activity, true)))
            camIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            intentList = addIntent(fragment.activity, intentList, camIntent)
        }
        if (intentList.size > 0) {
            chooserIntent = Intent.createChooser(intentList.removeAt(intentList.size - 1), "Choose an image")
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentList.toTypedArray())
        }
        return chooserIntent
    }

    /**Camera & Gallery Intent Query
     * [pictureIntent]
     * @param activity - current activity for showing image picker
     * */
    private fun pictureIntent(activity: Activity): Intent? {
        var chooserIntent: Intent? = null
        var intentList: MutableList<Intent> = ArrayList()
        val galleryIntent = Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intentList.add(galleryIntent)
        if (!checkCameraPermission(activity.applicationContext, android.Manifest.permission.CAMERA) ||
                hasCameraAccess(activity)) {
            val camIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            var photoFile = getTemporaryFile(activity.applicationContext, true)
            var photoUri: Uri = FileProvider.getUriForFile(activity.applicationContext, "com.example.android.8chat_android.fileprovider", photoFile)
            //camIntent.putExtra("return-data", true)
            //camIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getTemporaryFile(fragment.activity, true)))
            camIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            intentList = addIntent(activity, intentList, camIntent)
        }
        if (intentList.size > 0) {
            chooserIntent = Intent.createChooser(intentList.removeAt(intentList.size - 1), "Choose an image")
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentList.toTypedArray())
        }
        return chooserIntent
    }


    /**Intent List used to display available options
     *[addIntent]
     * @param context - current Fragment Context
     * @param intentList - targeted package intents to display
     * @param intent - intent with package name for displaying
     * @return List<Intent> for use on Alert Dialog
     * */
    private fun addIntent(context: Context, intentList: MutableList<Intent>, intent: Intent): MutableList<Intent> {
        val resolveInfo: List<ResolveInfo> = context.packageManager.queryIntentActivities(intent, 0)
        for (each in resolveInfo) {
            val packageName: String = each.activityInfo.packageName
            val targetIntent = Intent(intent)
            targetIntent.`package` = packageName
            intentList.add(targetIntent)
        }
        return intentList
    }


    /**Check Camera permission on App
     * [checkCameraPermission]
     * @param context - current fragment context
     * @param permission - string permission
     * @return Boolean indicating camera permission access granted
     * */
    private fun checkCameraPermission(context: Context, permission: String): Boolean {
        //Get package manager
        val packageManager: PackageManager = context.packageManager
        try {
            val packageInfo: PackageInfo = packageManager.getPackageInfo(context.packageName,
                    PackageManager.GET_PERMISSIONS)
            val requestedPermissions = packageInfo.requestedPermissions
            return if (requestedPermissions.isNotEmpty()) {
                requestedPermissions.contains(permission)
            } else {
                false
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return false
    }

    /**Check Camera granted permission from user in App
     *[hasCameraAccess]
     * @param context - current fragment context
     * @return Boolean indicating camera access app manifest permission*/
    private fun hasCameraAccess(context: Activity): Boolean {
        return ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
    }

    /**Temporary image file cache , uses temporary image constant
     *[getTemporaryFile]
     * @param context - current fragment context
     * @return temporary image file
     * */
    private fun getTemporaryFile(context: Context, isCamera: Boolean): File? {
        //Temporary File
        var storageDir: File?
        val externalCacheDirs: Array<File> = ContextCompat.getExternalCacheDirs(context)
        storageDir = if (!isCamera) { //Gallery
            if (externalCacheDirs.isNotEmpty()) {
                externalCacheDirs[0]
            } else {
                context.externalCacheDir
            }
        } else { //Camera
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        }
        //Time Stamp
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName: String = "TMP_" + timeStamp + "_"
        var tempFile: File? = null
        try {
            tempFile = createTempFile(imageFileName, null, storageDir)
            if (isCamera) {
                current_path = tempFile.absolutePath
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return tempFile
    }

    /**Get filepath for selected image
     *[getImageAsync]
     * @param fragment - current fragment context
     * @param requestCode - REQUEST_CODE_IMAGE
     * @param resultCode - RESULT_OK (-1)
     * @param intent - camera or gallery intent
     * @param onSelected completion handler to show image in UI
     * @param onComplete completion handler to notify adapter image has been set in the UI
     * */
    fun getImageAsync(fragment: Fragment, requestCode: Int, resultCode: Int, intent: Intent?,
                      onSelected: Procedure<ImageResult>?, onComplete: Procedure<ImageResult>): Boolean {
        if (requestCode == REQUEST_CODE_IMAGE) {
            getImageAsync(fragment, resultCode, intent, onSelected, onComplete)
            return true
        }
        return false
    }

    /**
     * Get filepath for selected image
     *@param fragment - current fragment context
     *@param resultCode - RESULT_OK (-1) needed to run Async Task
     *@param intent - camera or gallery intent
     *@param onSelected completion handler to show image in UI
     *@param onComplete completion handler to notify adapter image has been set in the UI
     * Important: This method doesn't work on the emulator - device only
     * */
    private fun getImageAsync(fragment: Fragment, resultCode: Int, intent: Intent?,
                              onSelected: Procedure<ImageResult>?, onComplete: Procedure<ImageResult>) {

        if (resultCode == Activity.RESULT_OK && intent != null) {

            val selectedImage: Uri
            val file: File? = getTemporaryFile(fragment.activity, false)
            val isCamera: Boolean = intent.data == null || intent.data.toString().contains(file.toString())
            selectedImage = if (isCamera) {
                Uri.fromFile(file)
            } else {
                intent.data
            }
            val imageResult: ImageResult = ImageResult.Companion.instance
            imageResult.setFile(file)
            imageResult.setBitmap(null)
            onSelected?.apply(imageResult)

            class MyAsyncTask : AsyncTask<Void, Void, ImageResult>() {

                override fun doInBackground(vararg p0: Void?): ImageResult {
                    //Resize Bitmap
                    val resizedBitmap: Bitmap? = if (!isCamera) { //Gallery
                        decodeBitmapFromGallery(fragment.activity, selectedImage, false)
                    } else { //Camera
                        decodeBitmapFromGallery(fragment.activity, selectedImage, true)
                    }
                    //TODO implement camera rotation
                    writeBitmapToFile(file, resizedBitmap)
                    val finalImageResult: ImageResult = ImageResult.Companion.instance
                    imageResult.setFile(file)
                    if (resizedBitmap != null) {
                        imageResult.setBitmap(resizedBitmap)
                    }
                    return finalImageResult
                }

                override fun onPostExecute(result: ImageResult?) {
                    super.onPostExecute(result)
                    onComplete.apply(result)
                    println("ON POST EXECUTED")
                }
            }

            val task = MyAsyncTask()
            task.execute()
        }
    }


    /**Get filepath for selected image
     *[getImageAsync]
     * @param activity - current activity context
     * @param requestCode - REQUEST_CODE_IMAGE
     * @param resultCode - RESULT_OK (-1)
     * @param intent - camera or gallery intent
     * @param onSelected completion handler to show image in UI
     * @param onComplete completion handler to notify adapter image has been set in the UI
     * */
    fun getImageAsync(activity: Activity, requestCode: Int, resultCode: Int, intent: Intent?,
                      onSelected: Procedure<ImageResult>?, onComplete: Procedure<ImageResult>): Boolean {
        if (requestCode == REQUEST_CODE_IMAGE) {
            getImageAsync(activity, resultCode, intent, onSelected, onComplete)
            return true
        }
        return false
    }

    /**
     * Get filepath for selected image
     *@param activity - current activity context
     *@param resultCode - RESULT_OK (-1) needed to run Async Task
     *@param intent - camera or gallery intent
     *@param onSelected completion handler to show image in UI
     *@param onComplete completion handler to notify adapter image has been set in the UI
     * Important: This method doesn't work on the emulator - device only
     * */
    private fun getImageAsync(activity: Activity, resultCode: Int, intent: Intent?,
                              onSelected: Procedure<ImageResult>?, onComplete: Procedure<ImageResult>) {

        if (resultCode == Activity.RESULT_OK && intent != null) {

            val selectedImage: Uri
            val file: File? = getTemporaryFile(activity.applicationContext, false)
            val isCamera: Boolean = intent.data == null || intent.data.toString().contains(file.toString())
            selectedImage = if (isCamera) {
                Uri.fromFile(file)
            } else {
                intent.data
            }
            val imageResult: ImageResult = ImageResult.Companion.instance
            imageResult.setFile(file)
            imageResult.setBitmap(null)
            onSelected?.apply(imageResult)

            class MyAsyncTask2 : AsyncTask<Void, Void, ImageResult>() {

                override fun doInBackground(vararg p0: Void?): ImageResult {
                    //Resize Bitmap
                    val resizedBitmap: Bitmap? = if (!isCamera) { //Gallery
                        decodeBitmapFromGallery(activity.applicationContext, selectedImage, false)
                    } else { //Camera
                        decodeBitmapFromGallery(activity.applicationContext, selectedImage, true)
                    }
                    //TODO implement camera rotation
                    writeBitmapToFile(file, resizedBitmap)
                    val finalImageResult: ImageResult = ImageResult.Companion.instance
                    imageResult.setFile(file)
                    if (resizedBitmap != null) {
                        imageResult.setBitmap(resizedBitmap)
                    }
                    return finalImageResult
                }

                override fun onPostExecute(result: ImageResult?) {
                    super.onPostExecute(result)
                    onComplete.apply(result)
                    println("ON POST EXECUTED")
                }
            }

            val task = MyAsyncTask2()
            task.execute()
        }
    }

    /**Decode Bitmap from Gallery
     * [decodeBitmapFromGallery]
     * @param context - current fragment context
     * @param uri - file uri*/
    private fun decodeBitmapFromGallery(context: Context, uri: Uri, isCamera: Boolean): Bitmap? {
        var outputBitmap: Bitmap? = null
        val fileDescriptor: AssetFileDescriptor?

        if (isCamera) {
            //Get size of Bitmap
            val boundOptions = BitmapFactory.Options()
            boundOptions.inJustDecodeBounds = true
            BitmapFactory.decodeFile(current_path, boundOptions)
            boundOptions.inJustDecodeBounds = false
            boundOptions.inSampleSize = resizeBitmap(boundOptions)
            //boundOptions.inSampleSize = 4
            outputBitmap = BitmapFactory.decodeFile(current_path, boundOptions)
        } else {
            try {
                //Access data from uri
                fileDescriptor = context.contentResolver.openAssetFileDescriptor(uri, "r") //Read mode
                //Get size of Bitmap
                val boundOptions = BitmapFactory.Options()
                boundOptions.inJustDecodeBounds = true
                if (fileDescriptor != null) {
                    BitmapFactory.decodeFileDescriptor(fileDescriptor.fileDescriptor, null, boundOptions)
                }
                boundOptions.inJustDecodeBounds = false
                val decodeOptions = BitmapFactory.Options()
                decodeOptions.inSampleSize = resizeBitmap(boundOptions)
                if (fileDescriptor != null) {
                    outputBitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor.fileDescriptor, null, decodeOptions)
                    fileDescriptor.close()
                }
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return outputBitmap
    }

    /**Resize Bitmap to maximum size
     * [resizeBitmap]
     * @param boundOptions - current bitmap options
     * @return Int - sample size returned with maximum app defined sizing
     * Important: Slow performance
     * */
    private fun resizeBitmap(boundOptions: BitmapFactory.Options): Int {
        //Get desired sample size
        val sampleSizes: Array<Int> = arrayOf(8, 4, 2, 1)
        var targetWidth: Int
        var targetHeight: Int
        var i = 0
        do {
            targetWidth = boundOptions.outWidth / sampleSizes[i]
            targetHeight = boundOptions.outHeight / sampleSizes[i]
            i++
        } while (i < sampleSizes.size - 1 &&
                (targetWidth.compareTo(DEFAULT_WIDTH) < DEFAULT_WIDTH ||
                        targetHeight.compareTo(DEFAULT_HEIGHT) < DEFAULT_HEIGHT))
        return sampleSizes[i]
    }

    /**Write Bitmap to File
     * [writeBitmapToFile]
     * @param tempFile - temporary file to be written to disk
     * @param bitmap - bitmap to be written to file
     * */
    private fun writeBitmapToFile(tempFile: File?, bitmap: Bitmap?) {
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(tempFile)
            bitmap?.compress(Bitmap.CompressFormat.PNG, 100, fos)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                if (fos != null) {
                    fos.close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    /*Singleton companion object
    * **/
    companion object {
        /*Properties*/
        val REQUEST_CODE_IMAGE = 1234
        val DEFAULT_WIDTH: Int = 400
        val DEFAULT_HEIGHT: Int = 600
        var current_path: String = ""
        //Singleton instance
        val instance: CameraControl by lazy { Holder.INSTANCE }
    }

}