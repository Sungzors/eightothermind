package com.phdlabs.sungwon.a8chat_android.structure.setting.channel

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.db.channels.ChannelsManager
import com.phdlabs.sungwon.a8chat_android.model.room.Room
import com.phdlabs.sungwon.a8chat_android.model.user.User
import com.phdlabs.sungwon.a8chat_android.structure.channel.create.ChannelCreateActivity
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import com.phdlabs.sungwon.a8chat_android.structure.favorite.message.FavoriteMessageActivity
import com.phdlabs.sungwon.a8chat_android.structure.setting.SettingContract
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import com.phdlabs.sungwon.a8chat_android.utility.camera.CircleTransform
import com.squareup.picasso.Picasso
import com.vicpin.krealmextensions.queryFirst
import kotlinx.android.synthetic.main.activity_channel_settings.*
import kotlinx.android.synthetic.main.toolbar.*

/**
 * Created by JPAM on 3/12/18.
 * [ChannelSettingsActivity]
 * Used to display Channel settings content & actions
 */
class ChannelSettingsActivity : CoreActivity(), SettingContract.Channel.View, View.OnClickListener {

    /*Controller*/
    override lateinit var controller: SettingContract.Channel.Controller

    /*Layout*/
    override fun layoutId(): Int = R.layout.activity_channel_settings

    override fun contentContainerId(): Int = R.id.achs_fragment_container

    /*Parameters*/
    override var activity: ChannelSettingsActivity? = this


    /*Init*/
    init {
        //Controller
        ChannelSettingsController(this)
    }

    /*Properties*/
    private var mChannelName = ""
    private var mChannelId = 0
    private var mRoomId: Int? = null
    private var mOwnerId = 0
    private var mOwner: User? = null
    private var mRoom: Room? = null
    private var mRoomParticipants: MutableList<Int>? = null


    /*LifeCycle*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Info & Channel Owner update
        getIntentInfo()
    }

    override fun onStart() {
        super.onStart()
        controller.start()
        //UI
        setupToolbar()
        setupClickers()
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
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.RequestCodes.EDIT_CHANNEL) {
                getIntentInfo()
                //TODO: Update UI
            }
        }

        if(resultCode == Constants.ResultCode.SUCCESS){
            setResult(Constants.ResultCode.SUCCESS)
            finish()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    /*Intent Info*/
    private fun getIntentInfo() {
        showProgress()
        //Channel Name
        mChannelName = intent.getStringExtra(Constants.IntentKeys.CHANNEL_NAME)
        mChannelId = intent.getIntExtra(Constants.IntentKeys.CHANNEL_ID, 0)
        //Room
        mRoomId = intent.getIntExtra(Constants.IntentKeys.ROOM_ID, 0)

        achs_favemsg_tv.text = resources.getString(
                R.string.fave_messages,
                "0"
        )
        mRoomId?.let {
            controller.getRoomInfo(it, {
                it?.let {
                    mRoom = it
                }
            })
            controller.getRoomParticipants(it, {
                it?.let {
                    mRoomParticipants = it
                    //Contact Id & Contact Info
                    mOwnerId = intent.getIntExtra(Constants.IntentKeys.OWNER_ID, 0)
                    controller.getChannelOwnerInfo(mOwnerId)
                    hideProgress()
                }
            })
            controller.getFavorite(it, {
                achs_favemsg_tv.text = resources.getString(
                        R.string.fave_messages,
                        it.toString()
                )
            })
        }
    }

    /*Toolbar*/
    private fun setupToolbar() {
        setToolbarTitle(mChannelName)
        showBackArrow(R.drawable.ic_back)
    }

    /*Display User Info*/
    override fun updateChannelOwnerInfo(channelOwner: User) {
        mOwner = channelOwner
        //UI
        mOwner?.hasFullName()?.let {
            //Full Name
            if (it.first) {
                it.second?.let { achs_profile_name.text = it }
            }
            //Channel
            mRoom?.let {
                it.channel?.let {
                    //Num. Followers
                    if (it) {
                        achs_followers_number.text = resources.getString(
                                R.string.channel_followers,
                                mRoom?.participantsId?.count().toString().trim()
                        )
                    }
                    //Channel Info
                    ChannelsManager.instance.getSingleChannel(mChannelId)?.let {
                        //Channel Pic
                        Picasso.with(context).load(it?.avatar)
                                .transform(CircleTransform())
                                .into(asc_chat_picture)
                        //Channel Description
                        achs_channel_description.text = it?.description
                    }
                }
            }
        }

        //Update UI
        controller.getAppUserId { id ->
            //Hide or show Follow button
            if (id == mOwnerId) { //Channel Owner
                achs_follow_button.visibility = View.GONE
                showRightTextToolbar("Edit")
                toolbar_right_text.setTextColor(resources.getColor(R.color.blue_color_picker))
                toolbar_right_text.setOnClickListener(this)
                achs_delete_container.visibility = View.VISIBLE
                achs_delete_container.setOnClickListener(this)
            } else { //Channel Visitor
                achs_follow_button.visibility = View.VISIBLE
                achs_delete_container.visibility = View.GONE
                //Follow || Un-followed channel state
                id?.let {
                    mRoomParticipants?.let {
                        if (it.contains(id)) { //Is following
                            achs_follow_button.text = getString(R.string.unfollow)
                        } else {//Isn't following
                            achs_follow_button.text = getString(R.string.follow)
                        }
                    }
                }
            }
        }
    }


    /*Clickers*/
    private fun setupClickers() {
        //Notifications Switch//TODO: Notifications
        achs_notif_switch.isSelected = false
        achs_notif_switch.setOnCheckedChangeListener { compoundButton, b ->
            if (b) { //Notifications ON
                //TODO: Enable Notifications
            } else {
                //TODO: Disable Notifications
            }
        }

        //Listeners
        achs_favemsg_container.setOnClickListener(this)
        achs_share_container.setOnClickListener(this)
        achs_follow_button.setOnClickListener(this)
        //Media & Files (Fragments)
        achs_button_media.setOnClickListener(this)
        achs_button_files.setOnClickListener(this)
        achs_button_media.text = "Media"
        achs_button_files.text = "Files"
        achs_button_media.isChecked = true
        achs_button_media.performClick()
        achs_button_files.isChecked = false
    }

    override fun onClick(p0: View?) {
        when (p0) {
        /*Edit Channel Name, Description & Photo*/
            toolbar_right_text -> {
                val intent = Intent(context, ChannelCreateActivity::class.java)
                intent.putExtra(Constants.IntentKeys.CHANNEL_ID, mChannelId)
                intent.putExtra(Constants.IntentKeys.ROOM_ID, mRoomId)
                startActivityForResult(intent, Constants.RequestCodes.EDIT_CHANNEL)

            }
        /*Fav Messages*/
            achs_favemsg_container -> {
                val intent = Intent(context, FavoriteMessageActivity::class.java)
                intent.putExtra(Constants.IntentKeys.ROOM_ID, mRoomId)
                intent.putExtra(Constants.IntentKeys.FAVE_TYPE, 2)
                startActivityForResult(intent, Constants.RequestCodes.OPEN_FAVE)
            }

        /*Share Channel*/
            achs_share_container -> {
                Toast.makeText(this, "In Progress", Toast.LENGTH_SHORT).show()
            }

        /*Follow Channel*/
            achs_follow_button -> {
                controller.getAppUserId { id ->
                    //Owner can't follow it's own channel
                    id?.let {
                        mRoomParticipants?.let {
                            if (it.contains(id)) { //Un-Follow
                                achs_follow_button.text = getString(R.string.follow)
                                mRoomId?.let {
                                    controller.unfollowChannel(it)
                                    updateRoomParticipants(id, true)
                                }

                            } else { //Follow
                                achs_follow_button.text = getString(R.string.unfollow)
                                controller.followChannel(mChannelId, id)
                                updateRoomParticipants(id, false)
                            }
                        }
                    }
                }
            }
        /*Delete Channel*/
            achs_delete_container -> {
                val builder = AlertDialog.Builder(this)
                        .setMessage(getString(R.string.confirm_channel_deletion))
                        .setPositiveButton(R.string.delete) { _, _ -> controller.deleteChannel(mChannelId) }
                        .setNegativeButton(R.string.cancel) { _, _ -> /*Nothing*/ }
                builder.create()
                builder.show()
            }
        /*Media*/
            achs_button_media -> {
                mRoomId?.let {
                    controller.getMedia(it)
                }
            }
        /*Files*/
            achs_button_files -> {
                mRoomId?.let {
                    controller.getFiles(it)
                }
            }
        }
    }

    /*MEDIA*/
    /*Media Selectors Count*/
    fun updateSelectorTitle(title1: String?, title2: String?) {
        title1?.let {
            achs_button_media.text = it
        }
        title2?.let {
            achs_button_files.text = it
        }
    }

    /*ROOM*/
    override fun updateRoomInfo() {
        Room().queryFirst { equalTo("id", mRoomId) }?.let {
            //Update Room Info
            mRoom = it
        }
    }

    override fun updateRoomParticipants(participantId: Int, remove: Boolean) {
        //Remove
        if (remove) {
            mRoomParticipants?.remove(participantId)
        } else {
            mRoomParticipants?.add(participantId)
        }
        //Update Followers Number
        achs_followers_number.text = resources.getString(
                R.string.channel_followers,
                mRoomParticipants?.count().toString().trim())
    }

    /*User Feedback*/
    override fun userFeedback(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun channelDeleted() {
        val deletedChannelIntent = Intent()
        deletedChannelIntent.putExtra(Constants.IntentKeys.CHANNEL_DELETED, true)
        setResult(Activity.RESULT_OK, deletedChannelIntent)
        finish()
    }

}