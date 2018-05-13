package com.phdlabs.sungwon.a8chat_android.structure.camera.filters

import android.app.Activity
import android.app.LoaderManager
import android.content.Loader
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.structure.camera.CameraContract
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import com.phdlabs.sungwon.a8chat_android.utility.DeviceInfo
import com.phdlabs.sungwon.a8chat_android.utility.adapter.BaseRecyclerAdapter
import com.phdlabs.sungwon.a8chat_android.utility.adapter.BaseViewHolder
import com.phdlabs.sungwon.a8chat_android.utility.adapter.ViewMap
import com.phdlabs.sungwon.a8chat_android.utility.camera.CameraControl
import com.phdlabs.sungwon.a8chat_android.utility.camera.PhotoFilterAsyncLoader
import kotlinx.android.synthetic.main.activity_camera_filters.*
import kotlinx.android.synthetic.main.progress_view.view.*
import org.apache.commons.io.FileUtils

/**
 * Created by JPAM on 5/9/18.
 * Image Filtering for Camera App
 */
class ImageFilterActivity : CoreActivity(), CameraContract.Filters.View, LoaderManager.LoaderCallbacks<List<Pair<String, String>?>?> {

    /*Controller*/
    override lateinit var controller: CameraContract.Filters.Controller

    /*Layout*/
    override fun layoutId(): Int = R.layout.activity_camera_filters

    override fun contentContainerId(): Int = 0

    /*Properties*/
    private var imgFilePath: String? = null
    private lateinit var mFilterAdapter: BaseRecyclerAdapter<Pair<String, String>?, BaseViewHolder>
    private var mFilterList: MutableList<Pair<String, String>?>? = null

    /*LifeCycle*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ImageFilterActController(this)
        //Get Photo
        intent.getStringExtra(Constants.CameraIntents.IMAGE_FILE_PATH)?.let {
            imgFilePath = it
            mFilterList = mutableListOf()
        } ?: run {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        imgFilePath?.let {
            setupPhoto(it)
            setupFilters()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    /**
     * [setupPhoto]
     * Setup Photo for applying filters
     * */
    private fun setupPhoto(imgFilePath: String) {
        //Filtered image rotation
        var imageRotation = 0f
        if (DeviceInfo.INSTANCE.isWarningDevice(Build.MODEL)) {
            imageRotation = 90f
        }
        val bm = CameraControl.instance.getImageFromPath(this, imgFilePath)
        acf_photo_iv.setImageBitmap(bm)
        acf_photo_iv.rotation = imageRotation
    }


    /**
     * [setupFilters]
     * Setup photo with filter previews
     * */
    private fun setupFilters() {
        //Filtered image rotation
        var imageRotation = 0f
        if (DeviceInfo.INSTANCE.isWarningDevice(Build.MODEL)) {
            imageRotation = 90f
        }
        //Filter Recycler
        mFilterAdapter = object : BaseRecyclerAdapter<Pair<String, String>?, BaseViewHolder>() {
            override fun onBindItemViewHolder(viewHolder: BaseViewHolder?, data: Pair<String, String>?, position: Int, type: Int) {
                val photoFilter = viewHolder?.get<ImageView>(R.id.vfi_iv)
                val filterName = viewHolder?.get<TextView>(R.id.vfi_name)
                photoFilter?.setImageURI(Uri.parse(data?.first))
                photoFilter?.rotation = imageRotation
                filterName?.text = data?.second
            }

            override fun viewHolder(inflater: LayoutInflater?, parent: ViewGroup?, type: Int): BaseViewHolder {
                return object : BaseViewHolder(R.layout.view_filter_image, inflater!!, parent) {
                    override fun addClicks(views: ViewMap?) {
                        super.addClicks(views)
                    }
                }
            }
        }

        mFilterAdapter.setItems(mFilterList)
        acf_filter_rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        acf_filter_rv.adapter = mFilterAdapter
        //Load Filtered images
        loaderManager.initLoader(0, null, this).forceLoad()
    }

    override fun onCreateLoader(p0: Int, p1: Bundle?): Loader<List<Pair<String, String>?>?> =
            PhotoFilterAsyncLoader(this, imgFilePath)

    override fun onLoadFinished(p0: Loader<List<Pair<String, String>?>?>?, p1: List<Pair<String, String>?>?) {
        mFilterList?.let {
            //it.clear()
            p1?.let {

                for (filteredPhoto in it) {
                    mFilterList?.add(filteredPhoto)
                }


                /*Picture count subtitle & toolbar swap*/
                mFilterList?.size?.let {
                    if (it > 0) {
                        //fcr_refresh.isRefreshing = false
                        /*Setup RecyclerView with fresh data*/
                        mFilterAdapter.setItems(mFilterList)
                        mFilterAdapter.notifyDataSetChanged()
                    }
                }

            }
        }
    }

    override fun onLoaderReset(p0: Loader<List<Pair<String, String>?>?>?) {
        mFilterList?.clear()
    }

}