package com.phdlabs.sungwon.a8chat_android.model.media

import android.os.Parcel
import android.os.Parcelable
import java.util.*

/**
 * Created by jpam on 1/19/18.
 * [GalleryPhoto] model for displaying images saved on the device's gallery
 * This model implements [Parcelable] for high-performance transport of
 * data of one component to another.
 */
class GalleryPhoto() : Parcelable {

    /*Properties*/
    var mPath:String? = null
    var mDate: String? = null

    /*Constructors*/
    constructor(imagePath: String, date: String?): this() {
        mPath = imagePath
        mDate = date
    }

    protected constructor(parcel: Parcel) : this() {
    }

    /*Required parcelable methods*/
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        //Image path
        parcel.writeString(mPath)
        //Date path
        mDate?.let {
            parcel.writeString(it)
        }
    }

    override fun describeContents(): Int = 0

    /*Companion object*/
    companion object CREATOR : Parcelable.Creator<GalleryPhoto> {

        fun create(thumbnailPath: String?,fullPath: String, date: String): GalleryPhoto = GalleryPhoto(fullPath, date)

        override fun createFromParcel(parcel: Parcel): GalleryPhoto = GalleryPhoto(parcel)

        override fun newArray(size: Int): Array<GalleryPhoto?> {
            return arrayOfNulls(size)
        }
    }
}