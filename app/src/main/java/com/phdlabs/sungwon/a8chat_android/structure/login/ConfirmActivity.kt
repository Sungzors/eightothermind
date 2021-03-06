package com.phdlabs.sungwon.a8chat_android.structure.login

import android.app.Activity
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
import com.phdlabs.sungwon.a8chat_android.structure.myProfile.update.MyProfileUpdateActivity
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
//TODO: Remove EventBus & Switch to Reactive Kotlin

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.RequestCodes.TC_PP_REQUEST_CODE) {
                finish()
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }


    @Subscribe
    public fun onEventUIThread(event: ConfirmEvent) {
        if (event.isSuccess) {
            hideProgress()
            if (isRegister) {
                val intent = Intent(this, DisclosureActivity::class.java)
                setResult(Activity.RESULT_OK)
                startActivityForResult(intent, Constants.RequestCodes.TC_PP_REQUEST_CODE)
            } else {
                val intent = Intent(this, MainActivity::class.java)
                setResult(Activity.RESULT_OK)
                startActivity(intent)
                finish()
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
            if(!isRegister) ac_button_sign_in.text = getString(R.string.login_profile)
            //Button alpha state
            ac_code_input.isSelected = true
            ac_code_input.setOnClickListener { ac_button_sign_in.background = getDrawable(R.drawable.gradient_eightchatblue) }
        }
    }

    private fun setupClickers() {
        //TODO: This network call doesn't have error handling, if the wrong verification code is entered the progress doesn't stop.
        ac_button_sign_in.setOnClickListener({
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