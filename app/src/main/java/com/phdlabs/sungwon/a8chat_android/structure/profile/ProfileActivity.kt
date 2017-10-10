package com.phdlabs.sungwon.a8chat_android.structure.profile

import android.content.Intent
import android.widget.ImageView
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.api.data.UserData
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import kotlinx.android.synthetic.main.activity_profile.*

/**
 * Created by SungWon on 10/2/2017.
 */
class ProfileActivity: CoreActivity(), ProfileContract.View {

    var profilePic: ImageView? = null

    override fun layoutId() = R.layout.activity_profile

    override fun contentContainerId() = 0

    override lateinit var controller: ProfileContract.Controller

    override fun onStart() {
        super.onStart()
        ProfileAController(this)
        profilePic = ap_profile_pic
        controller.start()
        setClickers()
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

    override val getActivity = this

    override val getProfileImageView = profilePic

    override val getUserData: UserData
        get() = UserData(ap_first_name.text.toString(), ap_last_name.text.toString(), ap_language_spinner.text.toString())

    private fun setClickers(){
        profilePic!!.setOnClickListener({
            controller.showPicture(this)
        })
        ap_submit_button.setOnClickListener({
            controller.postProfile()
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        controller.onPictureResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }
}