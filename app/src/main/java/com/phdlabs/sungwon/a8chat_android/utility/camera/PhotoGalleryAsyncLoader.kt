package com.phdlabs.sungwon.a8chat_android.utility.camera


import android.content.Context
import android.support.v4.content.AsyncTaskLoader
import com.phdlabs.sungwon.a8chat_android.model.media.GalleryPhoto

/**
 * Created by jpam on 1/22/18.
 * [PhotoGalleryAsyncLoader] used to load image resources in the background of a Fragment
 * @param mContext
 */
class PhotoGalleryAsyncLoader(mContext: Context) : AsyncTaskLoader<List<GalleryPhoto>>(mContext) {

    /*Properties*/
    private var mPhotoItems: List<GalleryPhoto>? = listOf()

    /**
     * Called in background & generates all of the [GalleryPhoto] data needed
     * to be published by the loader
     * */
    override fun loadInBackground(): List<GalleryPhoto> {
        val photos: List<GalleryPhoto> = PhotoFileProvider.instance.getAlbumImages(context)
        return photos
    }

    /**
     * Called when there is new data to deliver to the client.
     * Super class will take care of the delivery
     * */
    override fun deliverResult(data: List<GalleryPhoto>?) {
        if (isReset) {
            //Async query came in while the loader has stopped
            if (data != null) {
                onReleaseResources(data)
            }
        }

        var oldPhotos = mPhotoItems
        mPhotoItems = data

        if (isStarted) {
            //If the loader has started we can deliver the results
            super.deliverResult(data)
        }

        //Release resources delivered by old apps if needed
        if (oldPhotos != null) {
            onReleaseResources(oldPhotos)
        }
    }

    /**
     * Handles a request to start the loader
     * */
    override fun onStartLoading() {
        if (mPhotoItems != null) {
            //Deliver if a result is available
            deliverResult(mPhotoItems)
        } else {
            //Start load if the data has changed on the source
            forceLoad()
        }
    }

    /**
     * Handle request to stop loading
     * */
    override fun onStopLoading() {
        //Attempt to cancel the current load
        cancelLoad()
    }

    /**
     * Handles request to cancel the load
     * */
    override fun onCanceled(data: List<GalleryPhoto>?) {
        super.onCanceled(data)
        //Release resources is there is a cancelled request
        onReleaseResources(data)
    }

    override fun onReset() {
        super.onReset()
        //Make sure the loader has stopped
        onStopLoading()
        //Release resources
        if (mPhotoItems != null) {
            onReleaseResources(mPhotoItems)
            mPhotoItems = null
        }
    }


    /**
     * [onReleaseResources]
     * Helper function to release resources associated with an active dataset
     * */
    fun onReleaseResources(galleryPhotos: List<GalleryPhoto>?) {
        //For a simple List<> there is nothing to do, for a cursor we would close it here
        //TODO: Implement for further memory management
    }

}