package com.phdlabs.sungwon.a8chat_android.structure.splashScreen

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import com.phdlabs.sungwon.a8chat_android.structure.main.MainActivity

/**
 * Created by paix on 3/15/18.
 * - Splash Screen Used to Load Initial Data if user is Signed In
 */
class SplashScreenActivity : AppCompatActivity() {

    /*LifeCycle*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /**
         * If a user exists transition to Main Activity
         * */
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()

    }
}