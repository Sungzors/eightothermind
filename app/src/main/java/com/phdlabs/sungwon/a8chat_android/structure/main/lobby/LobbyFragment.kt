package com.phdlabs.sungwon.a8chat_android.structure.main.lobby

import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreFragment

/**
 * Created by SungWon on 10/17/2017.
 */
class LobbyFragment: CoreFragment(), LobbyContract.View {

    override lateinit var controller: LobbyContract.Controller

    override fun layoutId() = R.layout.fragment_lobby

    override fun onStart() {
        super.onStart()
        LobbyController(this)
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