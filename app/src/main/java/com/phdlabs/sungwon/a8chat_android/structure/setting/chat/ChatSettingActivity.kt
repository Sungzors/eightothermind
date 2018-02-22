package com.phdlabs.sungwon.a8chat_android.structure.setting.chat

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.view.View
import android.widget.Toast
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.model.contacts.Contact
import com.phdlabs.sungwon.a8chat_android.structure.chat.ChatActivity
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import com.phdlabs.sungwon.a8chat_android.structure.setting.SettingContract
import com.phdlabs.sungwon.a8chat_android.structure.setting.bottomtabfragments.MediaSettingFragment
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import com.phdlabs.sungwon.a8chat_android.utility.camera.CircleTransform
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_settings_chat.*
import java.util.*

/**
 * Created by SungWon on 1/22/2018.
 * Updated by JPAM on 02/22/2018
 */
class ChatSettingActivity : CoreActivity(), SettingContract.Chat.View, View.OnClickListener {

    /*Controller*/
    override lateinit var controller: SettingContract.Chat.Controller

    /*Layout*/
    override fun layoutId(): Int = R.layout.activity_settings_chat

    override fun contentContainerId(): Int = R.id.asc_fragment_container

    /*Properties*/
    private var mChatName = ""
    private var mRoomId = 0
    private var mUserId = 0
    private var mContact: Contact? = null
    private var mContactAvatar: String? = null
    private var mIntentFromContact: Boolean = false

    /*LifeCycle*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Controller
        ChatSettingController(this)
        //Get Intent
        mChatName = intent.getStringExtra(Constants.IntentKeys.CHAT_NAME)
        mRoomId = intent.getIntExtra(Constants.IntentKeys.ROOM_ID, 0)
        mUserId = intent.getIntExtra(Constants.IntentKeys.PARTICIPANT_ID, 0)
        mIntentFromContact = intent.getBooleanExtra(Constants.IntentKeys.FROM_CONTACTS, false)
        //Retrieve Contact Information
        mContact = controller.getContactInfo(mUserId)
        setupToolbar()
        setupUserInfo()
        setUpTabs()
        setUpClickers()
    }

    override fun onStart() {
        super.onStart()
        controller.start()
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
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.ContactItems.TO_CHAT_FROM_CONTACT_REQ_CODE) {
            //TODO: Back from chat launched from contacts screen, todo refresh media , files & favorite messages only
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    /*Toolbar*/
    private fun setupToolbar() {
        setToolbarTitle(mChatName)
        showBackArrow(R.drawable.ic_back)
    }

    /*User Info*/
    private fun setupUserInfo() {
        //Display Contact Info
        mContact?.let {
            //Display Name
            val fullName: Pair<Boolean, String?> = it.hasFullName()
            if (fullName.first) {
                asc_profile_name.text = fullName.second
            } else {
                asc_profile_name.text = it.first_name ?: "n/a"
            }
            //Phone Number
            asc_profile_phone.text = PhoneNumberUtils.formatNumber(it.phone, Locale.getDefault().country) ?: "n/a"
            //Language
            asc_profile_language.text = "Language: " + it.languages_spoken?.get(0)?.stringValue ?: "n/a"
            //Profile Image
            it.avatar?.let {
                mContactAvatar = it
                Picasso.with(context).load(it).resize(70, 70).onlyScaleDown()
                        .centerCrop().transform(CircleTransform()).into(asc_chat_picture)
            }
        }
    }

    private fun setUpTabs() {
        asc_bottomnav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.mst_media -> {
                    replaceFragment(MediaSettingFragment.newInstance(), false)
                }
                R.id.mst_file -> {
                    Toast.makeText(this, "gurp", Toast.LENGTH_SHORT).show()
                }
            }
            true
        }
        asc_bottomnav.selectedItemId = R.id.mst_media
    }

    private fun setUpClickers() {
        asc_notification_container.setOnClickListener(this)
        asc_chat_container.setOnClickListener(this)
        asc_call_container.setOnClickListener(this)
        asc_video_container.setOnClickListener(this)
        asc_money_container.setOnClickListener(this)
        asc_favemsg_container.setOnClickListener(this)
        asc_share_container.setOnClickListener(this)
        asc_clear_conversation.setOnClickListener(this)
        asc_block_container.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0) {
        /*Notifications*/
            asc_notification_container -> {
            }
        /*Private Chat with contact*/
            asc_chat_container -> {
                if (!mIntentFromContact) {
                    onBackPressed()
                } else {
                    val intent = Intent(context, ChatActivity::class.java)
                    intent.putExtra(Constants.IntentKeys.CHAT_NAME, mChatName)
                    intent.putExtra(Constants.IntentKeys.PARTICIPANT_ID, mContact?.id)
                    intent.putExtra(Constants.IntentKeys.ROOM_ID, mRoomId)
                    intent.putExtra(Constants.IntentKeys.CHAT_PIC, mContactAvatar ?: "")
                    startActivityForResult(intent, Constants.ContactItems.TO_CHAT_FROM_CONTACT_REQ_CODE)
                }
            }
        /*Call contact*/
            asc_call_container -> {
            }
        /*Video Call with contact*/
            asc_video_container -> {
            }
        /*Send Money to Contact*/
            asc_money_container -> {
            }
        /*Favorite Messages with Contact*/
            asc_favemsg_container -> {
            }
        /*Share with Contact*/
            asc_share_container -> {
            }
        /*Clear conversation*/
            asc_clear_conversation -> {
            }
        /*Block / Unblock Contact*/
            asc_block_container -> {
            }
        }
    }

    fun updateMenuTitle(title1: String?, title2: String?) {
        val leftTab = asc_bottomnav.menu.findItem(R.id.mst_media)
        val rightTab = asc_bottomnav.menu.findItem(R.id.mst_file)
        title1.let {
            leftTab.title = it
        }
        title2.let {
            rightTab.title = it
        }
    }

    override fun finishActivity() {
    }
}