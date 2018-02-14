package com.phdlabs.sungwon.a8chat_android.utility.camera

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import com.phdlabs.sungwon.a8chat_android.model.media.GalleryPhoto

/**
 * Created by paix on 11/8/17.
 * Custom App File Provider
 */
class PhotoFileProvider : FileProvider() {

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
    fun getAlbumImages(context: Context): List<GalleryPhoto> {
        val projection = arrayOf(
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.ORIENTATION //TODO: Added
        )
        val cursor = context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // Directory
                projection, // Which columns to return
                null, // Return all rows
                null,
                null// Ordered by Date Taken
        )
        //Extract proper columns
        val result: ArrayList<GalleryPhoto> = ArrayList<GalleryPhoto>(cursor.count)

        if (cursor.moveToFirst()) {
            do {
                //Full Image
                val fullImageUri = uriToFullImage(cursor, context)
                //Date
                val dateTaken = retrieveDateTaken(cursor, context)
                //Instantiate GalleryPhoto
                val imageOrientation = retrieveOrientation(cursor, context)
                val galleryPhoto = GalleryPhoto(fullImageUri.toString(), null, dateTaken, imageOrientation)
                result.add(galleryPhoto)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return result
    }

    /**
     * [uriToFullImage] Get the path to the full image for a given thumbnail
     * @param cursor for the current query
     * @param context
     * @return file path [Uri]
     * */
    fun uriToFullImage(cursor: Cursor, context: Context): Uri {
        val imageId = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media._ID))
        //Request image
        val filepathColumn = arrayOf(MediaStore.Images.Media.DATA)
        val imageCursor = context.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                filepathColumn,
                MediaStore.Images.Media._ID + "=?",
                arrayOf(imageId),
                null
        )
        if (imageCursor != null && imageCursor.moveToFirst()) {
            val columnIndex = imageCursor.getColumnIndex(filepathColumn[0])
            val filePath = imageCursor.getString(columnIndex)
            imageCursor.close()
            return Uri.parse(filePath)
        } else {
            imageCursor.close()
            return Uri.parse("")
        }
    }

    /**
     * [retrieveDateTaken] retrieve date information for the image
     * */
    private fun retrieveDateTaken(cursor: Cursor, context: Context): String {
        val imageId = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media._ID))
        //Request Date
        val dateColumn = arrayOf(MediaStore.Images.Media.DATE_TAKEN)
        val dateCursor = context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                dateColumn,
                MediaStore.Images.Media._ID + "=?",
                arrayOf(imageId),
                null
        )
        if (dateCursor != null && dateCursor.moveToFirst()) {
            val columnIndex = dateCursor.getColumnIndex(dateColumn[0])
            val date = dateCursor.getLong(columnIndex)
            dateCursor.close()
            return date.toString()
        } else {
            dateCursor.close()
            return ""
        }
    }

    /**
     * [retrieveOrientation] retrieve orientation information for the image
     * */
    private fun retrieveOrientation(cursor: Cursor, context: Context): String? {
        val imageId = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media._ID))
        //Orientation
        val orientationColumn = arrayOf(MediaStore.Images.Media.ORIENTATION)
        val orientationCursor = context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                orientationColumn,
                MediaStore.Images.Media._ID + "=?",
                arrayOf(imageId),
                null
        )
        if (orientationCursor != null && orientationCursor.moveToFirst()) {
            val columnIndex = orientationCursor.getColumnIndex(orientationColumn[0])
            val orientation = orientationCursor.getString(columnIndex)
            orientationCursor.close()
            return orientation
        } else {
            orientationCursor.close()
            return null
        }
    }
}