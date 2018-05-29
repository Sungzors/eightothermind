package com.phdlabs.sungwon.a8chat_android.utility.camera


import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import android.util.Log
import com.phdlabs.sungwon.a8chat_android.model.media.GalleryItem
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * Created by JPAM on 11/8/17.
 * [ImageUtils] Used to provide temporary file naming convention to
 * images & an ordered file saving system outside the [CameraControl]
 *
 */

class ImageUtils private constructor() {

    /*Properties*/
    var TAG = "Image Utils"

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
        private const val BASE_IMAGE_NAME = "otherMind"
        //Singleton INSTANCE
        val instance: ImageUtils by lazy { Holder.INSTANCE }
    }

    /**
     * [getTemporalFile]
     * Used to generate temporary files on the Android cache system
     * */
    fun getTemporalFile(context: Context, payload: String): File =
            File(context.externalCacheDir, BASE_IMAGE_NAME + payload)


    /**
     * [cachePicture]
     * Write [bitmap] to cached temporary file
     * */
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
            fos?.let {
                try {
                    it.flush()
                    it.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }
        return savedImage.absolutePath
    }

    /**
     * [saveVideoWithSuffix]
     * Save cached videos to external storage in the Movies Directory
     * */
    fun saveVideoWithSuffix(context: Context, albumName: String, cachedVideoFilePath: String): String {
        var selectedOutputPath = ""
        if (isExternalStorageWritable()) {

            //Create storage directory if it does not exist
            val mediaStorageDir = getPublicAlbumName(albumName)
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.d(TAG, "Failed to create directory")
                }
            }

            //Create a media file name
            selectedOutputPath = mediaStorageDir.path + File.separator + CameraControl.instance.mediaFileNaming() + ".mp4"
            try {
                //Read cached video file
                val fis = File(cachedVideoFilePath).inputStream()
                File(selectedOutputPath).outputStream().use { fis.copyTo(it) }
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        return selectedOutputPath
    }

    /**
     * [isExternalStorageWritable]
     * Check if external storage is available for writing data
     * */
    private fun isExternalStorageWritable(): Boolean {
        val status = Environment.getExternalStorageState()
        return status == Environment.MEDIA_MOUNTED
    }

    /**
     * [getPublicAlbumName]
     * Retrieve the External Storage directory with album name for VIDEOS
     * */
    private fun getPublicAlbumName(albumName: String): File {
        val videoStorageDir = File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES + File.separator), albumName)

        if (!videoStorageDir.exists()) {
            if (!videoStorageDir.mkdirs()) {
                Log.d(TAG, "Failed to create directory")
            }
        }

        return videoStorageDir
    }

    /**
     * [correctRotation]
     * Read & correct photo rotation based on orientation metadata
     * */
    fun correctRotation(galleryItem: GalleryItem): GalleryItem {

        return galleryItem.mOrientation.let {
            val orient = it?.toInt()
            when (orient) {
                0 -> {
                    galleryItem.mCorrectedOrientatiion = 0f
                }
                90 -> {
                    galleryItem.mCorrectedOrientatiion = 90f
                }
                180 -> {
                    galleryItem.mCorrectedOrientatiion = 180f
                }
                270 -> {
                    galleryItem.mCorrectedOrientatiion = 270f
                }

            }
            return@let galleryItem
        }
    }

}