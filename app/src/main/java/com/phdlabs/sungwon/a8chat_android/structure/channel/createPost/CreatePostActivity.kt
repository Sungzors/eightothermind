package com.phdlabs.sungwon.a8chat_android.structure.channel.createPost

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import com.andremion.louvre.home.GalleryActivity
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.structure.channel.ChannelContract
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import com.phdlabs.sungwon.a8chat_android.utility.adapter.BaseRecyclerAdapter
import com.phdlabs.sungwon.a8chat_android.utility.adapter.BaseViewHolder
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_create_post.*
import kotlinx.android.synthetic.main.toolbar.*

/**
 * Created by JPAM on 3/5/18.
 * [CreatePostActivity] used to manage media & newInstanceChannelRoom a post
 */
class CreatePostActivity : CoreActivity(), ChannelContract.CreatePost.View, View.OnClickListener {

    /*Controller*/
    override lateinit var controller: ChannelContract.CreatePost.Controller
    override var activity: CreatePostActivity = this

    /*Layout*/
    override fun layoutId(): Int = R.layout.activity_create_post

    override fun contentContainerId(): Int = 0

    /*Properties*/
    var mSelectedMedia: MutableList<Uri> = mutableListOf()
    var mMediaAdapter: BaseRecyclerAdapter<Uri, BaseViewHolder>? = null
    var mRoomId: Int? = null

    /*LifeCycle*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Keyboard behavior
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        //Controller
        CreatePostController(this)
        //Get Intent
        mRoomId = intent.getIntExtra(Constants.IntentKeys.ROOM_ID, 0)
        //Toolbar
        setupToolbar()
        //Clicks
        setClickers()
        //Recycler
        setupMediaRecycler()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Constants.CameraIntents.OPEN_MEDIA_PICKER) {
            if (resultCode == Activity.RESULT_OK) {
                data?.let {
                    //Collect selected images url's
                    mSelectedMedia = GalleryActivity.getSelection(it)
                    refreshMediaAdapter()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == Constants.PermissionsReqCode.READ_EXTERNAL_STORAGE) {
            if (grantResults.size != 1 || grantResults.get(0) != PackageManager.PERMISSION_GRANTED) {
                showError(getString(R.string.request_read_external_permission))
            } else {
                controller.openMediaPicker()
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    /*Toolbar*/
    fun setupToolbar() {
        //Back
        showBackArrow(R.drawable.ic_back)
        //Post
        toolbar_right_text.text = getString(R.string.post)
        toolbar_right_text.visibility = View.VISIBLE
        toolbar_right_text.setTextColor(Color.BLUE)
        //Title
        toolbar_title.text = getString(R.string.create_a_post)
    }

    /*On Click*/
    fun setClickers() {
        acp_addmedia_container.setOnClickListener(this)
        acp_addmedia_button.setOnClickListener(this)
        toolbar_right_text.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
        /*Add Media*/
            acp_addmedia_container.id -> {
                //Request Storage Permissions
                controller.requestStoragePermissions()
            }
            acp_addmedia_button.id -> {
                controller.requestStoragePermissions()
            }
        /*Create Post*/
            toolbar_right_text.id -> {
                if (mRoomId != 0) {
                    controller.createPost()
                }
            }
        }
    }

    /*Media Recycler*/
    fun setupMediaRecycler() {
        mMediaAdapter = object : BaseRecyclerAdapter<Uri, BaseViewHolder>() {
            override fun onBindItemViewHolder(viewHolder: BaseViewHolder?, data: Uri?, position: Int, type: Int) {
                val imageView = viewHolder?.get<ImageView>(R.id.vpmi_image)
                val deleteImageView = viewHolder?.get<ImageView>(R.id.vpmi_remove_image)
                data?.let {
                    var imageSize: Pair<Int, Int> = Pair(170, 170)
                    if (mSelectedMedia.count() == 1) {
                        imageSize = Pair(100, 100)
                    }
                    Picasso.with(context)
                            .load(it)
                            .resize(imageSize.first, imageSize.second)
                            .centerCrop()
                            .into(imageView)
                    //Delete
                    deleteImageView?.setOnClickListener {
                        //Delete image
                        mSelectedMedia.removeAt(position)
                        mMediaAdapter?.setItems(mSelectedMedia)
                        mMediaAdapter?.notifyItemChanged(position)
                    }
                }
            }

            override fun viewHolder(inflater: LayoutInflater?, parent: ViewGroup?, type: Int): BaseViewHolder =
                    object : BaseViewHolder(R.layout.view_post_media_item, inflater!!, parent) {}

        }
        mMediaAdapter?.setItems(mSelectedMedia)
        acp_media_recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        acp_media_recyclerView.adapter = mMediaAdapter
    }

    override fun refreshMediaAdapter() {
        if (mSelectedMedia.count() > 0) {
            mMediaAdapter?.setItems(mSelectedMedia)
            mMediaAdapter?.notifyDataSetChanged()
            acp_media_recyclerView.visibility = View.VISIBLE
        } else {
            acp_media_recyclerView.visibility = View.GONE
        }
    }

    /*POST*/
    override fun validatePost(): Boolean = cpa_message_post.text.isNotBlank()

    override fun getPostData(): Pair<String, MutableList<Uri>> =
            Pair(cpa_message_post.text.toString(), mSelectedMedia)

    override fun close() {
        this.finish()
    }

}