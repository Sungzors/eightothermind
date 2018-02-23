package com.phdlabs.sungwon.a8chat_android.structure.setting.chat

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.model.contacts.Contact
import com.phdlabs.sungwon.a8chat_android.model.room.Room
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
    //Chat & Room
    private var mChatName = ""
    private var mRoomId = 0
    private var mRoom: Room? = null
    //Contact
    private var contactId = 0
    private var mContact: Contact? = null
    private var mContactAvatar: String? = null
    private var mIntentFromContact: Boolean = false

    /*LifeCycle*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Controller
        ChatSettingController(this)

        /*Intent Information*/
        //Chat Name
        mChatName = intent.getStringExtra(Constants.IntentKeys.CHAT_NAME)
        //Room
        mRoomId = intent.getIntExtra(Constants.IntentKeys.ROOM_ID, 0)
        mRoom = controller.getRoomInfo(mRoomId)
        //Contact Id & Contact Info
        contactId = intent.getIntExtra(Constants.IntentKeys.PARTICIPANT_ID, 0)
        mContact = controller.getContactInfo(contactId)
        //Navigation
        mIntentFromContact = intent.getBooleanExtra(Constants.IntentKeys.FROM_CONTACTS, false)

        /*UI*/
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
        //Display Room State info (favorite room)
        mRoom?.let {
            it.type?.let {
                asc_favorite_button.isPressed = it == Constants.RoomState.TYPE_FAVORITE || it == Constants.RoomState.TYPE_UNREAD_FAVORITE
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

        //Notifications switch
        asc_notif_switch.isSelected = false
        asc_notif_switch.setOnCheckedChangeListener { compoundButton, b ->
            if (b) { //Notifications ON
                //TODO: Enable Notifications
            } else {
                //TODO: Disable Notifications
            }
        }
        //Favorite Room (current private chat || Contact)
        asc_favorite_button.setOnTouchListener { view, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                mRoom?.let {
                    asc_favorite_button.isPressed = !asc_favorite_button.isPressed
                    controller.favoriteRoom(mRoom, asc_favorite_button.isPressed)

                }
            }
            true
        }

        //OnClick
        asc_chat_container.setOnClickListener(this)
        asc_call_container.setOnClickListener(this)
        asc_video_container.setOnClickListener(this)
        asc_money_container.setOnClickListener(this)
        asc_favemsg_container.setOnClickListener(this)
        asc_share_container.setOnClickListener(this)
        asc_clear_conversation.setOnClickListener(this)
        asc_block_container.setOnClickListener(this)
    }

    override fun couldNotFavoriteContact() {
        asc_favorite_button.isPressed = false
    }

    override fun onClick(p0: View?) {
        when (p0) {
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
                //TODO
                Toast.makeText(context, "In progress", Toast.LENGTH_SHORT).show()
            }
        /*Video Call with contact*/
            asc_video_container -> {
                //TODO
                Toast.makeText(context, "In progress", Toast.LENGTH_SHORT).show()
            }
        /*Send Money to Contact*/
            asc_money_container -> {
                //TODO
                Toast.makeText(context, "In progress", Toast.LENGTH_SHORT).show()
            }
        /*Favorite Messages with Contact*/
            asc_favemsg_container -> {
                //TODO
                Toast.makeText(context, "In progress", Toast.LENGTH_SHORT).show()
            }
        /*Share with Contact*/
            asc_share_container -> {
                //TODO
                Toast.makeText(context, "In progress", Toast.LENGTH_SHORT).show()
            }
        /*Clear conversation*/
            asc_clear_conversation -> {
                //TODO
                Toast.makeText(context, "In progress", Toast.LENGTH_SHORT).show()
            }
        /*Block / Unblock Contact*/
            asc_block_container -> {
                //TODO
                Toast.makeText(context, "In progress", Toast.LENGTH_SHORT).show()
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

    /*User feedback*/
    override fun feedback(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

}