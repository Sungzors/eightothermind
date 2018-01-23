package com.phdlabs.sungwon.a8chat_android.structure.setting.chat

import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import com.phdlabs.sungwon.a8chat_android.structure.setting.SettingContract

/**
 * Created by SungWon on 1/22/2018.
 */
class ChatSettingActivity: CoreActivity(), SettingContract.Chat.View{

    override lateinit var controller: SettingContract.Chat.Controller

    override fun layoutId(): Int = R.layout.activity_settings_chat

    override fun contentContainerId(): Int = R.id.asc_fragment_container

    override fun onStart() {
        super.onStart()
        ChatSettingController(this)
        controller.start()
        setUpViews()
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
        setToolbarTitle("asdfgh")
    }

    override fun finishActivity() {
    }
}