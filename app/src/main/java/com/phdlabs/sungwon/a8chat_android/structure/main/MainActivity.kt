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
import com.phdlabs.sungwon.a8chat_android.structure.main.lobby.LobbyFragment
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import kotlinx.android.synthetic.main.activity_main.*
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

    /*LifeCycle*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.gradientRight)
        //Click listeners
        setupClickers()
        //Tabs
        showTabs()
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

        if (resultCode == Activity.RESULT_OK || resultCode == Activity.RESULT_CANCELED) {

            /*Camera*/
            if (requestCode == Constants.CameraIntents.CAMERA_REQUEST_CODE) {
                am_bottom_tab_nav.selectedItemId = R.id.mmt_home

                /*Contacts*/
            } else if (requestCode == Constants.ContactIntens.CONTACTS_REQ_CODE) {
                //todo: required contacts action
            }

        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    /*Tab Control*/
    private fun showTabs() {
        am_bottom_tab_nav.setOnNavigationItemSelectedListener { item ->
            onTabSelected(item)
            true
        }
        am_bottom_tab_nav.selectedItemId = R.id.mmt_home
    }

    private fun onTabSelected(item: MenuItem) {
        when (item.itemId) {
            R.id.mmt_home -> {
                super.onPostResume()
                replaceFragment(LobbyFragment.newInstance(), false)
            }
            R.id.mmt_camera -> controller.showCamera()
            R.id.mmt_profile -> controller.showProfile()
        }
    }

    /*On Click*/
    override fun onClick(p0: View?) {
        when (p0) {

        /*Contacts -> top left action*/
            toolbar_left_action -> {
                startActivityForResult(Intent(this, ContactsActivity::class.java),
                        Constants.ContactIntens.CONTACTS_REQ_CODE)
            }
        }
    }

    fun setupClickers() {
        toolbar_left_action.setOnClickListener(this)
    }

}