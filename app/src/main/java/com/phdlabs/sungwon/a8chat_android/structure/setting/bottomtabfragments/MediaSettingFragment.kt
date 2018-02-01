package com.phdlabs.sungwon.a8chat_android.structure.setting.bottomtabfragments

import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreFragment
import com.phdlabs.sungwon.a8chat_android.structure.setting.SettingContract

/**
 * Created by SungWon on 2/1/2018.
 */
class MediaSettingFragment : CoreFragment(), SettingContract.MediaFragment.View {

    override lateinit var controller: SettingContract.MediaFragment.Controller

    override fun layoutId() = R.layout.fragment_chat_setting_media

    companion object {
        fun newInstance(): MediaSettingFragment = MediaSettingFragment()
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
}