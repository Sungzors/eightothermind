package com.phdlabs.sungwon.a8chat_android.structure.login

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : CoreActivity() {

    override fun layoutId() = R.layout.activity_login

    override fun contentContainerId() = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        /*Register*/
        al_login_button.setOnClickListener({
            val intent = Intent(this, RegisterActivity::class.java)
            intent.putExtra(Constants.IntentKeys.LOGIN_KEY, "register")
            startActivity(intent)
        })
        /*LogIn*/
        al_signin_button.setOnClickListener({
            val intent = Intent(this, RegisterActivity::class.java)
            intent.putExtra(Constants.IntentKeys.LOGIN_KEY, "login")
            startActivity(intent)
        })
        setupUI()
    }

    private fun setupUI(){
        val content = SpannableString(getString(R.string.sign_in))
        content.setSpan(UnderlineSpan(), 0, content.length, 0)
        al_signin_button.text = content
    }

}
