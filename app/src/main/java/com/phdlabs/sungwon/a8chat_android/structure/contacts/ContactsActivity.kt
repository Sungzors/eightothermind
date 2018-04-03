package com.phdlabs.sungwon.a8chat_android.structure.contacts

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.structure.contacts.searchChannels.ChannelsFragment
import com.phdlabs.sungwon.a8chat_android.structure.contacts.searchContacts.ContactsFragment
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import kotlinx.android.synthetic.main.activity_contacts.*
import kotlinx.android.synthetic.main.toolbar.*

/**
 * Created by JPAM on 2/13/18.
 * [ContactsActivity] This activity holds separate fragments to display
 * user contacts & user channels
 */

class ContactsActivity : CoreActivity(), ContactsContract.EightFriends.View, View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener {


    /*Controller*/
    override lateinit var controller: ContactsContract.EightFriends.Controller

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
        toolbar_left_action_container.setOnClickListener(this)
        //Segmented control default state
        ac_button_contacts.text = getString(R.string.contacts_selector_default_text)
        ac_button_channels.text = getString(R.string.channels_selector_default_text)
        ac_button_contacts.setOnClickListener(this)
        ac_button_channels.setOnClickListener(this)
        ac_button_contacts.isChecked = true //Start loading contacts
        ac_button_channels.isChecked = false
        //Search
        ac_searchView.setOnClickListener(this)
    }

    override fun updateContactSelector(string: String, contactCount: Int) {

        if (contactCount > 0) {
            //Visible container
            ac_fragment_container.visibility = View.VISIBLE
            //Update UI
            ac_button_contacts.text = string
            //Set contacts fragment
            replaceFragment(ContactsFragment(), false)
        } else {
            //Visible Container
            ac_fragment_container.visibility = View.VISIBLE
        }

    }

    override fun updateChannelsSelector(string: String, channelCount: Int) {
        //Visible Container
        ac_fragment_container.visibility = View.VISIBLE
        ac_button_channels.text = string
        replaceFragment(ChannelsFragment(), false)
    }

    /*On Click*/
    override fun onClick(p0: View?) {

        when (p0) {
        /**
         * Back to [MainActivity]
         * */
            toolbar_left_action_container -> onBackPressed()

        /**
         * Contacts
         * */
            ac_button_contacts -> {
                // - set contacts fragment
                updateContactSelector("Contacts", 0)
                controller.loadContactsCheckCache()

            }
        /**
         * Channels
         * */
            ac_button_channels -> {
                // - set channels fragment
                updateChannelsSelector("Channels", 0)
                controller.loadChannels()
            }

        /**
         * Full search view touch
         * */
            ac_searchView -> {
                ac_searchView.isIconified = false
            }
        }
    }

    /*Refresh current screen*/
    override fun onRefresh() {
        if (ac_button_contacts.isChecked && !ac_button_channels.isChecked) {
            controller.loadContactsFromApi()
        } else if (ac_button_channels.isChecked && !ac_button_contacts.isChecked) {
            controller.loadChannels()
        }
    }

}