package com.phdlabs.sungwon.a8chat_android.utility.camera

import android.graphics.*
import android.hardware.camera2.CameraCharacteristics

/**
 * Created by paix on 1/16/18.
 * Simple class to scale & compress images taken by the user & provide an overall best performance
 */
class ImageScaling {

    /*Instance holder*/
    private object Holder {
        val INSTANCE = ImageScaling()
    }

    /*Companion*/
    companion object {

        /*Singleton INSTANCE model*/
        val instance: ImageScaling by lazy { Holder.INSTANCE }

    }

    /*Initializing*/
    init {
        println("Image Scaling ($this) is a Singleton")
    }

    /**
     * [ScalingLogic] defines how scaling should be carried out if source & destination have
     * different aspect ratios
     *
     * CROP: Scales the image at the minimum amount while making sure that at least one of the
     * dimensions fit inside the requested destination area.
     *
     * FIT: Scales the image to the minimum amount while making sure both dimensions fit inside
     * the requested destination area .
     *
     * */
    enum class ScalingLogic {
        CROP, FIT
    }

    /**
     * [decodeFileToBitmap] Decoding an image resource for further scaling to the requested destination dimensions
     * @param filePath -> temporary file path for the stored image
     * @param width -> desired width for the image
     * @param height -> desired height for the image
     * @param scale -> scaling logic
     * @return [Bitmap] resized & ready for scaling
     * */
    fun decodeFileToBitmap(filePath: String, dWidth: Int, dHeight: Int, scaling: ScalingLogic, facingLens: Int): Bitmap {
        /*Bitmap Options*/
        val options: BitmapFactory.Options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(filePath)
        options.inJustDecodeBounds = false
        options.inSampleSize = calculateSampleSize(options.outWidth, options.outHeight, dWidth, dHeight, scaling)
        return CameraControl.instance.rotatedBitmapCameraFrontLens(facingLens, options, filePath)
    }

    /**
     * [calculateSampleSize] Calculate optimal down-sampling factor given the dimensions of a source image using the
     * destination area & [ScalingLogic]
     * @param srcWidth source width of image
     * @param srcHeight source height of image
     * @param dstWidth destination width of image
     * @param dstHeight destination height of image
     * @param scaling scaling logic to avoid image stretching
     * @return optimal down-scaling sample size that will be used for decoding
     * */
    private fun calculateSampleSize(srcWidth: Int, srcHeight: Int, dstWidth: Int, dstHeight: Int, scaling: ScalingLogic): Int {
        /*Ratio*/
        val srcRatio: Float = (srcWidth / srcHeight.toFloat())
        val dstRatio: Float = (dstWidth / dstHeight.toFloat())
        /*Scaling type*/
        when (scaling) {
            ScalingLogic.FIT -> {
                if (srcRatio > dstRatio) {
                    return srcWidth / dstWidth
                } else {
                    return srcHeight / dstHeight
                }
            }
            ScalingLogic.CROP -> {
                if (srcRatio > dstRatio) {
                    return srcHeight / dstHeight
                } else {
                    return srcWidth / dstWidth
                }
            }
        }
    }

    /**
     * [createScaledBitmap] utility function for creating a scaled version of an existing bitmap
     *
     * @param unscaledBitmap [Bitmap] to be scaled
     * @param dstWidth wanted width of the bitmap destination
     * @param dstHeight wanted height of the bitmap destination
     * @param [ScalingLogic] defined scaling logic
     * @param scaled[Bitmap] object
     * */
    fun createScaledBitmap(unscaledBitmap: Bitmap, dstWidth: Int, dstHeight: Int, scaling: ScalingLogic): Bitmap {
        val srcRect = calculateSrcRect(unscaledBitmap.width, unscaledBitmap.height, dstWidth, dstHeight, scaling)
        val dstRect = calculateDstRect(unscaledBitmap.width, unscaledBitmap.height, dstWidth, dstHeight, scaling)
        val scaledBitmap = Bitmap.createBitmap(dstRect.width(), dstRect.height(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(scaledBitmap)
        canvas.drawBitmap(unscaledBitmap, srcRect, dstRect, Paint(Paint.FILTER_BITMAP_FLAG))
        return scaledBitmap
    }

    /**
     * [calculateSrcRect] calculates the *source* rectangle for scaling the bitmap
     *
     * @param srcWidth width of the source image
     * @param srcHeight height of the source image
     * @param dstWidth width of the destination area
     * @param dstHeight height of the destination area
     * @param [ScalingLogic] defined scaling logic
     * @return [Rect] optimal rectangle source
     * */
    fun calculateSrcRect(srcWidth: Int, srcHeight: Int, dstWidth: Int, dstHeight: Int, scaling: ScalingLogic): Rect {
        /*Ratio*/
        val srcRatio: Float = (srcWidth / srcHeight.toFloat())
        val dstRatio: Float = (dstWidth / dstHeight.toFloat())
        /*Scaling type*/
        when (scaling) {

            ScalingLogic.CROP -> {
                if (srcRatio > dstRatio) {
                    val srcRectWidth: Int = (srcHeight * dstRatio).toInt()
                    val srcRectLeft: Int = (srcWidth - srcRectWidth) / 2
                    return Rect(srcRectLeft, 0, srcRectLeft + srcRectWidth, srcHeight)
                } else {
                    val srcRectHeight: Int = (srcWidth / dstRatio).toInt()
                    val srcRectTop: Int = (srcHeight - srcRectHeight) / 2
                    return Rect(0, srcRectTop, srcWidth, srcRectTop + srcRectHeight)
                }
            }

            else -> {
                return Rect(0, 0, srcWidth, srcHeight)
            }

        }
    }

    /**
     * [calculateDstRect] calculates the *destination* rectangle for scaling the bitmap
     *
     * @param srcwidth width of the source image
     * @param srcHeight height of the source image
     * @param dstWidth  width of the destination area
     * @param dstHeight height of the destination area
     * @param [ScalingLogic] defined scaling logic
     * @return [Rect] optimal rectangle destination
     * */
    fun calculateDstRect(srcWidth: Int, srcHeight: Int, dstWidth: Int, dstHeight: Int, scaling: ScalingLogic): Rect {
        /*Ratio*/
        val srcRatio: Float = (srcWidth / srcHeight.toFloat())
        val dstRatio: Float = (dstWidth / dstHeight.toFloat())
        /*Scaling type*/
        when (scaling) {

            ScalingLogic.FIT -> {

                if (srcRatio > dstRatio) {
                    return Rect(0, 0, dstWidth, (dstWidth / srcRatio).toInt())
                } else {
                    return Rect(0, 0, (dstHeight * srcRatio).toInt(), dstHeight)
                }
            }
            else -> {
                return Rect(0, 0, dstWidth, dstHeight)
            }
        }
    }


}
