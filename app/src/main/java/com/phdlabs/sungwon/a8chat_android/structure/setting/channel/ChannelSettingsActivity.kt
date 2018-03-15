package com.phdlabs.sungwon.a8chat_android.structure.setting.channel

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.db.channels.ChannelsManager
import com.phdlabs.sungwon.a8chat_android.model.room.Room
import com.phdlabs.sungwon.a8chat_android.model.user.User
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import com.phdlabs.sungwon.a8chat_android.structure.setting.SettingContract
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import com.phdlabs.sungwon.a8chat_android.utility.camera.CircleTransform
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_channel_settings.*

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
    var mChannelName = ""
    var mChannelId = 0
    var mRoomId: Int? = null
    var mOwnerId = 0
    var mOwner: User? = null
    var mRoom: Room? = null


    /*LifeCycle*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Info & Channel Owner update
        getIntentInfo()
        //UI
        setupToolbar()
        setupClickers()
    }

    override fun onStart() {
        super.onStart()
        controller.start()
    }

    override fun onPause() {
        super.onPause()
        controller.pause()
    }

    override fun onStop() {
        super.onStop()
        controller.stop()
    }

    /*Intent Info*/
    private fun getIntentInfo() {
        //Channel Name
        mChannelName = intent.getStringExtra(Constants.IntentKeys.CHANNEL_NAME)
        mChannelId = intent.getIntExtra(Constants.IntentKeys.CHANNEL_ID, 0)
        //Room
        mRoomId = intent.getIntExtra(Constants.IntentKeys.ROOM_ID, 0)
        mRoomId?.let {
            mRoom = controller.getRoomInfo(it) //Get Room from controller
        }
        //Contact Id & Contact Info
        mOwnerId = intent.getIntExtra(Constants.IntentKeys.OWNER_ID, 0)
        controller.getChannelOwnerInfo(mOwnerId)
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
                    //Num. Followers //TODO: Ask Tomer if Followers === num of participants ?
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

        //TODO: Follow Channel

        //Listeners
        achs_favemsg_container.setOnClickListener(this)
        achs_share_container.setOnClickListener(this)
        achs_clear_conv_container.setOnClickListener(this)
        achs_block_container.setOnClickListener(this)
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
        /*Fav Messages*/
            achs_favemsg_container -> {
                Toast.makeText(this, "In Progress", Toast.LENGTH_SHORT).show()
            }

        /*Share Channel*/
            achs_share_container -> {
                Toast.makeText(this, "In Progress", Toast.LENGTH_SHORT).show()
            }

        /*Clear Channel Feed*/
            achs_clear_conv_container -> {
                Toast.makeText(this, "In Progress", Toast.LENGTH_SHORT).show()
            }

        /*Report Channel*/
            achs_block_container -> {
                Toast.makeText(this, "In Progress", Toast.LENGTH_SHORT).show()
            }
        /*Follow Channel*/
            achs_follow_button -> {
                Toast.makeText(this, "In Progress", Toast.LENGTH_SHORT).show()
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


}