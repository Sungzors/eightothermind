package com.phdlabs.sungwon.a8chat_android.structure.camera.preview

import android.content.Intent
import android.graphics.Point
import android.widget.ImageView
import com.phdlabs.sungwon.a8chat_android.structure.core.BaseController
import com.phdlabs.sungwon.a8chat_android.structure.core.BaseView

/**
 * Created by paix on 1/15/18.
 */
interface PreviewContract {

    interface View:BaseView<Controller> {
        fun getPreviewLayout(): ImageView
        fun getScreenSize():Pair<Int, Int>
        fun feedback(message: String)
    }

    interface Controller: BaseController {
        fun loadImagePreview(filePath: String?)
        fun saveImageToGallery()
        fun requestStoragePermissions()
    }

}