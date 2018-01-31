package com.phdlabs.sungwon.a8chat_android.structure.setting.chat

import android.widget.Toast
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.model.user.User
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import com.phdlabs.sungwon.a8chat_android.structure.setting.SettingContract
import kotlinx.android.synthetic.main.activity_settings_chat.*

/**
 * Created by SungWon on 1/22/2018.
 */
class ChatSettingActivity: CoreActivity(), SettingContract.Chat.View{

    override lateinit var controller: SettingContract.Chat.Controller

    override fun layoutId(): Int = R.layout.activity_settings_chat

    override fun contentContainerId(): Int = R.id.asc_fragment_container

    private var mUserId = 0
    private var mUser : User?

    init {
        mUser = null
    }

    override fun onStart() {
        super.onStart()
        ChatSettingController(this)
        controller.start()
        setUpViews()
        setUpTabs()
        setUpClickers()
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

    private fun setUpViews(){
        setToolbarTitle("asdfgh"/*mUser.name*/)
        showBackArrow(R.drawable.ic_back)
    }

    private fun setUpTabs(){
        asc_bottomnav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId){
                R.id.mst_media -> {
                    Toast.makeText(this, "gurp", Toast.LENGTH_SHORT).show()
                }
                R.id.mst_file -> {
                    Toast.makeText(this, "gurp", Toast.LENGTH_SHORT).show()
                }
            }
            true
        }
    }

    private fun setUpClickers(){
        asc_chat_container.setOnClickListener {
            onBackPressed()
        }
        asc_call_container.setOnClickListener {

        }
        asc_video_container.setOnClickListener {

        }
        asc_money_container.setOnClickListener {

        }
        asc_favemsg_container.setOnClickListener {

        }
        asc_share_container.setOnClickListener {

        }
        asc_close_container.setOnClickListener {

        }
        asc_block_container.setOnClickListener {

        }
    }

    override fun finishActivity() {
    }
}