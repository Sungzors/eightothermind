package com.phdlabs.sungwon.a8chat_android.structure.profile

import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity

/**
 * Created by SungWon on 10/2/2017.
 */
class ProfileActivity: CoreActivity(), ProfileContract.View {
    override fun layoutId() = R.layout.activity_profile

    override fun contentContainerId() = 0

    override lateinit var controller: ProfileContract.Controller

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