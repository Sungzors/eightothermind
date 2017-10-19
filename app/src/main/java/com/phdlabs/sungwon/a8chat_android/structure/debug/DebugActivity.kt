package com.phdlabs.sungwon.a8chat_android.structure.debug

import android.content.Intent
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import com.phdlabs.sungwon.a8chat_android.structure.login.LoginActivity
import com.phdlabs.sungwon.a8chat_android.structure.main.MainActivity
import com.phdlabs.sungwon.a8chat_android.structure.profile.ProfileActivity
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import com.phdlabs.sungwon.a8chat_android.utility.Preferences
import kotlinx.android.synthetic.main.activity_debug.*

/**
 * Created by SungWon on 10/2/2017.
 */
class DebugActivity: CoreActivity(){
    override fun layoutId() = R.layout.activity_debug

    override fun contentContainerId() = 0

    override fun onStart() {
        super.onStart()
        setClickers()

        //debug stopper here to get the values
        val token: String? = Preferences(this).getPreferenceString(Constants.PrefKeys.TOKEN_KEY)
        val userID: Int? = Preferences(this).getPreferenceInt(Constants.PrefKeys.USER_ID)

        ad_token.text = "Token: " + token?.length
        ad_user_id.text = "User ID: " + userID
    }

    private fun setClickers(){
        ad_start_button.setOnClickListener({
            //TODO: build launch
        })
        ad_login_button.setOnClickListener({
            startActivity(Intent(this, LoginActivity::class.java))
        })
        ad_profile_button.setOnClickListener({
            startActivity(Intent(this, ProfileActivity::class.java))
        })
        ad_main_button.setOnClickListener({
            startActivity(Intent(this, MainActivity::class.java))
        })
        ad_chat_button.setOnClickListener({
            //TODO: build chat
        })
        ad_sandbox_button.setOnClickListener({
            //TODO: build sandbox
        })
    }
}