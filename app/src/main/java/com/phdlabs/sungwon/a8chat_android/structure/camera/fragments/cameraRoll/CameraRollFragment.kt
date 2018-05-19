package com.phdlabs.sungwon.a8chat_android.structure.camera.fragments.cameraRoll

import android.app.LoaderManager
import android.content.Intent
import android.content.Loader
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Point
import android.media.ThumbnailUtils
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.model.media.GalleryItem
import com.phdlabs.sungwon.a8chat_android.structure.camera.fragments.CameraBaseFragment
import com.phdlabs.sungwon.a8chat_android.structure.camera.editing.EditingActivity
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import com.phdlabs.sungwon.a8chat_android.utility.adapter.BaseRecyclerAdapter
import com.phdlabs.sungwon.a8chat_android.utility.adapter.BaseViewHolder
import com.phdlabs.sungwon.a8chat_android.utility.adapter.EndlessRecyclerViewScrollListener
import com.phdlabs.sungwon.a8chat_android.utility.adapter.ViewMap
import com.phdlabs.sungwon.a8chat_android.utility.camera.CameraControl
import com.phdlabs.sungwon.a8chat_android.utility.camera.GalleryAsyncLoader
import com.phdlabs.sungwon.a8chat_android.utility.camera.GalleryFileProvider
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_cameraroll.*
import kotlinx.android.synthetic.main.toolbar.*


/**
 * Created by JPAM on 12/28/17.
 * [CameraRollFragment] showing available pictures sectioned by date
 * for the user to select & then preview after selection
 */
class CameraRollFragment : CameraBaseFragment(),
        LoaderManager.LoaderCallbacks<List<GalleryItem>>,
        View.OnClickListener {

    /*Properties*/
    private var mAdapter: BaseRecyclerAdapter<GalleryItem, BaseViewHolder>? = null
    private var mGalleryItems: ArrayList<GalleryItem> = ArrayList()
    private lateinit var mScrollListener: EndlessRecyclerViewScrollListener
    private lateinit var galleryAsyncLoader: GalleryAsyncLoader
    private lateinit var galleryProvider: GalleryFileProvider

    /*Companion*/
    companion object {
        fun create(): CameraRollFragment = CameraRollFragment()
    }

    /*Required*/
    override fun cameraLayoutId(): Int = R.layout.fragment_cameraroll

    override fun inOnCreateView(root: View?, container: ViewGroup?, savedInstanceState: Bundle?) {
        //If something needs to be added to the custom layout
    }

    /*LifeCycle*/
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //Toolbar setup -> Default
        toolbar_title_double_container.visibility = View.GONE
        toolbar_title_double_container
        toolbar_title.visibility = View.VISIBLE
        toolbar_title.text = getString(R.string.camera_left_tab)
        //Toolbar left action
        toolbar_leftoolbart_action.setImageDrawable(activity?.getDrawable(R.drawable.ic_back))
        setupClickListeners()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Gallery Provider
        galleryProvider = GalleryFileProvider.INSTANCE
        galleryProvider.setContentOffset(0)
        galleryProvider.setNumberOfItemsPerPage(40)
        //Gallery Loader
        galleryAsyncLoader = GalleryAsyncLoader(context!!, GalleryFileProvider.GALLERYFILETYPE.PHOTO, galleryProvider)

    }

    override fun onResume() {
        super.onResume()
        setupRecycler()
        //Load Images
        if (ContextCompat.checkSelfPermission(activity!!, Constants.AppPermissions.WRITE_EXTERNAL) != PackageManager.PERMISSION_GRANTED) {
            requestExternalStoragePermissions()
        } else {
            activity!!.loaderManager.initLoader(0, null, this).forceLoad()
        }
        userVisibleHint = false
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        if (menuVisible && isResumed) {
            userVisibleHint = true
            if (mGalleryItems.isEmpty()) {
                fcr_refresh.post { fcr_refresh.isRefreshing = true }
            }
        } else {
            userVisibleHint = false
        }
    }

    /**
     * [setupClickListeners] on the buttons
     * */
    private fun setupClickListeners() {
        toolbar_leftoolbart_action.setOnClickListener(this)
        val currentContext = this
        fcr_refresh.setColorSchemeResources(R.color.blue_color_picker, R.color.sky_blue_color_picker)
        fcr_refresh.setOnRefreshListener {
            activity?.let {
                //Refresh pictures
                if (ContextCompat.checkSelfPermission(it, Constants.AppPermissions.WRITE_EXTERNAL) != PackageManager.PERMISSION_GRANTED) {
                    requestExternalStoragePermissions()
                } else {
                    it.loaderManager.initLoader(0, null, currentContext).forceLoad()
                }
            }
        }
    }

    /**
     * [toolbarVisibility] will change depending on the amount of
     * available pictures
     * */
    private fun toolbarVisibility(photoCount: Int) {
        if (photoCount > 0) {
            //Hide original title
            toolbar_title.visibility = View.GONE
            //Show new Title
            toolbar_title_double_container.visibility = View.VISIBLE
            toolbar_title_double_top.visibility = View.VISIBLE
            toolbar_title_double_top.text = getString(R.string.camera_left_tab)
            //Show new Subtitle
            val toolbarSubtitle = photoCount.toString() + " " + "Photos"
            toolbar_title_double_bottom.visibility = View.VISIBLE
            toolbar_title_double_bottom.text = toolbarSubtitle
        } else {
            //Hide subtitle
            toolbar_title_double_container.visibility = View.GONE
            toolbar_title_double_top.visibility = View.GONE
            toolbar_title_double_bottom.text = ""
            toolbar_title_double_bottom.visibility = View.GONE
            //Show original title
            toolbar_title.visibility = View.GONE
        }
    }

    /**External storage permissions
     * Request external storage permissions
     * */
    private fun requestExternalStoragePermissions() {
        //Required permissions
        val whatPermissions = arrayOf(Constants.AppPermissions.WRITE_EXTERNAL)
        context?.let {
            //Request Permissions
            if (ContextCompat.checkSelfPermission(it, whatPermissions.get(0)) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(whatPermissions, Constants.PermissionsReqCode.WRITE_EXTERNAL_REQ_CODE)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == Constants.PermissionsReqCode.WRITE_EXTERNAL_REQ_CODE) {
            if (grantResults.size != 1 || grantResults.get(0) != PackageManager.PERMISSION_GRANTED) {
                showError(getString(R.string.request_write_external_permission))
            } else {
                activity!!.loaderManager.initLoader(0, null, this).forceLoad()
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    /*Setup recycler adapters*/
    private fun setupRecycler() {
        //Image scaling
        val displaySize = Point()
        activity?.windowManager?.defaultDisplay?.getSize(displaySize)
        val imageSquare = displaySize.x / 3

        //Adapter
        mAdapter = object : BaseRecyclerAdapter<GalleryItem, BaseViewHolder>() {

            override fun onBindItemViewHolder(viewHolder: BaseViewHolder?, data: GalleryItem?, position: Int, type: Int) {
                val imageView = viewHolder?.get<ImageView>(R.id.cr_iv_photo)
                viewHolder?.let {
                    context?.let {
                        //Picasso.with(it).load(data?.mThumbnailPath).centerInside().resize(imageSquare, imageSquare).into(imageView)
                        //imageView?.setImageBitmap(videoThumbnail(data?.mFullPath))
                        //imageView?.setImageBitmap(CameraControl.instance.getImageFromPath(context, data?.mThumbnailPath!!))
                        Picasso.with(it).load(data?.mFullPath).centerInside().resize(imageSquare, imageSquare).into(imageView)
                        println("ORIENTATION: $data")
                    }
                }
            }

            override fun viewHolder(inflater: LayoutInflater?, parent: ViewGroup?, type: Int): BaseViewHolder {
                return object : BaseViewHolder(R.layout.view_cameraroll_item, inflater!!, parent) {
                    override fun addClicks(views: ViewMap?) {
                        /**
                         * set listener to open [EditingActivity]
                         * when an image is tapped
                         * */
                        views?.click {
                            val intent = Intent(activity, EditingActivity::class.java)
                            intent.putExtra(
                                    Constants.CameraIntents.IMAGE_FILE_PATH,
                                    getItem(adapterPosition).mFullPath
                            )
                            intent.putExtra(
                                    Constants.CameraIntents.IS_FROM_CAMERA_ROLL, true
                            )
                            activity?.startActivity(intent)
                        }
                        super.addClicks(views)
                    }
                }
            }
        }
        mAdapter?.setItems(mGalleryItems)
        var gridLayout = GridLayoutManager(context, 3)
        cr_recyclerView.layoutManager = gridLayout
        cr_recyclerView.adapter = mAdapter

        //Endless Scroll Listener
        mScrollListener = object : EndlessRecyclerViewScrollListener(gridLayout) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                //Load data from Content Provider
                galleryProvider.setContentOffset(mGalleryItems.size)
                activity!!.loaderManager.initLoader(0, null, this@CameraRollFragment).forceLoad()
            }

        }

        cr_recyclerView.addOnScrollListener(mScrollListener)
    }

    fun refreshRecycler() {
        if (!loaderManager.hasRunningLoaders()) {
            activity!!.loaderManager.initLoader(0, null, this).forceLoad() //TODO: Add loader initial value for pagination
        }
    }

    /**
     * [LoaderManager] LifeCycle required methods
     * Required methods
     * */
    override fun onCreateLoader(id: Int, args: Bundle?): Loader<List<GalleryItem>> = galleryAsyncLoader

    override fun onLoadFinished(loader: Loader<List<GalleryItem>>?, data: List<GalleryItem>?) {
        //mGalleryItems.clear()
        data?.let {

            if (it.size == mGalleryItems.count() || it.isEmpty()) {
                fcr_refresh.isRefreshing = false
                return
            }

            for (galleryPhoto in it) {
                mGalleryItems.add(galleryPhoto)
            }

            /*Picture count subtitle & toolbar swap*/
            if (mGalleryItems.count() > 0) {
                toolbarVisibility(mGalleryItems.count())
                fcr_refresh.isRefreshing = false

                /*Setup RecyclerView with fresh data*/
                mAdapter?.setItems(mGalleryItems)
                cr_recyclerView?.post {
                    if (mGalleryItems.size > galleryProvider.mItemsPerPage) {
                        mAdapter?.notifyItemRangeChanged(mAdapter?.itemCount!!, mGalleryItems.size - galleryProvider.mItemsPerPage)
                    } else {
                        mAdapter?.notifyItemRangeChanged(0, mAdapter?.itemCount!!)
                    }
                }
            }
        }
    }

    override fun onLoaderReset(loader: Loader<List<GalleryItem>>?) {
        mGalleryItems.clear()
    }


    /*On Click action listeners*/
    override fun onClick(p0: View?) {
        when (p0) {

        /*Back button*/
            toolbar_leftoolbart_action -> {
                activity?.finish()
            }
        }
    }

    /**
     * [videoThumbnail]
     * Creates a video thumbnail for displaying the specified file
     * */
    fun videoThumbnail(filePath: String?): Bitmap =
            ThumbnailUtils.createVideoThumbnail(filePath, MediaStore.Video.Thumbnails.MINI_KIND)
}