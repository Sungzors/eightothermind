package com.phdlabs.sungwon.a8chat_android.structure.camera.fragments.cameraRoll

import android.content.pm.PackageManager
import android.graphics.Point
import android.os.Bundle
import android.support.v4.app.LoaderManager
import android.support.v4.content.ContextCompat
import android.support.v4.content.Loader
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.model.media.GalleryPhoto
import com.phdlabs.sungwon.a8chat_android.structure.camera.fragments.CameraBaseFragment
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import com.phdlabs.sungwon.a8chat_android.utility.adapter.BaseRecyclerAdapter
import com.phdlabs.sungwon.a8chat_android.utility.adapter.BaseViewHolder
import com.phdlabs.sungwon.a8chat_android.utility.adapter.ViewMap
import com.phdlabs.sungwon.a8chat_android.utility.camera.PhotoGalleryAsyncLoader
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_cameraroll.*
import java.io.File


/**
 * Created by paix on 12/28/17.
 * [CameraRollFragment] showing available pictures sectioned by date
 * for the user to select & then preview after selection
 */
class CameraRollFragment : CameraBaseFragment(), LoaderManager.LoaderCallbacks<List<GalleryPhoto>> {

    /*Properties*/
    private lateinit var mAdapter: BaseRecyclerAdapter<GalleryPhoto, BaseViewHolder>
    private var mGalleryPhotos: ArrayList<GalleryPhoto> = ArrayList()

    /*Companion*/
    companion object {
        fun create(): CameraRollFragment = CameraRollFragment()
    }

    /*Initialization*/
    init {
        //Gallery Photos
    }

    /*Required*/
    override fun cameraLayoutId(): Int = R.layout.fragment_cameraroll

    override fun inOnCreateView(root: View?, container: ViewGroup?, savedInstanceState: Bundle?) {
    }

    /*LifeCycle*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (ContextCompat.checkSelfPermission(activity!!, Constants.AppPermissions.WRITE_EXTERNAL) != PackageManager.PERMISSION_GRANTED) {
            requestExternalStoragePermissions()
        } else {
            //loaderManager.initLoader(0, null, this)
            loaderManager.initLoader(0, null, this).forceLoad()
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
                loaderManager.initLoader(0, null, this).forceLoad()
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    /*Setup recycler adapters*/
    fun setupRecycler() {
        val displaySize = Point()
        activity?.windowManager?.defaultDisplay?.getSize(displaySize)
        val imageWidth = displaySize.x / 3
        val imageHeight = imageWidth
        mAdapter = object : BaseRecyclerAdapter<GalleryPhoto, BaseViewHolder>() {

            override fun onBindItemViewHolder(viewHolder: BaseViewHolder?, data: GalleryPhoto?, position: Int, type: Int) {
                var imageView = viewHolder?.get<ImageView>(R.id.cr_iv_photo)
                viewHolder?.let {
                    context?.let {
                        Picasso.with(it).
                                load(File(data?.mFullPath)).
                                rotate(90f).
                                centerInside().
                                resize(imageWidth, imageHeight).
                                into(imageView)
                        println("DATE_TAKEN: " + data?.mDate)
                    }
                }
            }

            override fun viewHolder(inflater: LayoutInflater?, parent: ViewGroup?, type: Int): BaseViewHolder {
                return object : BaseViewHolder(R.layout.view_cameraroll_item, inflater!!, parent) {
                    override fun addClicks(views: ViewMap?) {
                        super.addClicks(views)
                    }
                }
            }
        }
        mAdapter.setItems(mGalleryPhotos)
        //mAdapter.notifyDataSetChanged()
        val gridLayoutManager = GridLayoutManager(context, 3)
        cr_recyclerView.layoutManager = gridLayoutManager
        cr_recyclerView.adapter = mAdapter

    }

    /**
     * [LoaderManager] LifeCycle required methods
     * Required methods
     * */
    override fun onCreateLoader(id: Int, args: Bundle?): Loader<List<GalleryPhoto>> =
            PhotoGalleryAsyncLoader(context!!)

    override fun onLoadFinished(loader: Loader<List<GalleryPhoto>>?, data: List<GalleryPhoto>?) {
        mGalleryPhotos.clear()
        data?.let {
            for (galleryPhoto in it) {
                mGalleryPhotos.add(galleryPhoto)
            }
            //TODO: Remove
            val photoCount = mGalleryPhotos.count()
            println("Photo Count: " + photoCount)
            //TODO: Remove
            /*Setup RecyclerView with fresh data*/
            setupRecycler()
        }
    }

    override fun onLoaderReset(loader: Loader<List<GalleryPhoto>>?) {
        mGalleryPhotos.clear()
    }

}