package com.phdlabs.sungwon.a8chat_android.structure.login

import android.content.Intent
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.widget.Toast
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.api.data.LoginData
import com.phdlabs.sungwon.a8chat_android.api.data.VerifyData
import com.phdlabs.sungwon.a8chat_android.api.event.ConfirmEvent
import com.phdlabs.sungwon.a8chat_android.api.event.ResendEvent
import com.phdlabs.sungwon.a8chat_android.api.response.ResendResponse
import com.phdlabs.sungwon.a8chat_android.api.response.TokenResponse
import com.phdlabs.sungwon.a8chat_android.api.rest.Caller
import com.phdlabs.sungwon.a8chat_android.api.rest.Rest
import com.phdlabs.sungwon.a8chat_android.api.utility.Callback8
import com.phdlabs.sungwon.a8chat_android.db.EventBusManager
import com.phdlabs.sungwon.a8chat_android.model.user.User
import com.phdlabs.sungwon.a8chat_android.model.user.registration.Token
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import com.phdlabs.sungwon.a8chat_android.structure.main.MainActivity
import com.phdlabs.sungwon.a8chat_android.structure.profile.ProfileActivity
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import com.vicpin.krealmextensions.queryFirst
import com.vicpin.krealmextensions.save
import kotlinx.android.synthetic.main.activity_confirm.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

/**
 * Created by SungWon on 9/26/2017.
 * Updated by JPAM on 12/21/2017
 */

class ConfirmActivity : CoreActivity() {

    /*Properties*/
    val mDataEventBus: EventBus = EventBusManager.instance().mDataEventBus
    val mCaller: Caller = Rest.getInstance().caller
    private var isRegister: Boolean = true
    private var user: User? = null

    override fun layoutId() = R.layout.activity_confirm
    override fun contentContainerId() = 0

    override fun onStart() {
        super.onStart()
        mDataEventBus.register(this)
        isRegister = intent.getBooleanExtra(Constants.IntentKeys.LOGIN_KEY, true)
        user = User().queryFirst()
        setToolbarTitle("Confirmation")
        setupUI()
        setupClickers()
    }

    override fun onStop() {
        super.onStop()
        mDataEventBus.unregister(this)
    }


    @Subscribe
    public fun onEventUIThread(event: ConfirmEvent) {
        if (event.isSuccess) {
            hideProgress()
            if(isRegister){
                startActivity(Intent(this, ProfileActivity::class.java))
            } else {
                startActivity(Intent(this, MainActivity::class.java))
            }

        } else {
            hideProgress()
            Toast.makeText(this, event.errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    @Subscribe
    public fun onEventUIThread(event: ResendEvent) {
        if (event.isSuccess) {
            hideProgress()
            Toast.makeText(this, "The code has been resent via a text message", Toast.LENGTH_SHORT).show()
        } else {
            hideProgress()
            Toast.makeText(this, event.errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupUI() {
        user?.let {
            ac_textview_instruction.text = String.format(getString(R.string.send_1, it.phone))
            val content = SpannableString(getString(R.string.resend_code))
            content.setSpan(UnderlineSpan(), 0, content.length, 0)
            ac_textview_resend_code.text = content
        }
    }

    private fun setupClickers() {
        ac_button_create_profile.setOnClickListener({
            showProgress()
            user?.let {
                it.phone?.let {
                    var phone = it
                    phone = phone.replace("[^0-9]".toRegex(), "")
                    val call = mCaller.verify(VerifyData(user!!.country_code!!, phone, ac_code_input.code.joinToString("")))
                    call.enqueue(object : Callback8<TokenResponse, ConfirmEvent>(mDataEventBus) {
                        override fun onSuccess(data: TokenResponse?) {
                            /*Cache token in Realm*/
                            data?.let {
                                val realmToken = Token()
                                realmToken.token = "Bearer " + it.token
                                /*Save token*/
                                realmToken.save()
                                /*Update user*/
                                it.user?.save()
                            }
                            /*Update User*/
                            mDataEventBus.post(ConfirmEvent())
                            hideProgress()
                            //Toast.makeText(context, "Confirmed " + data?.token, Toast.LENGTH_SHORT).show()
                        }
                    })

                }
            }
        })
        ac_textview_resend_code.setOnClickListener({
            showProgress()
            user?.let {
                if (!it.phone.isNullOrBlank() && !it.country_code.isNullOrBlank()) {
                    val call = mCaller.resend(LoginData(it.country_code!!, it.phone!!))
                    call.enqueue(object : Callback8<ResendResponse, ResendEvent>(mDataEventBus) {
                        override fun onSuccess(data: ResendResponse?) {
                            mDataEventBus.post(ResendEvent())
                        }
                    })
                }
            }
        })
    }
}