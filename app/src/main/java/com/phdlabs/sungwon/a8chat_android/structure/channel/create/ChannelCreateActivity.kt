package com.phdlabs.sungwon.a8chat_android.structure.channel.create

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.api.data.channel.ChannelPostData
import com.phdlabs.sungwon.a8chat_android.model.channel.Channel
import com.phdlabs.sungwon.a8chat_android.model.media.Media
import com.phdlabs.sungwon.a8chat_android.model.room.Room
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

    /*Controller*/
    override lateinit var controller: ChannelContract.Create.Controller

    /*Layout*/
    override fun layoutId(): Int = R.layout.activity_channel_create

    override fun contentContainerId(): Int = 0

    /*Properties*/
    override val getActivity: ChannelCreateActivity = this
    //UI form
    private var isCheckedAddToProf: Boolean = false
    private var mMedia: Media? = null
    //Channel Editing
    private var isEdit: Boolean = false
    private var mChannelId: Int? = null
    private var mRoomId: Int? = null
    private var mRoom: Room? = null
    private var mChannel: Channel? = null

    /*LifeCycle*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ChannelCreateAController(this)
        getIntentInfo()
    }

    override fun onStart() {
        super.onStart()
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

    /*Editing Intent*/
    private fun getIntentInfo() {
        //Get intent info
        mChannelId = intent.getIntExtra(Constants.IntentKeys.CHANNEL_ID, 0)
        mRoomId = intent.getIntExtra(Constants.IntentKeys.ROOM_ID, 0)
        //Validate Intent
        if (mChannelId != 0 && mRoomId != 0) {
            isEdit = true
            //Room
            mRoomId?.let {
                controller.getRoomInfo(it, { room ->
                    mRoom = room
                })
            }
            //Channel
            mChannelId?.let {
                controller.getChannelInfo(it, { channel ->
                    mChannel = channel
                })
            }
        }
    }

    /*Transition*/
    override fun onCreateChannel(chanId: Int?, chanName: String?, roomId: Int?, ownerId: Int?) {
        //Dismiss previous activity
        setResult(Activity.RESULT_OK)
        //Transition to new channel
        val intent = Intent(this, MyChannelActivity::class.java)
        intent.putExtra(Constants.IntentKeys.CHANNEL_ID, chanId)
        intent.putExtra(Constants.IntentKeys.CHANNEL_NAME, chanName)
        intent.putExtra(Constants.IntentKeys.ROOM_ID, roomId)
        intent.putExtra(Constants.IntentKeys.OWNER_ID, ownerId)
        startActivity(intent)
        /*Finish current activity & deliver navigation results to previous back-stack*/
        finish()
    }

    override fun onUpdateChannel(chanId: Int?, chanName: String?, roomId: Int?, ownerId: Int?) {
        //Refresh Channel settings activity
        setResult(Activity.RESULT_OK)
        //Transition tu ChannelSettings
        intent.putExtra(Constants.IntentKeys.CHANNEL_ID, chanId)
        intent.putExtra(Constants.IntentKeys.CHANNEL_NAME, chanName)
        intent.putExtra(Constants.IntentKeys.ROOM_ID, roomId)
        intent.putExtra(Constants.IntentKeys.OWNER_ID, ownerId)
        finish()
    }

    /*UI*/
    private fun setUpViews() {
        /*Toolbar & Controls*/
        if (isEdit) {
            mChannel?.let { channel ->
                //Toolbar
                setToolbarTitle(getString(R.string.edit_channel))
                showRightTextToolbar(getString(R.string.done))
                /*Form*/
                channel.add_to_profile?.let {
                    if (it) {
                        acc_add_to_profile_button.isChecked = it
                    }
                }
                acc_add_to_profile_button.setOnCheckedChangeListener { _, b ->
                    isCheckedAddToProf = b
                }
                //Channel Information
                acc_channel_name.setText(channel.name)
                acc_unique_id.setText(channel.unique_id)
                acc_short_description.setText(channel.description)
                Picasso.with(context)
                        .load(channel.avatar)
                        .placeholder(R.drawable.addphoto)
                        .transform(CircleTransform())
                        .into(acc_channel_picture)
            }
        } else {
            setToolbarTitle(getString(R.string.create_a_channel))
            showRightTextToolbar(getString(R.string.create))
            /*Form*/
            acc_add_to_profile_button.setOnCheckedChangeListener { _, b ->
                isCheckedAddToProf = b
            }
        }
        showBackArrow(R.drawable.ic_back)
    }

    /*Set Channel Image*/
    override fun setChannelImage(filePath: String) {
        if (isEdit) {//TODO: Test
            Picasso.with(context)
                    .load(filePath)
                    .placeholder(R.drawable.addphoto)
                    .transform(CircleTransform())
                    .into(acc_channel_picture)
        } else {
            Picasso.with(context)
                    .load("file://" + filePath)
                    .placeholder(R.drawable.addphoto)
                    .transform(CircleTransform())
                    .into(acc_channel_picture)

        }
    }

    /*Get uploaded media & Persist in lifecycle*/
    override fun getMedia(media: Media) {
        mMedia = media
    }

    /*On Click*/
    private fun setUpClickers() {
        toolbar_right_text.setOnClickListener {
            if (isEdit) { //Editing Channel
                mChannelId?.let {
                    controller.updateChannel(it,
                            ChannelPostData(
                                    mMedia?.id.toString().trim(),
                                    acc_channel_name.text.toString().trim(),
                                    acc_unique_id.text.toString().trim(),
                                    acc_short_description.text.toString().trim(),
                                    mChannel?.add_to_profile ?: false,
                                    null
                            )
                    )
                }
            } else { //Creating Channel
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
        }

        /*Profile picture*/
        acc_channel_picture.setOnClickListener {
            controller.showPicture()
        }

    }
}