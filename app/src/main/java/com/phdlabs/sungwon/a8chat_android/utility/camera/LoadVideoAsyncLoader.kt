package com.phdlabs.sungwon.a8chat_android.utility.camera

import android.content.AsyncTaskLoader
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import net.protyposis.android.mediaplayer.MediaSource
import net.protyposis.android.mediaplayer.UriSource

/**
 * Created by JPAM on 4/30/18.
 * Load video files asynchronously from Media library to Video Preview Activity
 */
class LoadVideoAsyncLoader(mContext: Context, var bundle: Bundle?) : AsyncTaskLoader<MediaSource>(mContext) {

    /*Properties*/
    private var mMediaSource: MediaSource? = null

    /**
     * Called in background & generates & pulls the [MediaSource] data needed
     * to be published by the loader
     * */
    override fun loadInBackground(): MediaSource? {
        return try {
            bundle?.getParcelable<Uri>("uri")?.let {
                mMediaSource = UriSource(context, it)
                mMediaSource
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Called when there is new data to deliver to the client.
     * Super class will take care of the delivery
     * */
    override fun deliverResult(data: MediaSource?) {
        if (isReset) {
            data?.let {
                onReleaseResources(data)
            }
        }
        if (isStarted) {
            super.deliverResult(data)
        }
    }

    /**
     * Handles a request to start the loader
     * */
    override fun onStartLoading() {
        mMediaSource?.let {
            //Deliver if a result is available
            deliverResult(it)
        } ?: run {
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
    override fun onCanceled(data: MediaSource?) {
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
        mMediaSource?.let {
            onReleaseResources(it)
            mMediaSource = null
        }
    }

    /**
     * [onReleaseResources]
     * Helper function to release resources associated with an active data-set
     * */
    fun onReleaseResources(data: MediaSource?) {
        //For a simple item there is nothing to do, for a cursor we would close it here
        //TODO: Implement for further memory management
    }

}