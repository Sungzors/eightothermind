package com.phdlabs.sungwon.a8chat_android.structure.main

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.MenuItem
import android.view.WindowManager
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import com.phdlabs.sungwon.a8chat_android.structure.main.lobby.LobbyFragment
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Created by SungWon on 10/13/2017.
 */
class MainActivity: CoreActivity(), MainContract.View{

    override lateinit var controller: MainContract.Controller

    override fun layoutId() = R.layout.activity_main

    override fun contentContainerId() = R.id.am_content_frame

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.gradientRight)

        showTabs()
    }

    override fun onStart() {
        super.onStart()
        MainAController(this)
        controller.start()
    }

    override fun onResume() {
        super.onResume()
//        replaceFragment(LobbyFragment.newInstance(), false)
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

    private fun showTabs(){
        am_bottom_tab_nav.setOnNavigationItemSelectedListener { item ->
            onTabSelected(item)
            true
        }
        am_bottom_tab_nav.selectedItemId = R.id.mmt_home
    }

    private fun onTabSelected(item: MenuItem){
        when (item.itemId){
            R.id.mmt_home -> replaceFragment(LobbyFragment.newInstance(), false)
            R.id.mmt_camera -> controller.showCamera()
            R.id.mmt_profile -> controller.showProfile()
        }
    }

}