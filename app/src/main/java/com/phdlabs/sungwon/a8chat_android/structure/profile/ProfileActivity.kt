package com.phdlabs.sungwon.a8chat_android.structure.profile

import android.content.Intent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.api.data.UserData
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import com.phdlabs.sungwon.a8chat_android.structure.main.MainActivity
import com.phdlabs.sungwon.a8chat_android.utility.camera.CircleTransform
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*

/**
 * Created by SungWon on 10/2/2017.
 */
class ProfileActivity : CoreActivity(), ProfileContract.View, AdapterView.OnItemSelectedListener {

    /*Properties*/
    private var profilePic: ImageView? = null
    private var language: String = "english"

    /*UI*/
    override fun layoutId() = R.layout.activity_profile

    override fun contentContainerId() = 0

    /*Controller*/
    override lateinit var controller: ProfileContract.Controller
    override val getActivity = this

    /*LifeCycle*/
    override fun onStart() {
        super.onStart()
        setToolbarTitle("Tell Us About Yourself")
        ProfileAController(this)
        profilePic = ap_profile_pic
        val spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.language_array, android.R.layout.simple_spinner_item)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        ap_language_spinner.adapter = spinnerAdapter
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        controller.onPictureResult(requestCode, resultCode, data)

    }

    override fun startApp() {
        startActivity(Intent(this, MainActivity::class.java))
    }

    /*Data*/
    override val getProfileImageView = profilePic

    override val getUserData: UserData
        get() = UserData(ap_first_name.text.toString(), ap_last_name.text.toString(), language, "")

    override fun nullChecker(): Boolean = (ap_first_name.text.toString() == "" || ap_last_name.text.toString() == "")

    private fun setClickers() {
        ap_profile_pic.setOnClickListener({
            controller.showPicture(this)
        })
        ap_submit_button.setOnClickListener({
            controller.postProfile()
        })
    }

    /*Profile image*/
    override fun setProfileImageView(pictureUrl: String) { //UI
        Picasso.with(context).load("file://" + pictureUrl).transform(CircleTransform()).into(profilePic)

    }

    /*Language*/
    override fun onNothingSelected(p0: AdapterView<*>?) {
        language = "english"
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        language = p0!!.getItemAtPosition(p2).toString().toLowerCase()
    }
}