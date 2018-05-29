package com.phdlabs.sungwon.a8chat_android.model.media

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by jpam on 1/19/18.
 * [GalleryItem] model for displaying images saved on the device's gallery
 * This model implements [Parcelable] for high-performance transport of
 * data of one component to another.
 */
class GalleryItem() : Parcelable {

    /*Properties*/
    var mFullPath: String? = null
    var mThumbnailPath: String? = null
    var mDate: String? = null
    var mOrientation: String? = null
    var mCorrectedOrientatiion: Float = 0f

    /*Constructors*/
    constructor(fullPath: String, thumbnailPath: String?, date: String?, orientation: String?) : this() {
        mFullPath = fullPath
        mThumbnailPath = thumbnailPath
        mDate = date
        mOrientation = orientation
    }

    protected constructor(parcel: Parcel) : this() {}

    /*Required parcelable methods*/
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        //Image path
        parcel.writeString(mFullPath)
        //Thumbnail path
        mThumbnailPath?.let {
            parcel.writeString(it)
        }
        //Date path
        mDate?.let {
            parcel.writeString(it)
        }
        //Orientation path
        mOrientation?.let {
            parcel.writeString(it)
        }
        //Corrected orientation
        mCorrectedOrientatiion?.let {
            parcel.writeFloat(it)
        }
    }

    override fun describeContents(): Int = 0

    /*Companion object*/
    companion object CREATOR : Parcelable.Creator<GalleryItem> {

        fun create(thumbnailPath: String?, fullPath: String, date: String, orientation: String?):
                GalleryItem = GalleryItem(fullPath, thumbnailPath, date, orientation)

        override fun createFromParcel(parcel: Parcel): GalleryItem = GalleryItem(parcel)

        override fun newArray(size: Int): Array<GalleryItem?> = arrayOfNulls(size)
    }

}