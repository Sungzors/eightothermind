package com.phdlabs.sungwon.a8chat_android.structure.myProfile.update

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.api.data.UserData
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import com.phdlabs.sungwon.a8chat_android.structure.main.MainActivity
import com.phdlabs.sungwon.a8chat_android.structure.myProfile.ProfileContract
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import com.phdlabs.sungwon.a8chat_android.utility.camera.CircleTransform
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_my_profile_update.*
import kotlinx.android.synthetic.main.toolbar.*

/**
 * Created by SungWon on 10/2/2017.
 * Updated by JPAM on 02/16/2017
 */
class MyProfileUpdateActivity : CoreActivity(), ProfileContract.Update.View,
        AdapterView.OnItemSelectedListener, View.OnClickListener {

    /*Properties*/
    private var profilePic: ImageView? = null
    private var language: String = "english"

    /*UI*/
    override fun layoutId() = R.layout.activity_my_profile_update

    override fun contentContainerId() = 0

    /*Controller*/
    override lateinit var controller: ProfileContract.Update.Controller
    override val getUpdateActivityMy = this

    /*Properties*/
    private var willEdit: Boolean = false
    override var isUpdating: Boolean = false
        get() = willEdit

    /*LifeCycle*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Controller init
        MyProfileUpdateAController(this)
        intent.hasExtra(Constants.ProfileIntents.WILL_EDIT_PROFILE).let {
            if (it) {
                willEdit = it
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (willEdit) {
            setToolbarTitle("Update Profile")
            toolbar_left_action_container.visibility = View.VISIBLE
            toolbar_leftoolbart_action.visibility = View.VISIBLE
            ap_submit_button.text = getString(R.string.update_profile)
            toolbar_leftoolbart_action.setImageDrawable(getDrawable(R.drawable.ic_back))

        } else {
            setToolbarTitle("Tell Us About Yourself")
            toolbar_left_action_container.visibility = View.GONE
            toolbar_leftoolbart_action.visibility = View.GONE
            ap_submit_button.text = getString(R.string.get_started)
        }
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
        get() = UserData(ap_first_name.text.toString().trim(), ap_last_name.text.toString().trim(), language, "")

    override fun nullChecker(): Boolean = (ap_first_name.text.toString() == "" || ap_last_name.text.toString() == "")


    /*On Click*/
    override fun onClick(p0: View?) {
        when (p0) {
        /*Back*/
            toolbar_left_action_container -> onBackPressed()
        /*Change Photo*/
            ap_profile_pic -> controller.showPicture(this)
        /*Submit Profile || Update Profile*/
            ap_submit_button -> controller.postProfile()
        }
    }

    private fun setClickers() {
        toolbar_left_action_container.setOnClickListener(this)
        ap_profile_pic.setOnClickListener(this)
        ap_submit_button.setOnClickListener(this)
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