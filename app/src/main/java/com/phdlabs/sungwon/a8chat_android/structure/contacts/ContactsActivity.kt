package com.phdlabs.sungwon.a8chat_android.structure.contacts

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RadioButton
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.structure.contacts.fragments.ChannelsFragment
import com.phdlabs.sungwon.a8chat_android.structure.contacts.fragments.ContactsFragment
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import kotlinx.android.synthetic.main.activity_contacts.*
import kotlinx.android.synthetic.main.toolbar.*

/**
 * Created by paix on 2/13/18.
 * [ContactsActivity] This activity holds separate fragments to display
 * user contacts & user channels
 */

class ContactsActivity : CoreActivity(), ContactsAContract.View, View.OnClickListener {

    /*Controller*/
    override lateinit var controller: ContactsAContract.Controller

    /*Layout*/
    override fun layoutId(): Int = R.layout.activity_contacts

    override fun contentContainerId(): Int = R.id.ac_fragment_container

    override var activity: ContactsActivity = this

    /*LifeCycle*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*Controller*/
        ContactsAController(this)
        /*Toolbar*/
        setupToolbar()
        controller.onCreate()
    }

    override fun onStart() {
        super.onStart()
        controller.start()
    }

    override fun onResume() {
        super.onResume()
        controller.resume()
    }

    override fun onStop() {
        super.onStop()
        controller.stop()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (!controller.permissionResults(requestCode, permissions, grantResults)) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    /*Toolbar*/
    private fun setupToolbar() {
        //Toolbar
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.whiteTransparent)
        toolbar_title.text = getString(R.string.my_contacts)
        toolbar_leftoolbart_action.setImageDrawable(getDrawable(R.drawable.ic_back))
        toolbar_leftoolbart_action.scaleType = ImageView.ScaleType.CENTER
        toolbar_leftoolbart_action.setOnClickListener(this)
        //Segmented control default state
        ac_button_contacts.text = "Contacts" //TODO: Add contacts number
        ac_button_channels.text = "Channels" //TODO: Add channel number
        ac_button_contacts.setOnClickListener(this)
        ac_button_channels.setOnClickListener(this)
        ac_button_contacts.isChecked = true
        ac_button_channels.isChecked = false
    }

    override fun updateContactSelector(string: String, contactCount: Int) {
        if (contactCount > 0) {
            //Visible container
            ac_fragment_container.visibility = View.VISIBLE
            //Invisible zero state
            ac_textView_zero_state.visibility = View.GONE
            //Update UI
            ac_button_contacts.text = string
            //Set contacts fragment
            replaceFragment(ContactsFragment(), false)
        } else {
            //Visible Container
            ac_fragment_container.visibility = View.VISIBLE
            //Visible Zero State
            ac_textView_zero_state.visibility = View.VISIBLE
        }

    }

    override fun updateChannelsSelector(string: String, channelCount: Int) {
        if (channelCount > 0) {
            //TODO: Retrieve & show channels
        } else {
            //Visible Container
            ac_fragment_container.visibility = View.VISIBLE
            replaceFragment(ChannelsFragment(), false)
        }
    }

    /*On Click*/
    override fun onClick(p0: View?) {

        when (p0) {
        /**
         * Back to [MainActivity]
         * */
            toolbar_leftoolbart_action -> onBackPressed()

        /**
         * Contacts
         * */
            ac_button_contacts -> {
                // - set contacts fragment
                updateContactSelector("Contacts", 0)
                controller.loadContacts()

            }
        /**
         * Channels
         * */
            ac_button_channels -> {
                //TODO: Load channels
                // - set channels fragment
                updateChannelsSelector("Channels", 0)

            }
        }
    }

}