package com.phdlabs.sungwon.a8chat_android.structure.camera.editing


import android.graphics.Bitmap
import android.widget.ImageView
import com.ahmedadeltito.photoeditorsdk.PhotoEditorSDK
import com.phdlabs.sungwon.a8chat_android.structure.core.BaseController
import com.phdlabs.sungwon.a8chat_android.structure.core.BaseView

/**
 * Created by JPAM on 1/15/18.
 * EditingActivity Contract
 */
interface EditingContract {

    interface View : BaseView<Controller> {
        /*Photo*/
        fun getPreviewLayout(): ImageView
        fun getPhotoEditor(): PhotoEditorSDK

        /*Toast*/
        fun feedback(message: String)

        /*Activity*/
        var activity:EditingActivity?

        /*Navigation*/
        var isFromCameraRoll: Boolean
    }

    interface Controller : BaseController {
        /*load image from path*/
        fun loadImagePreview(filePath: String?)

        /*save image*/
        fun saveImageToGallery()

        /*request permissions*/
        fun requestStoragePermissions()

        /*Editing colors*/
        fun collectEditingColors(): ArrayList<Int>

        /*Photo SDK Controller*/
        fun addImageToPhotoSDK(image: Bitmap)
        fun addTextToPhotoSDK(string: String, colorCodeTextView: Int)
        fun clearAllViews()
        fun undoViews()
        fun eraseDrawing()
    }

}