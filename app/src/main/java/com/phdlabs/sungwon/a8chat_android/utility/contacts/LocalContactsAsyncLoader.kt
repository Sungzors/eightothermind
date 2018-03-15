package com.phdlabs.sungwon.a8chat_android.utility.contacts

import android.content.AsyncTaskLoader
import android.content.Context
import com.phdlabs.sungwon.a8chat_android.model.contacts.LocalContact

/**
 * Created by JPAM on 2/13/18.
 * [LocalContactsAsyncLoader] used to load local contacts resources in the background of a Fragment
 * @param mContext
 */
class LocalContactsAsyncLoader(mContext: Context) : AsyncTaskLoader<List<LocalContact>>(mContext) {

    /*Properties*/
    private var mLocalContacts: List<LocalContact>? = listOf()

    /**
     * Called in background & generates all of the [LocalContact]
     * data needed to be uploaded to server & newInstanceChannelRoom eight [Contact] on callback
     * */
    override fun loadInBackground(): List<LocalContact> =
            ContactsFileProvider.instance.loadLocalContacts(context)

    /**
     * Called when there is new data to deliver to the client.
     * Super class will take care of the delivery
     * */
    override fun deliverResult(data: List<LocalContact>?) {
        if (isReset) {
            //Async query came in while the loader has stopped
            if (data != null) {
                onReleaseResources(data)
            }
        }

        val oldContacts = mLocalContacts
        mLocalContacts = data

        if (isStarted) {
            //If the loader has started we can deliver the results
            super.deliverResult(data)
        }

        //Release resources delivered by old apps if needed
        if (oldContacts != null) {
            onReleaseResources(oldContacts)
        }
    }

    /**
     * Handles a request to start the loader
     * */
    override fun onStartLoading() {
        if (mLocalContacts != null) {
            //Deliver if a result is available
            deliverResult(mLocalContacts)
        } else {
            forceLoad()
        }
    }

    /**
     * Handle request to stop loading
     * */
    override fun onStopLoading() {
        cancelLoad()
    }

    /**
     * Handles request to cancel the load
     * */
    override fun onCanceled(data: List<LocalContact>?) {
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
        if (mLocalContacts != null) {
            onReleaseResources(mLocalContacts)
            mLocalContacts = null
        }
    }

    /**
     * [onReleaseResources]
     * Helper function to release resources associated with an active dataset
     * */
    fun onReleaseResources(galleryPhotos: List<LocalContact>?) {
        //For a simple List<> there is nothing to do, for a cursor we would close it here
        //TODO: Implement for further memory management
    }
}