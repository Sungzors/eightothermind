package com.phdlabs.sungwon.a8chat_android.structure.camera.editing

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.ahmedadeltito.photoeditorsdk.OnPhotoEditorSDKListener
import com.ahmedadeltito.photoeditorsdk.PhotoEditorSDK
import com.ahmedadeltito.photoeditorsdk.ViewType
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import com.phdlabs.sungwon.a8chat_android.utility.adapter.BaseRecyclerAdapter
import com.phdlabs.sungwon.a8chat_android.utility.adapter.BaseViewHolder
import com.phdlabs.sungwon.a8chat_android.utility.camera.CameraControl
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_camera_preview.*
import kotlinx.android.synthetic.main.view_camera_control_close.*
import kotlinx.android.synthetic.main.view_camera_control_editing.*
import kotlinx.android.synthetic.main.view_camera_control_save.*
import kotlinx.android.synthetic.main.view_camera_control_send.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by paix on 1/15/18.
 * [EditingActivity] shows the picture taken from the [NormalFragment]
 * or the picture selected from the [CameraRollFragment]
 */
class EditingActivity : CoreActivity(),
        EditingContract.View,
        View.OnClickListener,
        OnPhotoEditorSDKListener {

    /*Controller*/
    override lateinit var controller: EditingContract.Controller

    /*Layout*/
    override fun layoutId(): Int = R.layout.activity_camera_preview

    override fun contentContainerId(): Int = 0

    /*Context*/
    override var activity: EditingActivity? = this

    /*Properties*/
    //File
    private var imgFilePath: String? = null
    //Colors
    private var colorPickerColors: ArrayList<Int>? = null
    private var colorCodeTextView: Int = -1
    //Photo Editor
    private lateinit var photoEditorSDK: PhotoEditorSDK
    //Adapters
    private lateinit var colorPickerAdapter: BaseRecyclerAdapter<Int, BaseViewHolder>


    /*LifeCycle*/
    override

    fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*Controller init*/
        EditingActivityController(this)
        /*Load image*/
        imgFilePath = intent.extras.getString(Constants.CameraIntents.IMAGE_FILE_PATH)
        imgFilePath?.let {
            controller.loadImagePreview(it)
        }
        /*Resources*/
        colorPickerColors = controller.collectEditingColors()
    }

    override fun onResume() {
        super.onResume()
        controller.resume()
        setupUI()
    }

    override fun onStop() {
        super.onStop()
        controller.stop()
        restoreUI()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == Constants.PermissionsReqCode.WRITE_EXTERNAL_REQ_CODE) {
            if (grantResults.size != 1 || grantResults.get(0) != PackageManager.PERMISSION_GRANTED) {
                showError(getString(R.string.request_write_external_permission))
            } else {
                controller.saveImageToGallery()
            }
        } else {

            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result: CropImage.ActivityResult = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                //Complete filepath
                imgFilePath = result.uri.toString()
                //File path for displaying image with picasso
                val resultUri = result.uri.toString().substring(7)
                //Load image in UI
                controller.loadImagePreview(resultUri)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                //Error
                showError(result.error.localizedMessage)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    /*User Interface*/
    private fun setupUI() {
        //Switch close & back controls
        iv_camera_close.visibility = View.GONE
        iv_camera_back.visibility = View.VISIBLE
        cc_close_back.setOnClickListener(this)
        //Font
        val editingFont: Typeface = Typeface.createFromAsset(assets, "Eventtus-Icons.ttf")
        clear_all_tv.setTypeface(editingFont)
        clear_all_tv.setOnClickListener(this)
        iv_undo.setOnClickListener(this)
        delete_tv.setTypeface(editingFont)
        //Photo editor SDK
        photoEditorSDK = PhotoEditorSDK.PhotoEditorSDKBuilder(this)
                .parentView(parent_image_rl)
                .childView(photo_edit_iv)
                .deleteView(delete_rl)
                .brushDrawingView(drawing_view)//not used
                .buildPhotoEditorSDK()
        photoEditorSDK.setOnPhotoEditorSDKListener(this)
        //Drawing
        done_drawing_tv.setOnClickListener(this)
        erase_drawing_tv.setOnClickListener(this)
        //Save
        iv_camera_save.setOnClickListener(this)
        //Editing controls
        ll_camera_text.setOnClickListener(this)
        ll_camera_crop.setOnClickListener(this)
        ll_camera_draw.setOnClickListener(this)
        //Send
        iv_camera_send.setOnClickListener(this)
        //UI top & bottom shadows

    }

    /**
     * [restoreUI] restores User Interface on Activity exit
     * */
    private fun restoreUI() {
        //Restore close & back controls for Camera View
        iv_camera_close.visibility = View.VISIBLE
        iv_camera_back.visibility = View.GONE
    }

    /**
     * [updateView] updates shadow components
     * when the user is editing
     * */
    private fun updateView(visibility: Int) {
        top_shadow.visibility = visibility
        top_parent_rl.visibility = visibility
        bottom_shadow.visibility = visibility
        bottom_parent_rl.visibility = visibility
    }

    /**
     * [openAddTextPopUpWindow]
     * for adding text to the image through a pop up window
     * */
    private fun openAddTextPopUpWindow(text: String, colorCode: Int) {
        colorCodeTextView = colorCode
        //Layout
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val addTextPopupWindowRootView = inflater.inflate(R.layout.view_camera_add_text, null)
        val addTextColorPickerRecyclerView = addTextPopupWindowRootView.findViewById(R.id.add_text_color_picker_recycler_view) as RecyclerView
        val doneTextView = addTextPopupWindowRootView.findViewById(R.id.add_text_done_tv) as TextView
        val addTextEditText = addTextPopupWindowRootView.findViewById(R.id.add_text_edit_text) as TextView
        //Color picker adapter
        colorPickerAdapter = object : BaseRecyclerAdapter<Int, BaseViewHolder>() {
            override fun onBindItemViewHolder(viewHolder: BaseViewHolder?, data: Int?, position: Int, type: Int) {
                //Selectable colors
                val colorPickerView: View = viewHolder!!.get(R.id.color_picker_view)
                colorPickerView.setOnClickListener({
                    data?.let {
                        //Text
                        addTextEditText.setTextColor(it)
                        colorCodeTextView = it
                    }
                })
                //Bind item
                data?.let {
                    val biggerCircle: ShapeDrawable = ShapeDrawable(OvalShape())
                    biggerCircle.intrinsicHeight = 20
                    biggerCircle.intrinsicWidth = 20
                    biggerCircle.bounds = Rect(0, 0, 20, 20)
                    biggerCircle.paint.color = it
                    val smallerCircle: ShapeDrawable = ShapeDrawable(OvalShape())
                    smallerCircle.intrinsicHeight = 5
                    smallerCircle.intrinsicWidth = 5
                    smallerCircle.bounds = Rect(0, 0, 5, 5)
                    smallerCircle.paint.color = Color.WHITE
                    smallerCircle.setPadding(10, 10, 10, 10)
                    val drawables = arrayOf(smallerCircle, biggerCircle)
                    val layerDrawable: LayerDrawable = LayerDrawable(drawables)
                    colorPickerView.setBackgroundDrawable(layerDrawable)
                }
            }

            override fun getItemCount(): Int {
                colorPickerColors?.let {
                    return it.count()
                }
                return 0
            }

            override fun viewHolder(inflater: LayoutInflater?, parent: ViewGroup?, type: Int): BaseViewHolder =
                    object : BaseViewHolder(R.layout.color_picker_item_list, inflater!!, parent) {}
        }
        //Layout manager
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        addTextColorPickerRecyclerView.layoutManager = layoutManager
        addTextColorPickerRecyclerView.setHasFixedSize(true)
        //Data
        colorPickerAdapter.setItems(colorPickerColors)
        //Set Adapter
        addTextColorPickerRecyclerView.adapter = colorPickerAdapter
        //Data validation
        if (!text.isBlank()) {
            addTextEditText.setText(text)
            addTextEditText.setTextColor(if (colorCode == -1) resources.getColor(R.color.white) else colorCode)
        }
        //Popup Window
        val popupWindow: PopupWindow = PopupWindow(this)
        popupWindow.contentView = addTextPopupWindowRootView
        popupWindow.width = LinearLayout.LayoutParams.MATCH_PARENT
        popupWindow.height = LinearLayout.LayoutParams.MATCH_PARENT
        popupWindow.isFocusable = true
        popupWindow.setBackgroundDrawable(null)
        popupWindow.showAtLocation(addTextPopupWindowRootView, Gravity.TOP, 0, 0)
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        doneTextView.setOnClickListener {
            controller.addTextToPhotoSDK(addTextEditText.text.toString().trim(), colorCodeTextView)
            val inputMethodManagerLocal = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManagerLocal.hideSoftInputFromWindow(it.windowToken, 0)
            popupWindow.dismiss()
        }
    }

    /***/
    private fun updateBrushDrawingView(brushDrawingMode: Boolean) {
        photoEditorSDK.setBrushDrawingMode(brushDrawingMode)
        if (brushDrawingMode) {
            //Views
            drawing_view_color_picker_recycler_view.visibility = View.VISIBLE
            done_drawing_tv.visibility = View.VISIBLE
            erase_drawing_tv.visibility = View.VISIBLE
            //Color picker adapter
            colorPickerAdapter = object : BaseRecyclerAdapter<Int, BaseViewHolder>() {
                override fun onBindItemViewHolder(viewHolder: BaseViewHolder?, data: Int?, position: Int, type: Int) {
                    //Selectable colors
                    val colorPickerView: View = viewHolder!!.get(R.id.color_picker_view)
                    colorPickerView.setOnClickListener({
                        data?.let {
                            //Brush
                            photoEditorSDK.brushColor = it
                        }
                    })
                    //Bind item
                    data?.let {
                        val biggerCircle: ShapeDrawable = ShapeDrawable(OvalShape())
                        biggerCircle.intrinsicHeight = 20
                        biggerCircle.intrinsicWidth = 20
                        biggerCircle.bounds = Rect(0, 0, 20, 20)
                        biggerCircle.paint.color = it
                        val smallerCircle: ShapeDrawable = ShapeDrawable(OvalShape())
                        smallerCircle.intrinsicHeight = 5
                        smallerCircle.intrinsicWidth = 5
                        smallerCircle.bounds = Rect(0, 0, 5, 5)
                        smallerCircle.paint.color = Color.WHITE
                        smallerCircle.setPadding(10, 10, 10, 10)
                        val drawables = arrayOf(smallerCircle, biggerCircle)
                        val layerDrawable: LayerDrawable = LayerDrawable(drawables)
                        colorPickerView.setBackgroundDrawable(layerDrawable)
                    }
                }

                override fun getItemCount(): Int {
                    colorPickerColors?.let {
                        return it.count()
                    }
                    return 0
                }

                override fun viewHolder(inflater: LayoutInflater?, parent: ViewGroup?, type: Int): BaseViewHolder =
                        object : BaseViewHolder(R.layout.color_picker_item_list, inflater!!, parent) {}
            }
            //Layout Manager
            val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            drawing_view_color_picker_recycler_view.layoutManager = linearLayoutManager
            drawing_view_color_picker_recycler_view.setHasFixedSize(true)
            //Data
            colorPickerAdapter.setItems(colorPickerColors)
            //Set adapter
            drawing_view_color_picker_recycler_view.adapter = colorPickerAdapter
        } else {
            updateView(View.VISIBLE)
            drawing_view_color_picker_recycler_view.visibility = View.GONE
            done_drawing_tv.visibility = View.GONE
            erase_drawing_tv.visibility = View.GONE
        }
    }

    /*Photo loading*/
    override fun getPreviewLayout(): ImageView = photo_edit_iv

    /*Photo Editor*/
    override fun getPhotoEditor(): PhotoEditorSDK = photoEditorSDK


    /*On Click listener*/
    override fun onClick(p0: View?) {
        when (p0) {
        /*Close Activity*/
            cc_close_back -> {
                onBackPressed()
            }
        /*Save picture*/
            iv_camera_save -> {
                if (ContextCompat.checkSelfPermission(this,
                                Constants.AppPermissions.WRITE_EXTERNAL) != PackageManager.PERMISSION_GRANTED) {
                    controller.requestStoragePermissions()
                } else {
                    controller.saveImageToGallery()
                }
            }
        /*Add Text*/
            ll_camera_text -> {
                openAddTextPopUpWindow("", -1)
            }
        /*Crop Image*/
            ll_camera_crop -> {
                CropImage
                        .activity(Uri.parse("file://" + imgFilePath))
                        .start(this)
            }
        /*Add Drawing*/
            ll_camera_draw -> {
                updateBrushDrawingView(true)
            }
        /*Done Drawing*/
            done_drawing_tv -> {
                updateBrushDrawingView(false)
            }
        /*Erase Drawing*/
            erase_drawing_tv -> {
                controller.eraseDrawing()
            }
        /*Clear All Changes*/
            clear_all_tv -> {
                controller.clearAllViews()
            }

        /*Undo Last Change*/
            iv_undo -> {
                controller.undoViews()
            }
        }
    }

    /*Photo Editor Listeners -> Required methods*/
    override fun onEditTextChangeListener(text: String?, colorCode: Int) {
        text?.let {
            openAddTextPopUpWindow(it, colorCode)
        }
    }

    override fun onAddViewListener(viewType: ViewType?, numberOfAddedViews: Int) {
        /*If changes were made*/
        if (numberOfAddedViews > 0) {
            //Undo button
            vccs_undo_layout.visibility = View.VISIBLE
            iv_undo.visibility = View.VISIBLE
            undo_text_tv.visibility = View.VISIBLE
        }
        /*Editing View Type*/
        when (viewType) {
            ViewType.BRUSH_DRAWING -> println("BRUSH_DRAWING: " + "onAddViewListener")
            ViewType.IMAGE -> println("IMAGE: " + "onAddViewListener")
            ViewType.TEXT -> println("TEXT: " + "onAddViewListener")
            else -> println("UNKOWN: " + "onAddViewListener")
        }
    }

    override fun onRemoveViewListener(numberOfAddedViews: Int) {
        /*If changes were made*/
        if (numberOfAddedViews == 0) {
            //Undo button
            vccs_undo_layout.visibility = View.GONE
            iv_undo.visibility = View.GONE
            undo_text_tv.visibility = View.GONE
        }
    }

    override fun onStartViewChangeListener(viewType: ViewType?) {
        when (viewType) {
            ViewType.BRUSH_DRAWING -> println("BRUSH_DRAWING: " + "onStartViewChangeListener")
            ViewType.IMAGE -> println("IMAGE: " + "onStartViewChangeListener")
            ViewType.TEXT -> println("TEXT: " + "onStartViewChangeListener")
            else -> println("UNKNOWN: " + "onStartViewChangeListener")
        }
    }

    override fun onStopViewChangeListener(viewType: ViewType?) {
        when (viewType) {
            ViewType.BRUSH_DRAWING -> println("BRUSH_DRAWING: " + "onStopViewChangeListener")
            ViewType.IMAGE -> println("IMAGE: " + "onStopViewChangeListener")
            ViewType.TEXT -> println("TEXT: " + "onStopViewChangeListener")
            else -> println("UNKNOWN: " + "onStopViewChangeListener")
        }
    }

    /*Utilities*/
    override fun feedback(message: String) {
        showToast(message)
    }

}