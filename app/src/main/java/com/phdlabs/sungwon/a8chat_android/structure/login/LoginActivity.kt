package com.phdlabs.sungwon.a8chat_android.structure.login

import android.app.Activity
import android.content.Intent
import android.text.InputFilter
import android.text.SpannableString
import android.text.style.UnderlineSpan
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import kotlinx.android.synthetic.main.activity_login.*

/**
 * Created by JPAM on 03/15/2018
 * */

class LoginActivity : CoreActivity() {

    override fun layoutId() = R.layout.activity_login

    override fun contentContainerId() = 0

    override fun onStart() {
        super.onStart()
        /*Register*/
        al_login_button.setOnClickListener({
            val intent = Intent(this, RegisterActivity::class.java)
            intent.putExtra(Constants.IntentKeys.LOGIN_KEY, "register")
            startActivityForResult(intent, Constants.RequestCodes.LOGIN_SIGNUP)
        })
        /*LogIn*/
        al_signin_button.setOnClickListener({
            val intent = Intent(this, RegisterActivity::class.java)
            intent.putExtra(Constants.IntentKeys.LOGIN_KEY, "login")
            startActivityForResult(intent, Constants.RequestCodes.LOGIN_SIGNUP)
        })
        setupUI()
    }

    private fun setupUI() {
        val content = SpannableString(getString(R.string.sign_in))
        content.setSpan(UnderlineSpan(), 0, content.length, 0)
        al_signin_button.text = content
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.RequestCodes.LOGIN_SIGNUP) {
                finish()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}
