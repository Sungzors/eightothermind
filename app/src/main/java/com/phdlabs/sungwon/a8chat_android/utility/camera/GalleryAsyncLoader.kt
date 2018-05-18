package com.phdlabs.sungwon.a8chat_android.utility.camera


import android.content.Context
import android.content.AsyncTaskLoader
import com.phdlabs.sungwon.a8chat_android.model.media.GalleryItem

/**
 * Created by jpam on 1/22/18.
 * [GalleryAsyncLoader] used to load image resources in the background of a Fragment
 * @param mContext
 */
class GalleryAsyncLoader(mContext: Context, var type: GalleryFileProvider.GALLERYFILETYPE, var galleryProvider: GalleryFileProvider) : AsyncTaskLoader<List<GalleryItem>>(mContext) {

    /*Properties*/
    private var mItemItems: List<GalleryItem>? = listOf()

    /**
     * Called in background & generates all of the [GalleryItem] data needed
     * to be published by the loader
     * */
    override fun loadInBackground(): List<GalleryItem> = galleryType(type)

    /**
     * [galleryType]
     * Fetch the desired type of files
     * */
    private fun galleryType(type: GalleryFileProvider.GALLERYFILETYPE): List<GalleryItem> {
        //Setup Gallery Provider
        var galleryContent: List<GalleryItem> = emptyList()
        //Choose the type of data to be returned by the provider
        if (type == GalleryFileProvider.GALLERYFILETYPE.PHOTO) {
            galleryContent = galleryProvider.getAlbumImages(context)

        } else if (type == GalleryFileProvider.GALLERYFILETYPE.VIDEO) {
            galleryContent = galleryProvider.getAlbumVideos(context)
        }
        return galleryContent
    }

    /**
     * Called when there is new data to deliver to the client.
     * Super class will take care of the delivery
     * */
    override fun deliverResult(data: List<GalleryItem>?) {

        if (isReset) {
            //Async query came in while the loader has stopped
            if (data != null) {
                onReleaseResources(data)
            }
        }

        val oldPhotos = mItemItems
        mItemItems = data

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
        if (mItemItems != null) {
            //Deliver if a result is available
            deliverResult(mItemItems)
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
    override fun onCanceled(data: List<GalleryItem>?) {
        super.onCanceled(data)
        //Release resources is there is a cancelled request
        onReleaseResources(data)
    }

    /**
     * Reset request
     * */
    override fun onReset() {
        super.onReset()
        //Make sure the loader has stopped
        onStopLoading()
        //Release resources
        if (mItemItems != null) {
            onReleaseResources(mItemItems)
            mItemItems = null
        }
    }

    /**
     * [onReleaseResources]
     * Helper function to release resources associated with an active dataset
     * */
    fun onReleaseResources(galleryItems: List<GalleryItem>?) {
        //For a simple List<> there is nothing to do, for a cursor we would close it here
        //TODO: Implement for further memory management
    }

}