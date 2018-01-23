package com.phdlabs.sungwon.a8chat_android.structure.camera.fragments.cameraRoll

import android.content.Context
import android.database.Cursor
import android.os.Bundle
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.model.media.GalleryPhoto
import com.phdlabs.sungwon.a8chat_android.structure.camera.fragments.CameraBaseFragment
import com.phdlabs.sungwon.a8chat_android.utility.adapter.BaseRecyclerAdapter
import com.phdlabs.sungwon.a8chat_android.utility.adapter.BaseViewHolder
import com.phdlabs.sungwon.a8chat_android.utility.camera.PhotoGalleryAsyncLoader


/**
 * Created by paix on 12/28/17.
 * [CameraRollFragment] showing available pictures sectioned by date
 * for the user to select & then preview after selection
 */
class CameraRollFragment: CameraBaseFragment(), LoaderManager.LoaderCallbacks<List<GalleryPhoto>> {

    /*Properties*/
    private lateinit var mAdapter: BaseRecyclerAdapter<GalleryPhoto, BaseViewHolder>
    private lateinit var mGalleryPhotos: ArrayList<GalleryPhoto>
    private lateinit var mCursor: Cursor
    private var mColumnIndex: Int = 0

    /*Companion*/
    companion object {
        fun create(): CameraRollFragment = CameraRollFragment()
    }

    /*Initialization*/
    init {

        //Gallery Photos
        mGalleryPhotos = ArrayList()

    }

    /*Required*/
    override fun cameraLayoutId(): Int = R.layout.fragment_cameraroll

    override fun inOnCreateView(root: View?, container: ViewGroup?, savedInstanceState: Bundle?) {
    }

    /*LifeCycle*/

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        //showProgress()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //loaderManager.initLoader(0, null, this)
        loaderManager.initLoader(0, null, this).forceLoad()
    }


    /*Setup recycler adapters*/
    fun setupRecycler() {
        mAdapter = object: BaseRecyclerAdapter<GalleryPhoto,BaseViewHolder>() {
            override fun onBindItemViewHolder(viewHolder: BaseViewHolder?, data: GalleryPhoto?, position: Int, type: Int) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun viewHolder(inflater: LayoutInflater?, parent: ViewGroup?, type: Int): BaseViewHolder {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }
    }

    /**
     * [LoaderManager] LifeCycle required methods
     * */
    override fun onCreateLoader(id: Int, args: Bundle?): Loader<List<GalleryPhoto>> =
            PhotoGalleryAsyncLoader(context!!)

    override fun onLoadFinished(loader: Loader<List<GalleryPhoto>>?, data: List<GalleryPhoto>?) {
        mGalleryPhotos.clear()
        data?.let {
            for (galleryPhoto in it) {
                mGalleryPhotos.add(galleryPhoto)
            }
            var photoCount = mGalleryPhotos.count()
            println("Photo COunt: " + photoCount)
            //hideProgress()
        }
    }

    override fun onLoaderReset(loader: Loader<List<GalleryPhoto>>?) {
        mGalleryPhotos.clear()
        //hideProgress()
    }


}