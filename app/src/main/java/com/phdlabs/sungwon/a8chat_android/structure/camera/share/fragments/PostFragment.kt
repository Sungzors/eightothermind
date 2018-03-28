package com.phdlabs.sungwon.a8chat_android.structure.camera.share.fragments

import android.os.Bundle
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreFragment
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_share_post.*

/**
 * Created by JPAM on 3/26/18.
 * This fragment manages the media thumbnail content with the message post
 */
class PostFragment : CoreFragment() {

    /*Layout*/
    override fun layoutId(): Int = R.layout.fragment_share_post

    companion object {
        fun create(filePath: String): PostFragment {
            val fragment = PostFragment()
            val args = Bundle()
            args.putString(Constants.CameraIntents.IMAGE_FILE_PATH, filePath)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onStart() {
        super.onStart()
        //Thumbnail
        arguments?.getString(Constants.CameraIntents.IMAGE_FILE_PATH)?.let {
            Picasso.with(context).load("file://$it").placeholder(R.drawable.addphoto).into(fsp_thumbnail_iv)
        }
    }

    fun getMessage(): String? {
        return fsp_post_message.text.toString()
    }


    //TODO: Validate & Capture Post Message. -> Send result to ShareCameraMediaActivity Activity.
}