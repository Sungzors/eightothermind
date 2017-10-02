package com.phdlabs.sungwon.a8chat_android.structure.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
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
        al_login_button.setOnClickListener({
            val intent = Intent(this, RegisterActivity::class.java)
            intent.putExtra(Constants.IntentKeys.LOGIN_KEY, "register")
            startActivity(intent)
        })
        al_signin_button.setOnClickListener({
            Toast.makeText(this, "shitclicked!", Toast.LENGTH_SHORT).show()
        })
        al_signin_button.setOnClickListener({
            val intent = Intent(this, RegisterActivity::class.java)
            intent.putExtra(Constants.IntentKeys.LOGIN_KEY, "login")
            startActivity(intent)
        })
    }
}
