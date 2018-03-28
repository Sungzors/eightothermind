package com.phdlabs.sungwon.a8chat_android.structure.setting.chat

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.telephony.PhoneNumberUtils
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.model.contacts.Contact
import com.phdlabs.sungwon.a8chat_android.model.room.Room
import com.phdlabs.sungwon.a8chat_android.structure.chat.ChatActivity
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import com.phdlabs.sungwon.a8chat_android.structure.favorite.message.FavoriteMessageActivity
import com.phdlabs.sungwon.a8chat_android.structure.setting.SettingContract
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import com.phdlabs.sungwon.a8chat_android.utility.camera.CircleTransform
import com.squareup.picasso.Picasso
import com.vicpin.krealmextensions.save
import kotlinx.android.synthetic.main.activity_settings_chat.*
import java.util.*

/**
 * Created by SungWon on 1/22/2018.
 * Updated by JPAM on 02/22/2018
 */
class ChatSettingActivity : CoreActivity(), SettingContract.Chat.View, View.OnClickListener {

    /*Controller*/
    override lateinit var controller: SettingContract.Chat.Controller
    override var activity: ChatSettingActivity? = this

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

        asc_favemsg_tv.text = resources.getString(
                R.string.fave_messages,
                "0"
        )

        controller.getFavorite(mRoomId, {
            asc_favemsg_tv.text = resources.getString(
                    R.string.fave_messages,
                    it.toString()
            )
        })

        /*UI*/
        setupToolbar()
        setupUserInfo()
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
            //TODO: Back from chat launched from contacts screen, todo refreshChannels media , files & favorite messages only
        }

        if(resultCode == Constants.ResultCode.SUCCESS){
            setResult(Constants.ResultCode.SUCCESS)
            finish()
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
        } ?: run {
            //Not a contact
            val alertDialog = AlertDialog.Builder(this)
            alertDialog.setTitle("Please add $mChatName using a phone number in your Contacts App")
            alertDialog.setPositiveButton("OK") { _, _ ->
                onBackPressed()
            }
            alertDialog.show()
        }
        //Display Room State info (favorite room)
        mRoom?.let {
            it.type?.let {
                asc_favorite_button.isPressed = it == Constants.RoomState.TYPE_FAVORITE || it == Constants.RoomState.TYPE_UNREAD_FAVORITE
                mContact?.isFavorite = it == Constants.RoomState.TYPE_FAVORITE || it == Constants.RoomState.TYPE_UNREAD_FAVORITE
                mContact?.save()
            }
        }
    }

    private fun setUpClickers() {
        //Notifications switch //TODO: Notifications
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
                    //UI
                    asc_favorite_button.isPressed = !asc_favorite_button.isPressed
                    mContact?.isFavorite = asc_favorite_button.isPressed
                    //Realm
                    mContact?.save()
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
        asc_clear_conv_container.setOnClickListener(this)
        acs_block_container.setOnClickListener(this)
        //Frag Container
        acs_button_media.setOnClickListener(this)
        acs_button_files.setOnClickListener(this)
        acs_button_media.text = "Media"
        acs_button_files.text = "Files"
        //Init state
        acs_button_media.isChecked = true //Default
        acs_button_media.performClick()
        acs_button_files.isChecked = false

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
                val intent = Intent(context, FavoriteMessageActivity::class.java)
                intent.putExtra(Constants.IntentKeys.ROOM_ID, mRoomId)
                intent.putExtra(Constants.IntentKeys.FAVE_TYPE, 1)
                startActivityForResult(intent, Constants.RequestCodes.OPEN_FAVE)
            }
        /*Share with Contact*/
            asc_share_container -> {
                //TODO
                Toast.makeText(context, "In progress", Toast.LENGTH_SHORT).show()
            }
        /*Clear conversation*/
            asc_clear_conv_container -> {
                //TODO
                Toast.makeText(context, "In progress", Toast.LENGTH_SHORT).show()
            }
        /*Block / Unblock Contact*/
            acs_block_container -> {
                //TODO
                Toast.makeText(context, "In progress", Toast.LENGTH_SHORT).show()
            }
        /*Media Left Tab*/
            acs_button_media -> {
                controller.getSharedMediaPrivate(contactId)
            }
        /*Files Right Tab*/
            acs_button_files -> {
                controller.getSharedFilesPrivate(mRoomId)
            }
        }
    }

    /*Favorite*/
    override fun couldNotFavoriteContact() {
        asc_favorite_button.isPressed = false
    }

    /*Media Selectors Count*/
    fun updateSelectorTitle(title1: String?, title2: String?) {
        title1?.let {
            acs_button_media.text = it
        }
        title2?.let {
            acs_button_files.text = it
        }
    }

    /*User feedback*/
    override fun feedback(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

}