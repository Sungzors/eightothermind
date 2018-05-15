package com.phdlabs.sungwon.a8chat_android.utility.camera

import android.content.AsyncTaskLoader
import android.content.Context
import android.graphics.Bitmap
import android.hardware.camera2.CameraCharacteristics
import com.phdlabs.sungwon.a8chat_android.structure.camera.filters.FILTERS
import net.alhazmy13.imagefilter.ImageFilter

/**
 * Created by JPAM on 5/11/18.
 * Loads photo & returns filtered thumbnails
 */
class PhotoFilterAsyncLoader(mContext: Context, var filePath: String?) : AsyncTaskLoader<List<Pair<String, String>?>?>(mContext) {

    /*Properties*/
    //Filtered Photo List
    var filteredPhotos: MutableList<Pair<String, String>>? = null


    override fun loadInBackground(): List<Pair<String, String>?>? {
        return try {
            //Filtered photos
            filteredPhotos = mutableListOf()

            //Get Bitmap
            val cc = CameraControl.instance
            val iu = ImageUtils.instance
            var bm: Bitmap?

            filePath?.let {
                val newFilePath = CameraControl.instance.compressFile(it, 155, 155, 0)
                bm = cc.getImageFromPath(context, newFilePath)

                //Process photo in different filters
                for (filter in FILTERS.values()) {
                    when (filter) {
                        FILTERS.GRAY -> {
                            bm?.let {
                                filteredPhotos?.add(0,
                                        Pair(iu.cachePicture(context, ImageFilter.applyFilter(it, FILTERS.GRAY.filter),
                                                cc.mediaFileNaming() + FILTERS.GRAY.filterName), FILTERS.GRAY.filterName))

                            }
                        }
                        FILTERS.RELIEF -> {
                            bm?.let {
                                filteredPhotos?.add(1,
                                        Pair(iu.cachePicture(context, ImageFilter.applyFilter(it, FILTERS.RELIEF.filter),
                                                cc.mediaFileNaming() + FILTERS.RELIEF.filterName), FILTERS.RELIEF.filterName))
                            }
                        }
                        FILTERS.AVERAGE_BLUR -> {
                            bm?.let {
                                filteredPhotos?.add(2,
                                        Pair(iu.cachePicture(context, ImageFilter.applyFilter(it, FILTERS.AVERAGE_BLUR.filter),
                                                cc.mediaFileNaming() + FILTERS.AVERAGE_BLUR.filterName), FILTERS.AVERAGE_BLUR.filterName))
                            }
                        }
                        FILTERS.OIL -> {
                            bm?.let {
                                filteredPhotos?.add(3,
                                        Pair(iu.cachePicture(context, ImageFilter.applyFilter(it, FILTERS.OIL.filter),
                                                cc.mediaFileNaming() + FILTERS.OIL.filterName), FILTERS.OIL.filterName))
                            }
                        }
                        FILTERS.NEON -> {
                            bm?.let {
                                filteredPhotos?.add(4,
                                        Pair(iu.cachePicture(context, ImageFilter.applyFilter(it, FILTERS.NEON.filter),
                                                cc.mediaFileNaming() + FILTERS.NEON.filterName), FILTERS.NEON.filterName))
                            }
                        }
                        FILTERS.PIXELATE -> {
                            bm?.let {
                                filteredPhotos?.add(5,
                                        Pair(iu.cachePicture(context, ImageFilter.applyFilter(it, FILTERS.PIXELATE.filter),
                                                cc.mediaFileNaming() + FILTERS.PIXELATE.filterName), FILTERS.PIXELATE.filterName))
                            }
                        }
                        FILTERS.TV -> {
                            bm?.let {
                                filteredPhotos?.add(6,
                                        Pair(iu.cachePicture(context, ImageFilter.applyFilter(it, FILTERS.TV.filter),
                                                cc.mediaFileNaming() + FILTERS.TV.filterName), FILTERS.TV.filterName))
                            }
                        }
                        FILTERS.INVERT_COLOR -> {
                            bm?.let {
                                filteredPhotos?.add(7,
                                        Pair(iu.cachePicture(context, ImageFilter.applyFilter(it, FILTERS.INVERT_COLOR.filter),
                                                cc.mediaFileNaming() + FILTERS.INVERT_COLOR.filterName), FILTERS.INVERT_COLOR.filterName))
                            }
                        }
                        FILTERS.BLOCK -> {
                            bm?.let {
                                filteredPhotos?.add(8,
                                        Pair(iu.cachePicture(context, ImageFilter.applyFilter(it, FILTERS.BLOCK.filter),
                                                cc.mediaFileNaming() + FILTERS.BLOCK.filterName), FILTERS.BLOCK.filterName))
                            }
                        }
                        FILTERS.OLD -> {
                            bm?.let {
                                filteredPhotos?.add(9,
                                        Pair(iu.cachePicture(context, ImageFilter.applyFilter(it, FILTERS.OLD.filter),
                                                cc.mediaFileNaming() + FILTERS.OLD.filterName), FILTERS.OLD.filterName))
                            }
                        }
                        FILTERS.SHARPEN -> {
                            bm?.let {
                                filteredPhotos?.add(10,
                                        Pair(iu.cachePicture(context, ImageFilter.applyFilter(it, FILTERS.SHARPEN.filter),
                                                cc.mediaFileNaming() + FILTERS.SHARPEN.filterName), FILTERS.SHARPEN.filterName))
                            }
                        }
                        FILTERS.LIGHT -> {
                            bm?.let {
                                filteredPhotos?.add(11,
                                        Pair(iu.cachePicture(context, ImageFilter.applyFilter(it, FILTERS.LIGHT.filter),
                                                cc.mediaFileNaming() + FILTERS.LIGHT.filterName), FILTERS.LIGHT.filterName))
                            }
                        }
                        FILTERS.LOMO -> {
                            bm?.let {
                                filteredPhotos?.add(12,
                                        Pair(iu.cachePicture(context, ImageFilter.applyFilter(it, FILTERS.LOMO.filter),
                                                cc.mediaFileNaming() + FILTERS.LOMO.filterName), FILTERS.LOMO.filterName))
                            }
                        }
                        FILTERS.HDR -> {
                            bm?.let {
                                filteredPhotos?.add(13,
                                        Pair(iu.cachePicture(context, ImageFilter.applyFilter(it, FILTERS.HDR.filter),
                                                cc.mediaFileNaming() + FILTERS.HDR.filterName), FILTERS.HDR.filterName))
                            }
                        }
                        FILTERS.GAUSSIAN_BLUR -> {
                            bm?.let {
                                filteredPhotos?.add(14,
                                        Pair(iu.cachePicture(context, ImageFilter.applyFilter(it, FILTERS.GAUSSIAN_BLUR.filter),
                                                cc.mediaFileNaming() + FILTERS.GAUSSIAN_BLUR.filterName), FILTERS.GAUSSIAN_BLUR.filterName))
                            }
                        }
                        FILTERS.SOFT_GLOW -> {
                            bm?.let {
                                filteredPhotos?.add(15,
                                        Pair(iu.cachePicture(context, ImageFilter.applyFilter(it, FILTERS.SOFT_GLOW.filter),
                                                cc.mediaFileNaming() + FILTERS.SOFT_GLOW.filterName), FILTERS.SOFT_GLOW.filterName))
                            }
                        }
                        FILTERS.SKETCH -> {
                            bm?.let {
                                filteredPhotos?.add(16,
                                        Pair(iu.cachePicture(context, ImageFilter.applyFilter(it, FILTERS.SKETCH.filter),
                                                cc.mediaFileNaming() + FILTERS.SKETCH.filterName), FILTERS.SKETCH.filterName))
                            }
                        }
                        FILTERS.MOTION_BLUR -> {
                            bm?.let {
                                filteredPhotos?.add(17,
                                        Pair(iu.cachePicture(context, ImageFilter.applyFilter(it, FILTERS.MOTION_BLUR.filter),
                                                cc.mediaFileNaming() + FILTERS.MOTION_BLUR.filterName), FILTERS.MOTION_BLUR.filterName))
                            }
                        }
                        FILTERS.GOTHAM -> {
                            bm?.let {
                                filteredPhotos?.add(18,
                                        Pair(iu.cachePicture(context, ImageFilter.applyFilter(it, FILTERS.GOTHAM.filter),
                                                cc.mediaFileNaming() + FILTERS.GOTHAM.filterName), FILTERS.GOTHAM.filterName))
                            }
                        }
                    }
                }
            }
            filteredPhotos
        } catch (e: Exception) {
            e.stackTrace
            null
        }
    }

    override fun deliverResult(data: List<Pair<String, String>?>?) {
        if (isReset) {
            data?.let {
                onReleaseResources(data)
            }
        }
        if (isStarted) {
            super.deliverResult(data)
        }
    }

    override fun onStartLoading() {
        filteredPhotos?.let {
            //Deliver if a result is available
            deliverResult(it)
        } ?: run {
            //Start load if the data has changed on the source
            forceLoad()
        }
    }

    override fun onStopLoading() {
        //Attempt to cancel the current load
        cancelLoad()
    }

    override fun onCanceled(data: List<Pair<String, String>?>?) {
        super.onCanceled(data)
        onReleaseResources(data)
    }

    override fun onReset() {
        super.onReset()
        //Make sure the loader has stopped
        onStopLoading()
        //Release resources
        filteredPhotos?.let {
            onReleaseResources(it)
            filteredPhotos = null
        }
    }

    /**
     * [onReleaseResources]
     * Helper function to release resources associated with an active data-set
     * */
    fun onReleaseResources(data: List<Pair<String, String>?>?) {
        //For a simple item there is nothing to do, for a cursor we would close it here
        //TODO: Remove cached images from Android System

    }

}