package com.phdlabs.sungwon.a8chat_android.utility.camera


import android.content.Context
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import kotlin.properties.Delegates

/**
 * Created by JPAM on 11/8/17.
 * [ImageUtils] Used to provide temporary file naming convention to
 * images & an ordered file saving system
 *
 */

class ImageUtils private constructor() {

    /*Initializer Log used for testing
     *
     * INFO:
     * init will be called when this class is initialized for
     * the first time (i.e. when calling CameraControl.INSTANCE)
     * */
    init {
        println("ImageUtils ($this) is a Singleton")
    }

    /**Instance Holder for Singleton - Camera Control
     * val INSTANCE is the initialized variable
     *
     * INFO:
     * Holder object & lazy INSTANCE is used to ensure only one
     * INSTANCE of CameraControl() is created
     * */
    private object Holder {

        val INSTANCE = ImageUtils()
    }

    /*Singleton companion object*/
    companion object {
        private val BASE_IMAGE_NAME = "i_prefix_"
        //Singleton INSTANCE
        val instance: ImageUtils by lazy { Holder.INSTANCE }
    }

    fun getTemporalFile(context: Context, payload: String): File =
            File(context.externalCacheDir, BASE_IMAGE_NAME + payload)


    fun cachePicture(context: Context, bitmap: Bitmap, imageSuffix: String): String {
        val savedImage = getTemporalFile(context, "$imageSuffix.jpeg")
        var fos: FileOutputStream? = null
        if (savedImage.exists()) {
            savedImage.delete()
        }
        try {
            fos = FileOutputStream(savedImage.path)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos)
        } catch (e: java.io.IOException) {
            e.printStackTrace()
        } finally {
            if (!bitmap.isRecycled) {
                bitmap.recycle()
            }
            if (fos != null) {
                try {
                    fos.flush()
                    fos.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }

        return savedImage.absolutePath
    }

    fun saveVideoWithSuffix(folderName: String, videoName: String): String {
        var selectedOutputPath = ""
        if (isSDCARDMounted()) {
            val mediaStorageDir = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), folderName)
            // Create a storage directory if it does not exist
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.d("Image Utils", "Failed to create directory")
                }
            }

            // Create a media file name
            selectedOutputPath = mediaStorageDir.path + File.separator + videoName + ".mp4"
            Log.d("ImageUtils", "selected video path $selectedOutputPath")
            val file = File(selectedOutputPath)
            try {
                val out = FileOutputStream(file)
                out.flush()
                out.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }


        return selectedOutputPath
    }

    private fun isSDCARDMounted(): Boolean {
        val status = Environment.getExternalStorageState()
        return status == Environment.MEDIA_MOUNTED
    }

}