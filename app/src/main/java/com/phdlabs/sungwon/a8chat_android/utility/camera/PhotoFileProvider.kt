package com.phdlabs.sungwon.a8chat_android.utility.camera

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import com.phdlabs.sungwon.a8chat_android.model.media.GalleryPhoto

/**
 * Created by paix on 11/8/17.
 * Custom App File Provider
 */
class PhotoFileProvider: FileProvider(){

    /*Singleton Holder*/
    private object Holder {
        var INSTANCE = PhotoFileProvider()
    }

    /*Companion*/
    companion object {
        //Singleton INSTANCE
        val instance: PhotoFileProvider by lazy { Holder.INSTANCE }
    }

    /**
     * [getAlbumImages] Fetches both full size images
     * via a single query
     * @param context
     * @return List
     * */
    fun getAlbumImages(context: Context):List<GalleryPhoto> {
        val projection = arrayOf(
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATE_TAKEN
        )
        val cursor = context.contentResolver.query(
                MediaStore.Images.Media.INTERNAL_CONTENT_URI, // Directory
                projection, // Which columns to return
                null, // Return all rows
                null,
                null// Ordered by Date Taken
        )
        //Extract propper columns
        val imagesColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
        val dateColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN)
        val result: ArrayList<GalleryPhoto> = ArrayList<GalleryPhoto>(cursor.count)

        if(cursor.moveToFirst()){
            do {
                //Full Image
                val thumbnailId = cursor.getInt(imagesColumnIndex)
                val fullImageUri = cursor.getString(thumbnailId)
                //Date
//                val dateId = cursor.getInt(dateColumnIndex)
//                val photoDateTaken = cursor.getLong(dateId)
                //Create the list item
                //var galleryPhoto = GalleryPhoto(fullImageUri, photoDateTaken)
                var galleryPhoto = GalleryPhoto(fullImageUri, null)

                result.add(galleryPhoto)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return result
    }

}