package com.phdlabs.sungwon.a8chat_android.structure.main

import android.view.MenuItem
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Created by SungWon on 10/13/2017.
 */
class MainActivity: CoreActivity(), MainContract.View{

    override lateinit var controller: MainContract.Controller

    override fun layoutId() = R.layout.activity_main

    override fun contentContainerId() = R.id.am_content_frame

    override fun onStart() {
        super.onStart()
        MainAController(this)
        controller.start()
        showTabs()
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

    private fun showTabs(){
        am_bottom_tab_nav.setOnNavigationItemSelectedListener { item ->
            onTabSelected(item)
            true
        }
    }

    private fun onTabSelected(item: MenuItem){
        when (item.itemId){
            R.id.mmt_home -> controller.showHome()
            R.id.mmt_camera -> controller.showCamera()
            R.id.mmt_profile -> controller.showProfile()
        }
    }

}