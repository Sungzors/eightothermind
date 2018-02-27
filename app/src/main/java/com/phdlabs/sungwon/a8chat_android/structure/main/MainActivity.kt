package com.phdlabs.sungwon.a8chat_android.structure.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.structure.contacts.ContactsActivity
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import com.phdlabs.sungwon.a8chat_android.structure.createnew.CreateNewActivity
import com.phdlabs.sungwon.a8chat_android.structure.main.lobby.LobbyFragment
import com.phdlabs.sungwon.a8chat_android.structure.myProfile.detail.MyProfileFragment
import com.phdlabs.sungwon.a8chat_android.structure.myProfile.update.MyProfileUpdateActivity
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.view.*
import kotlinx.android.synthetic.main.toolbar_main.*

/**
 * Created by SungWon on 10/13/2017.
 * Updated by JPAM on 02/13/2018
 */
class MainActivity : CoreActivity(), MainContract.View, View.OnClickListener {

    /*Controller*/
    override lateinit var controller: MainContract.Controller

    /*Layout*/
    override fun layoutId() = R.layout.activity_main

    override fun contentContainerId() = R.id.am_content_frame

    /*Properties*/
    override val activity: MainActivity = this
    private var lastSelectedTabId: Int? = null

    /*Instances*/
    private var mLobbyFragment: LobbyFragment = LobbyFragment.newInstance()

    init {
        //Fresh data on launch
    }

    /*LifeCycle*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Default toolbar
        setupToolbars()
        toolbarControl(true)
        //Click listeners
        setupClickers()
        //Tabs
        showTabs(true, false)
    }

    override fun onStart() {
        super.onStart()
        MainAController(this)
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

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.CameraIntents.CAMERA_REQUEST_CODE) { //Camera
                //Set home button selected
                am_bottom_tab_nav.setOnNavigationItemSelectedListener(null)
                showTabs(false, true)
            } else if (requestCode == Constants.ContactItems.CONTACTS_REQ_CODE) { //Contacts-Eight Friends
                //todo: required contacts action if needed
            } else if (requestCode == Constants.ProfileIntents.EDIT_MY_PROFIILE) { //Profile
                //todo: required profile action if needed
            } else if (requestCode == Constants.ContactItems.INVITE_CONTACTS_REQ_CODE) { //Invite Contact
                //todo: required invite contact action if needed
            } else if (requestCode == Constants.ChannelRequestCodes.CREATE_NEW_BACK_REQ_CODE) {
                //Refresh Lobby if coming back from Create New screen
                mLobbyFragment.controller.setRefreshFlag(true)
                am_bottom_tab_nav.setOnNavigationItemSelectedListener(null)
                showTabs(false, false)
            }

        } else if (resultCode == Activity.RESULT_CANCELED) {
            //Actions
            if (requestCode == Constants.CameraIntents.CAMERA_REQUEST_CODE) { //Camera
                //Don't refreshChannels Lobby
                mLobbyFragment.controller.setRefreshFlag(false)
                //Set home button selected
                am_bottom_tab_nav.setOnNavigationItemSelectedListener(null)
                showTabs(false, true)
            } else if (requestCode == Constants.ChannelRequestCodes.CREATE_NEW_BACK_REQ_CODE) {
                //Don't refreshChannels Lobby
                mLobbyFragment.controller.setRefreshFlag(false)
                //Set home button selected
                am_bottom_tab_nav.setOnNavigationItemSelectedListener(null)
                showTabs(false, false)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    /*Toolbar Control*/
    private fun setupToolbars() {
        //Status bar flags
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        //Profile Toolbar setup
        profile_toolbar.toolbar_title.text = getString(R.string.my_profile)
        profile_toolbar.toolbar_right_text.text = getString(R.string.my_profile_edit)
        profile_toolbar.toolbar_right_text.visibility = View.VISIBLE
        profile_toolbar.toolbar_right_text.setTextColor(ContextCompat.getColor(this, R.color.confirmText))
    }

    private fun toolbarControl(homeToolbar: Boolean) {
        if (homeToolbar) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.gradientRight)
            home_toolbar.visibility = View.VISIBLE
            profile_toolbar.visibility = View.GONE
        } else {
            window.statusBarColor = ContextCompat.getColor(this, R.color.eight_status_bar)
            home_toolbar.visibility = View.GONE
            profile_toolbar.visibility = View.VISIBLE
        }
    }

    /*Tab Control*/
    private fun showTabs(isLaunch: Boolean, backFromCamera: Boolean) {
        //Last selected item
        if (backFromCamera) {
            lastSelectedTabId?.let {
                am_bottom_tab_nav.selectedItemId = it
            }
        }
        //Navigation
        am_bottom_tab_nav.setOnNavigationItemSelectedListener { item ->
            onTabSelected(item)
            true
        }
        //Launch Lobby 1st time
        if (isLaunch) {
            am_bottom_tab_nav.selectedItemId = R.id.mmt_home
        }
    }

    private fun onTabSelected(item: MenuItem) {
        when (item.itemId) {

        /*Home*/
            R.id.mmt_home -> {
                super.onPostResume()
                setupToolbars()
                toolbarControl(true)
                /*If coming back from profile, do not refreshChannels data*/
                lastSelectedTabId?.let {
                    if (it == R.id.mmt_profile) {
                        mLobbyFragment = LobbyFragment.newInstance(false)
                    }
                }
                replaceFragment(contentContainerId(), mLobbyFragment, false)

                lastSelectedTabId = R.id.mmt_home
            }

        /*Camera*/
            R.id.mmt_camera -> controller.showCamera()

        /*Profile*/
            R.id.mmt_profile -> {
                super.onPostResume()
                toolbarControl(false)
                replaceFragment(contentContainerId(), MyProfileFragment.newInstance(), false)
                lastSelectedTabId = R.id.mmt_profile
            }
        }
    }


    /*On Click*/
    private fun setupClickers() {
        toolbar_left_action.setOnClickListener(this)
        profile_toolbar.toolbar_right_text.setOnClickListener(this)
        home_toolbar.toolbar_right_picture.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0) {

        /**Contacts -> top left action to [ContactsActivity]*/
            toolbar_left_action -> {
                startActivityForResult(Intent(this, ContactsActivity::class.java),
                        Constants.ContactItems.CONTACTS_REQ_CODE)
            }

        /**Edit Profile -> [MyProfileFragment]*/
            profile_toolbar.toolbar_right_text -> {
                val editIntent = Intent(this, MyProfileUpdateActivity::class.java)
                editIntent.putExtra(Constants.ProfileIntents.WILL_EDIT_PROFILE, true)
                startActivityForResult(editIntent,
                        Constants.ProfileIntents.EDIT_MY_PROFIILE)
            }
        /**Create New -> [CreateNewActivity]*/
            home_toolbar.toolbar_right_picture -> {
                val createNewIntent = Intent(this, CreateNewActivity::class.java)
                startActivityForResult(createNewIntent, Constants.ChannelRequestCodes.CREATE_NEW_BACK_REQ_CODE)

            }
        }
    }

}
