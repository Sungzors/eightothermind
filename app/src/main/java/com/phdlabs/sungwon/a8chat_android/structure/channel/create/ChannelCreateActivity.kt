package com.phdlabs.sungwon.a8chat_android.structure.channel.create

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.WindowManager
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.api.data.ChannelPostData
import com.phdlabs.sungwon.a8chat_android.model.media.Media
import com.phdlabs.sungwon.a8chat_android.structure.channel.ChannelContract
import com.phdlabs.sungwon.a8chat_android.structure.channel.mychannel.MyChannelActivity
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import com.phdlabs.sungwon.a8chat_android.utility.camera.CircleTransform
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_channel_create.*
import kotlinx.android.synthetic.main.toolbar.*

/**
 * Created by SungWon on 11/30/2017.
 * Updated by JPAM on 01/25/2018
 */
class ChannelCreateActivity : CoreActivity(), ChannelContract.Create.View {

    /*Properties*/
    override fun layoutId(): Int = R.layout.activity_channel_create

    override fun contentContainerId(): Int = 0
    override lateinit var controller: ChannelContract.Create.Controller
    override val getActivity: ChannelCreateActivity = this

    //UI form
    private var isCheckedAddToProf: Boolean = false
    private var mMedia: Media? = null

    /*LifeCycle*/
    override fun onStart() {
        super.onStart()
        ChannelCreateAController(this)
        controller.start()
        setUpViews()
        setUpClickers()
    }

    override fun onResume() {
        super.onResume()
        controller.resume()
    }

    override fun onPause() {
        super.onPause()
        controller.pause()
    }

    override fun onStop() {
        super.onStop()
        controller.stop()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        controller.onPictureResult(requestCode, resultCode, data)

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == Constants.PermissionsReqCode.CAMERA_REQ_CODE) {
            if (grantResults.size != 1 || grantResults.get(0) != PackageManager.PERMISSION_GRANTED) {
                showError(getString(R.string.request_camera_permission))
            } else {
                controller.showPicture()
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    /*Transition*/
    override fun onCreateChannel(chanId: Int?, chanName: String?, roomId: Int?, ownerId: Int?) {
        val intent = Intent(this, MyChannelActivity::class.java)
        intent.putExtra(Constants.IntentKeys.CHANNEL_ID, chanId)
        intent.putExtra(Constants.IntentKeys.CHANNEL_NAME, chanName)
        intent.putExtra(Constants.IntentKeys.ROOM_ID, roomId)
        intent.putExtra(Constants.IntentKeys.OWNER_ID, ownerId)
        startActivity(intent)
    }


    /*UI*/
    private fun setUpViews() {
        /*Toolbar & Controls*/
        setToolbarTitle(getString(R.string.create_a_channel))
        showRightTextToolbar(getString(R.string.create))
        showBackArrow(R.drawable.ic_back)
        /*Form*/
        acc_add_to_profile_button.setOnCheckedChangeListener { _, b ->
            isCheckedAddToProf = b
        }
    }

    /*Set Channel Image*/
    override fun setChannelImage(filePath: String) {
        Picasso.with(context)
                .load("file://" + filePath)
                .placeholder(R.drawable.addphoto)
                .transform(CircleTransform())
                .into(acc_channel_picture)
    }

    /*Get uploaded media & Persist in lifecycle*/
    override fun getMedia(media: Media) {
        mMedia = media
    }

    /*On Click*/
    private fun setUpClickers() {
        toolbar_right_text.setOnClickListener {
            /*Create Channel*/
            controller.getUserId { id ->
                controller.createChannel(
                        ChannelPostData(
                                mMedia?.id.toString().trim(),
                                acc_channel_name.text.toString().trim(),
                                acc_unique_id.text.toString().trim(),
                                acc_short_description.text.toString().trim(),
                                isCheckedAddToProf,
                                id
                        )
                )
            }

        }
        /*Profile picture*/
        acc_channel_picture.setOnClickListener {
            controller.showPicture()
        }
    }
}