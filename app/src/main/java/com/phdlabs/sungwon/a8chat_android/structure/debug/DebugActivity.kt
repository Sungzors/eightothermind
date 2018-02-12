package com.phdlabs.sungwon.a8chat_android.structure.debug

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.db.UserManager
import com.phdlabs.sungwon.a8chat_android.structure.camera.CameraActivity
import com.phdlabs.sungwon.a8chat_android.structure.channel.channelshow.ChannelShowActivity
import com.phdlabs.sungwon.a8chat_android.structure.channel.create.ChannelCreateActivity
import com.phdlabs.sungwon.a8chat_android.structure.channel.postshow.ChannelPostShowActivity
import com.phdlabs.sungwon.a8chat_android.structure.chat.ChatActivity
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import com.phdlabs.sungwon.a8chat_android.structure.event.create.EventCreateActivity
import com.phdlabs.sungwon.a8chat_android.structure.login.LoginActivity
import com.phdlabs.sungwon.a8chat_android.structure.main.MainActivity
import com.phdlabs.sungwon.a8chat_android.structure.profile.ProfileActivity
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.rx.RealmObservableFactory
import kotlinx.android.synthetic.main.activity_debug.*
import java.io.File

/**
 * Created by SungWon on 10/2/2017.
 * Updated by JPAM on 12/21/2017
 */
class DebugActivity : CoreActivity() {
    override fun layoutId() = R.layout.activity_debug

    override fun contentContainerId() = 0

    private lateinit var realmConfig: RealmConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*Realm*/ //TODO: Remove for production, this should be in Application()
        Realm.init(this)
        realmConfig = RealmConfiguration.Builder().rxFactory(RealmObservableFactory())
                .deleteRealmIfMigrationNeeded()
                .build()
        Realm.setDefaultConfiguration(realmConfig)
        //DEV
        print("REALM PATH: " + realmConfig.path) //Path for realm browser

    }

    override fun onStart() {
        super.onStart()
        setClickers()
        /*Get current user*/
        UserManager.instance.getCurrentUser { success, user, token ->
            if (success) {
                ad_loading_text.text = "User Loaded"
                ad_token.text = "Token: " + token?.token?.length
                ad_user_id.text = "User ID: " + user?.id
            } else {
                ad_loading_text.text = "User Load failed"
            }
        }
    }

    private fun setClickers() {
        ad_start_button.setOnClickListener({
            startActivity(Intent(this, LoginActivity::class.java))
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
            startActivity(Intent(this, ChatActivity::class.java))
        })
        ad_create_channel_button.setOnClickListener {
            startActivity(Intent(this, ChannelCreateActivity::class.java))
        }
        ad_channel_post_button.setOnClickListener {
            val intent = Intent(this, ChannelPostShowActivity::class.java)
            intent.putExtra(Constants.IntentKeys.MESSAGE_ID, "11")
            startActivity(intent)
        }
        ad_channel_show.setOnClickListener {
            startActivity(Intent(this, ChannelShowActivity::class.java))
        }
        ad_sandbox_button.setOnClickListener({
            var realm = Realm.getInstance(realmConfig)
            var exportFile : File? = null
            try{
                exportFile = File(this.externalCacheDir, "export.realm")
                exportFile.delete()
                realm.writeCopyTo(exportFile)
            } catch (e : io.realm.internal.IOException){
                e.printStackTrace()
            }

            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "plain/text"
            intent.putExtra(Intent.EXTRA_EMAIL, "sung@phdlabs.com")
            intent.putExtra(Intent.EXTRA_SUBJECT, "Hello me duckduckducks")
            intent.putExtra(Intent.EXTRA_TEXT, "Ducks")
            val u = Uri.fromFile(exportFile)
            intent.putExtra(Intent.EXTRA_STREAM, u)

            startActivity(Intent.createChooser(intent, "wut"))
        })
        ad_camera_button.setOnClickListener({
            startActivity(Intent(this, CameraActivity::class.java))
        })
        ad_event_create.setOnClickListener {
            startActivity(Intent(this, EventCreateActivity::class.java))
        }
    }
}