package com.phdlabs.sungwon.a8chat_android.utility.camera

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import com.phdlabs.sungwon.a8chat_android.model.media.GalleryItem

/**
 * Created by JPAM on 11/8/17.
 * Custom App File Provider
 */
class GalleryFileProvider : FileProvider() {

    /*Singleton Holder*/
    private object Holder {
        var INSTANCE = GalleryFileProvider()
    }

    /*Companion*/
    companion object {
        //Singleton INSTANCE
        val INSTANCE: GalleryFileProvider by lazy { Holder.INSTANCE }
    }

    enum class GALLERYFILETYPE {
        PHOTO,
        VIDEO
    }

    /*Properties*/
    var mOffset = 0
    var mItemsPerPage = 0

    /**
     * Content Offset
     * */
    fun setContentOffset(offset: Int) {
        mOffset = offset
    }

    private fun getContentOffset(): String = mOffset.toString()

    /**
     * Number of pages per item
     * */
    fun setNumberOfItemsPerPage(items: Int) {
        mItemsPerPage = items
    }

    private fun getNumberOfItemsPerPage(): Int = mItemsPerPage

    /**
     * [getAlbumVideos] Fetches Videos from gallery
     * via single query
     * @param context
     * @return List
     * */
    fun getAlbumVideos(context: Context): List<GalleryItem> {
        val projection = arrayOf(
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DATE_TAKEN,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.MINI_THUMB_MAGIC
        )
        val cursor = context.contentResolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                null
        )
        val result: ArrayList<GalleryItem> = ArrayList(cursor.count)

        if (cursor.moveToFirst()) {
            do {
                //Video
                val fullVideoUri = uriToFullVideo(cursor, context)
                //Date
                val dateTaken = retrieveDateTaken(cursor, context, GALLERYFILETYPE.VIDEO)
                val galleryItem = GalleryItem(fullVideoUri.toString(), null, dateTaken, null)
                result.add(galleryItem)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return result
    }

    /**
     * [getAlbumImages] Fetches both full size images
     * via a single query
     * @param context
     * @return List
     * */
    fun getAlbumImages(context: Context): List<GalleryItem> {
        //Full photo info
        val fullPhotoProjection = arrayOf(
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.ORIENTATION
        )

        //Full photo Cursor
        val fullPhotoCursor = context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // Directory
                fullPhotoProjection, // Which columns to return
                null, // Return all rows
                null,
                MediaStore.Images.Media.DATE_ADDED + " limit " + getNumberOfItemsPerPage() + " offset " + getContentOffset()
        )

        //Extract proper columns
        val fullPhotoResult: ArrayList<GalleryItem> = ArrayList<GalleryItem>(fullPhotoCursor.count)

        if (fullPhotoCursor.moveToFirst()) {
            do {
                //Full Image
                val fullImageUri = uriToFullImage(fullPhotoCursor, context)
                //Date
                val dateTaken = retrieveDateTaken(fullPhotoCursor, context, GALLERYFILETYPE.PHOTO)
                //Thumbnail
                val thumbnail = retrieveThumbnailPath(fullImageUri.second, context)
                //Orientation
                val imageOrientation = retrieveOrientation(fullPhotoCursor, context)
                //Gallery Item
                val galleryPhoto = GalleryItem(fullImageUri.first, thumbnail, dateTaken, imageOrientation)
                //Result
                fullPhotoResult.add(galleryPhoto)
            } while (fullPhotoCursor.moveToNext())
        }
        fullPhotoCursor.close()
        return fullPhotoResult

    }

    /**
     * [uriToFullImage] Get the path to the full image for a given thumbnail
     * @param cursor for the current query
     * @param context
     * @return file path [Uri] + imageId [String]
     * */
    private fun uriToFullImage(cursor: Cursor, context: Context): Pair<String, String> {
        val imageId = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media._ID))
        //Request image
        val filepathColumn = arrayOf(MediaStore.Images.Media.DATA)
        val imageCursor = context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                filepathColumn,
                MediaStore.Images.Media._ID + "=?",
                arrayOf(imageId),
                null
        )
        if (imageCursor != null && imageCursor.moveToFirst()) {
            val columnIndex = imageCursor.getColumnIndex(filepathColumn[0])
            val filePath = imageCursor.getString(columnIndex)
            imageCursor.close()
            return Pair(filePath, imageId)
        } else {
            imageCursor.close()
            return Pair("", "")
        }
    }

    /**
     * [uriToFullVideo] Get the path to the full video for a given video
     * @param cursor for the current query
     * @param context
     * @return file path [Uri]
     * */
    private fun uriToFullVideo(cursor: Cursor, context: Context): Uri {
        val videoId = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media._ID))
        //Request video
        val filePathColumn = arrayOf(MediaStore.Video.Media.DATA)
        val videoCursor = context.contentResolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                filePathColumn,
                MediaStore.Video.Media._ID + "+?",
                arrayOf(videoId),
                null
        )
        if (videoCursor != null && videoCursor.moveToFirst()) {
            val columnIndex = videoCursor.getColumnIndex(filePathColumn[0])
            val filePath = videoCursor.getString(columnIndex)
            videoCursor.close()
            return Uri.parse(filePath)
        } else {
            videoCursor.close()
            return Uri.parse("")
        }

    }

    /**
     * [retrieveDateTaken] retrieve date information for the image
     * */
    private fun retrieveDateTaken(cursor: Cursor, context: Context, fileType: GALLERYFILETYPE): String {

        val imageId: String
        val dateCursor: Cursor

        if (GALLERYFILETYPE.PHOTO == fileType) {
            imageId = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media._ID))
            //Request Date
            val dateColumn = arrayOf(MediaStore.Images.Media.DATE_TAKEN)
            dateCursor = context.contentResolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    dateColumn,
                    MediaStore.Images.Media._ID + "=?",
                    arrayOf(imageId),
                    null
            )
            return if (dateCursor != null && dateCursor.moveToFirst()) {
                val columnIndex = dateCursor.getColumnIndex(dateColumn[0])
                val date = dateCursor.getLong(columnIndex)
                dateCursor.close()
                date.toString()
            } else {
                dateCursor.close()
                ""
            }

        } else if (GALLERYFILETYPE.VIDEO == fileType) {
            imageId = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media._ID))
            //Request Date
            val dateColumn = arrayOf(MediaStore.Video.Media.DATE_TAKEN)
            dateCursor = context.contentResolver.query(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    dateColumn,
                    MediaStore.Video.Media._ID + "=?",
                    arrayOf(imageId),
                    null
            )
            return if (dateCursor != null && dateCursor.moveToFirst()) {
                val columnIndex = dateCursor.getColumnIndex(dateColumn[0])
                val date = dateCursor.getLong(columnIndex)
                dateCursor.close()
                date.toString()
            } else {
                dateCursor.close()
                ""
            }
            /*Default*/
        } else {
            return ""
        }
    }

    /**
     * [retrieveThumbnailPath] retrieve path for thumbnail
     * */
    private fun retrieveThumbnailPath(imageId: String, context: Context): String? {
        //Thumbnail
        val thumbnailColumn = arrayOf(MediaStore.Images.Thumbnails.DATA)
        val thumbnailCursor = context.contentResolver.query(
                MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
                thumbnailColumn,
                MediaStore.Images.Thumbnails.IMAGE_ID + "=?",
                arrayOf(imageId),
                null
        )
        return if (thumbnailCursor != null && thumbnailCursor.moveToNext()) {
            val columnIndex = thumbnailCursor.getColumnIndex(thumbnailColumn[0])
            val thumbnail = thumbnailCursor.getString(columnIndex)
            thumbnailCursor.close()
            thumbnail
        } else {
            thumbnailCursor?.close()
            null
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
        return if (orientationCursor != null && orientationCursor.moveToFirst()) {
            val columnIndex = orientationCursor.getColumnIndex(orientationColumn[0])
            val orientation = orientationCursor.getString(columnIndex)
            orientationCursor.close()
            orientation
        } else {
            orientationCursor.close()
            null
        }
    }
}