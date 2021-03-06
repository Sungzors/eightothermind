package com.phdlabs.sungwon.a8chat_android.structure.camera.filters

import android.app.Activity
import android.app.LoaderManager
import android.content.Intent
import android.content.Loader
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.structure.camera.CameraContract
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import com.phdlabs.sungwon.a8chat_android.utility.RoundedCornersTransform
import com.phdlabs.sungwon.a8chat_android.utility.adapter.BaseRecyclerAdapter
import com.phdlabs.sungwon.a8chat_android.utility.adapter.BaseViewHolder
import com.phdlabs.sungwon.a8chat_android.utility.adapter.ViewMap
import com.phdlabs.sungwon.a8chat_android.utility.camera.CameraControl
import com.phdlabs.sungwon.a8chat_android.utility.camera.PhotoFilterAsyncLoader
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_camera_filters.*
import kotlinx.android.synthetic.main.view_camera_control_filters.*
import net.alhazmy13.imagefilter.ImageFilter

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
    private var mSelectedFilter: String? = null
    /*LifeCycle*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Controller
        ImageFilterActController(this)
        //Setup Clickers
        setupClickers()
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
        mSelectedFilter?.let {
            val intent = Intent()
            intent.putExtra(Constants.IntentKeys.FILTERS, mSelectedFilter)
            setResult(Activity.RESULT_OK, intent)
            finish()
        } ?: run {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    /**
     * [setupClickers]
     * On click listeners
     * */
    fun setupClickers() {
        /*Save to gallery*/
        iv_camera_back.setOnClickListener {
            onBackPressed()
        }

        /*Clear filter*/
        val editingFont: Typeface = Typeface.createFromAsset(assets, "Eventtus-Icons.ttf")
        clear_all_tv.typeface = editingFont
        clear_all_tv.setOnClickListener {
            acf_photo_iv.setImageURI(Uri.parse(imgFilePath))
            mSelectedFilter = null
        }

    }

    /**
     * [setupPhoto]
     * Setup Photo for applying filters
     * */
    private fun setupPhoto(imgFilePath: String) {
        //Filtered image rotation
        val bm = CameraControl.instance.getImageFromPath(this, imgFilePath)
        acf_photo_iv.setImageBitmap(bm)
    }


    /**
     * [setupFilters]
     * Setup photo with filter previews
     * */
    private fun setupFilters() {
        //Filter Recycler
        mFilterAdapter = object : BaseRecyclerAdapter<Pair<String, String>?, BaseViewHolder>() {
            override fun onBindItemViewHolder(viewHolder: BaseViewHolder?, data: Pair<String, String>?, position: Int, type: Int) {
                val photoFilter = viewHolder?.get<ImageView>(R.id.vfi_iv)
                val filterName = viewHolder?.get<TextView>(R.id.vfi_name)
                //Load photo
                Picasso.with(context).load("file://" + data?.first).transform(RoundedCornersTransform(20, 8)).into(photoFilter)
                //Load title
                filterName?.text = data?.second
            }

            override fun viewHolder(inflater: LayoutInflater?, parent: ViewGroup?, type: Int): BaseViewHolder {
                return object : BaseViewHolder(R.layout.view_filter_image, inflater!!, parent) {
                    override fun addClicks(views: ViewMap?) {
                        //Selected thumbnail will trigger a filter preview
                        views?.click {
                            imgFilePath?.let {
                                for (filter in FILTERS.values()) {
                                    if (getItem(adapterPosition)?.second == filter.filterName) {
                                        //Process original photo with filter
                                        acf_photo_iv.setImageBitmap(ImageFilter.applyFilter(
                                                CameraControl.instance.getImageFromPath(
                                                        this@ImageFilterActivity, it), filter.filter))
                                        mSelectedFilter = filter.name
                                    }
                                }
                            }
                        }
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

    /*Process filter preview*/
    override fun processFilter(filter: ImageFilter.Filter) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /*Load Filter thumbnails*/
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