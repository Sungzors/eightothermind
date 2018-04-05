package com.phdlabs.sungwon.a8chat_android.structure.myProfile.detail

import android.content.Intent
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.view.View
import android.widget.Toast
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.db.user.UserManager
import com.phdlabs.sungwon.a8chat_android.structure.contacts.invite.InviteContactsActivity
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreFragment
import com.phdlabs.sungwon.a8chat_android.structure.favorite.message.FavoriteMessageActivity
import com.phdlabs.sungwon.a8chat_android.structure.myProfile.notifications.NotificationsGlobalSettings
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import com.phdlabs.sungwon.a8chat_android.utility.camera.CircleTransform
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_my_profile.*
import java.util.*

/**
 * Created by JPAM on 2/16/18.
 * [MyProfileFragment]
 * managed from MainActivity Tab Bar
 */
class MyProfileFragment : CoreFragment(), View.OnClickListener {

    /*Layout*/
    override fun layoutId(): Int = R.layout.fragment_my_profile

    /*Companion*/
    companion object {
        fun newInstance() = MyProfileFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //User information
        displayUserInfo()
        //Set Clickers
        setClickers()

    }

    /*User Info -> Top card*/
    private fun displayUserInfo() {
        //Display cached info
        UserManager.instance.getCurrentUser { success, user, _ ->
            if (success) {
                user?.let {
                    //Display Profile picture
                    Picasso.with(context)
                            .load(it.avatar)
                            .resize(70, 70)
                            .onlyScaleDown()
                            .centerCrop()
                            .transform(CircleTransform())
                            .into(fmp_picture)
                    //Display Info
                    val fullName: Pair<Boolean, String?> = it.hasFullName()
                    if (fullName.first) {
                        fmp_name.text = fullName.second
                    } else {
                        fmp_name.text = it.first_name ?: "n/a"
                    }
                    fmp_phone_number.text = PhoneNumberUtils.formatNumber(it.phone, Locale.getDefault().country) ?: "n/a"
                    fmp_language.text = "Language: " + it.languages_spoken?.get(0)?.stringValue ?: "n/a"
                }
            }
        }
        UserManager.instance.getSelfFavoriteCount { count, error ->
            error?.let {
                showError(it)
            } ?: kotlin.run {
                fmp_fav_messages_text.text = resources.getString(
                        R.string.fave_messages,
                        count.toString()
                )
            }
        }
    }

    /*Clickable UI*/
    private fun setClickers() {
        fmp_container_calendars.setOnClickListener(this)
        fmp_payment_container.setOnClickListener(this)
        fmp_container_notifications_settings.setOnClickListener(this)
        fmp_container_fav_messages.setOnClickListener(this)
        fmp_container_account.setOnClickListener(this)
        fmp_container_terms_support.setOnClickListener(this)
        fmp_fav_notif_container.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0) {
        /*Calendars*/
            fmp_container_calendars -> {
                //Todo: Transition to calendars
                Toast.makeText(context, "In progress", Toast.LENGTH_SHORT).show()
            }
        /*Payments*/
            fmp_payment_container -> {
                //Todo: Transition to payments
                Toast.makeText(context, "In progress", Toast.LENGTH_SHORT).show()
            }
        /*Notification Settings*/
            fmp_container_notifications_settings -> {
                val intent = Intent(context, NotificationsGlobalSettings::class.java)
                startActivity(intent)
            }
        /*Favorite Messages*/
            fmp_container_fav_messages -> {
                val intent = Intent(context, FavoriteMessageActivity::class.java)
                intent.putExtra(Constants.IntentKeys.FAVE_TYPE, 3)
                startActivityForResult(intent, Constants.RequestCodes.OPEN_FAVE)
            }
        /*Account*/
            fmp_container_account -> {
                //Todo: Access Account settings
                Toast.makeText(context, "In progress", Toast.LENGTH_SHORT).show()
            }
        /*Terms & Support*/
            fmp_container_terms_support -> {
                //Todo: Access Terms & support
                Toast.makeText(context, "In progress", Toast.LENGTH_SHORT).show()
            }
        /*Invite friends*/
            fmp_fav_notif_container -> {
                activity?.startActivityForResult(Intent(context, InviteContactsActivity::class.java),
                        Constants.ContactItems.INVITE_CONTACTS_REQ_CODE)
            }
        }
    }

}