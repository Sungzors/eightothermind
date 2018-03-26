package com.phdlabs.sungwon.a8chat_android.structure.camera.editing

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.support.v13.app.ActivityCompat
import android.support.v4.content.ContextCompat
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.structure.camera.share.ShareCameraMedia
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import com.phdlabs.sungwon.a8chat_android.utility.DeviceInfo
import com.phdlabs.sungwon.a8chat_android.utility.SuffixDetector
import com.phdlabs.sungwon.a8chat_android.utility.camera.CameraControl
import com.squareup.picasso.Picasso
import java.io.File

/**
 * Created by JPAM on 1/15/18.
 * [EditingActivityController] manages business logic
 * for [EditingActivity], including [PhotoEditorSDK]
 */
class EditingActivityController(val mView: EditingContract.View) : EditingContract.Controller {

    /*LOG*/
    private val TAG = "Camera Preview"

    /*Properties*/
    lateinit var imageFilePath: String

    /*Initialization*/
    init {
        mView.controller = this
    }

    /*LifeCycle*/
    override fun start() {
    }

    override fun resume() {
    }

    override fun pause() {
    }

    override fun stop() {
    }

    override fun requestStoragePermissions() {
        val whatPermissions = arrayOf(Constants.AppPermissions.WRITE_EXTERNAL)
        mView.getContext()?.let {
            //Request Permissions
            if (ContextCompat.checkSelfPermission(it, whatPermissions.get(0)) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(mView.getContext() as EditingActivity,
                        whatPermissions, Constants.PermissionsReqCode.WRITE_EXTERNAL_REQ_CODE)
            }
        }
    }


    /*Load image preview*/
    override fun loadImagePreview(filePath: String?) {

        filePath?.let {
            //Load Image Preview
            imageFilePath = it
            if (DeviceInfo.INSTANCE.isWarningDevice(Build.MODEL)) {
                var presentWithRotation: Float = 90f
                if (mView.isFromCameraRoll) {
                    presentWithRotation = 0f
                }
                Picasso.with(mView.getContext())
                        .load(File(it))
                        .rotate(presentWithRotation) //Full screen //TODO: Probably 90 degrees
                        .into(mView.getPreviewLayout())
            } else {
                Picasso.with(mView.getContext())
                        .load(File(it))
                        .into(mView.getPreviewLayout())
            }
        }
    }

    /*Save file*/
    override fun saveImageToGallery() {
        imageFilePath.let {
            mView.activity?.let {
                CameraControl.instance.addToGallery(
                        it,
                        mView.getPhotoEditor().saveImageWithSuffix(
                                "8",
                                CameraControl.instance.mediaFileNaming())
                )
                it.setResult(Activity.RESULT_OK)
                //User feedback
                mView.activity?.resources?.let {
                    mView.feedback(it.getString(R.string.cameraroll_saved_photo))
                }
            }
        }
    }

    /*Collect Editing colors resources*/
    override fun collectEditingColors(): ArrayList<Int> {
        val colorPickerColors: ArrayList<Int> = arrayListOf()
        mView.activity?.let {
            colorPickerColors.add(it.resources.getColor(R.color.black))
            colorPickerColors.add(it.resources.getColor(R.color.white))
            colorPickerColors.add(it.resources.getColor(R.color.blue_color_picker))
            colorPickerColors.add(it.resources.getColor(R.color.brown_color_picker))
            colorPickerColors.add(it.resources.getColor(R.color.green_color_picker))
            colorPickerColors.add(it.resources.getColor(R.color.orange_color_picker))
            colorPickerColors.add(it.resources.getColor(R.color.red_color_picker))
            colorPickerColors.add(it.resources.getColor(R.color.red_orange_color_picker))
            colorPickerColors.add(it.resources.getColor(R.color.sky_blue_color_picker))
            colorPickerColors.add(it.resources.getColor(R.color.violet_color_picker))
            colorPickerColors.add(it.resources.getColor(R.color.yellow_color_picker))
            colorPickerColors.add(it.resources.getColor(R.color.yellow_green_color_picker))
        }
        return colorPickerColors
    }

    /*PHOTO EDITOR Controller*/

    /**
     * [addImageToPhotoSDK] adds image to [photoEditorSDK]
     * for editing & rendering
     * */
    override fun addImageToPhotoSDK(image: Bitmap) {
        mView.getPhotoEditor().addImage(image)
    }

    /**
     * [addTextToPhotoSDK] adds text o image managed by
     * [photoEditorSDK] for editing & rendering
     * */
    override fun addTextToPhotoSDK(string: String, colorCodeTextView: Int) {
        mView.getPhotoEditor().addText(string, colorCodeTextView)
    }

    /**
     * [clearAllViews] in [photoEditorSDK]
     * */
    override fun clearAllViews() {
        undoViews()
        mView.getPhotoEditor().clearBrushAllViews()
        mView.getPhotoEditor().clearAllViews()
    }

    /**
     * [undoViews] in [photoEditorSDK]
     * */
    override fun undoViews() {
        mView.getPhotoEditor().viewUndo()
    }

    /**
     * [eraseDrawing] in [photoEditorSDK]
     * */
    override fun eraseDrawing() {
        mView.getPhotoEditor().brushEraser()
    }

    /**
     * Send Image to [ShareCameraMedia]
     * to be shared
     * */
    override fun sendImage() {
        //Save Image to Gallery
        imageFilePath.let {
            mView.activity?.let {
                CameraControl.instance.addToGallery(
                        it,
                        mView.getPhotoEditor().saveImageWithSuffix(
                                "8",
                                CameraControl.instance.mediaFileNaming())
                )
                it.setResult(Activity.RESULT_OK)
                /**Transition to [ShareCameraMedia]*/
                val intent = Intent(it, ShareCameraMedia::class.java)
                intent.putExtra(Constants.CameraIntents.IMAGE_FILE_PATH, imageFilePath)
                it.startActivityForResult(intent, Constants.RequestCodes.SHARE_MEDIA)
            }
        }
    }


}