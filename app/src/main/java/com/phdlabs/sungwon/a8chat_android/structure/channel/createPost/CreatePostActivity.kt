package com.phdlabs.sungwon.a8chat_android.structure.channel.createPost

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.andremion.louvre.home.GalleryActivity
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.structure.channel.ChannelContract
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import kotlinx.android.synthetic.main.activity_create_post.*
import kotlinx.android.synthetic.main.toolbar.*

/**
 * Created by paix on 3/5/18.
 * [CreatePostActivity] used to manage media & create a post
 */
class CreatePostActivity : CoreActivity(), ChannelContract.CreatePost.View, View.OnClickListener {

    /*Controller*/
    override lateinit var controller: ChannelContract.CreatePost.Controller
    override var activity: CreatePostActivity = this

    /*Layout*/
    override fun layoutId(): Int = R.layout.activity_create_post

    override fun contentContainerId(): Int = 0

    /*Properties*/
    var mSelectedMedia: ArrayList<String> = arrayListOf()

    /*LifeCycle*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Keyboard behavior
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        //Controller
        CreatePostController(this)
        //Toolbar
        setupToolbar()
        //Clicks
        setClickers()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Constants.CameraIntents.OPEN_MEDIA_PICKER){
            if (resultCode == Activity.RESULT_OK){
                data?.let {
                    //Collect selected images url's
                    println("DATA" +  data)
                    var photos = GalleryActivity.getSelection(it)
                    println("PHTOS: " + photos)

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
        showBackArrow(R.drawable.ic_back)
        toolbar_right_text.text = getString(R.string.post)
        toolbar_right_text.setTextColor(Color.BLUE)
    }

    /*On Click*/
    fun setClickers() {
        acp_addmedia_container.setOnClickListener(this)
        acp_addmedia_button.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
        /*Add Media*/
            acp_addmedia_container.id  -> {
                //Request Storage Permissions
                controller.requestStoragePermissions()
            }
            acp_addmedia_button.id -> {
                controller.requestStoragePermissions()
            }

        }
    }

}