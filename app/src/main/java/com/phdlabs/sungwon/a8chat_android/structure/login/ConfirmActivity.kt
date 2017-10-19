package com.phdlabs.sungwon.a8chat_android.structure.login

import android.content.Intent
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
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import com.phdlabs.sungwon.a8chat_android.structure.profile.ProfileActivity
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import com.phdlabs.sungwon.a8chat_android.utility.Preferences
import kotlinx.android.synthetic.main.activity_confirm.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

/**
 * Created by SungWon on 9/26/2017.
 */
class ConfirmActivity: CoreActivity(){

    val mDataEventBus: EventBus = EventBusManager.instance().mDataEventBus
    val mCaller: Caller = Rest.getInstance().caller

    private var isRegister: Boolean = true
    private lateinit var mCountryCode: String
    private lateinit var mPhone: String

    override fun layoutId() = R.layout.activity_confirm

    override fun contentContainerId() = 0

    override fun onStart() {
        super.onStart()
        mDataEventBus.register(this)
        isRegister = intent.getBooleanExtra(Constants.IntentKeys.LOGIN_KEY, true)
        mCountryCode = intent.getStringExtra(Constants.IntentKeys.LOGIN_CC)
        mPhone = intent.getStringExtra(Constants.IntentKeys.LOGIN_PHONE)

        setupUI()
        setupClickers()
    }

    override fun onStop() {
        super.onStop()
        mDataEventBus.unregister(this)
    }



    @Subscribe
    public fun onEventUIThread(event: ConfirmEvent){
        if(event.isSuccess){
            hideProgress()
            startActivity(Intent(this, ProfileActivity::class.java))
        } else {
            hideProgress()
            Toast.makeText(this, event.errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    @Subscribe
    public fun onEventUIThread(event: ResendEvent){
        if(event.isSuccess){
            hideProgress()
            Toast.makeText(this, "The code has been resent via a text message", Toast.LENGTH_SHORT).show()
        } else {
            hideProgress()
            Toast.makeText(this, event.errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupUI(){
        ac_textview_instruction.text = String.format(getString(R.string.send_1, mPhone))
    }

    private fun setupClickers(){
        ac_button_create_profile.setOnClickListener({
            showProgress()
            var phone = mPhone
            phone = phone.replace("[^0-9]".toRegex(),"")
            val call = mCaller.verify(VerifyData(mCountryCode, phone, ac_code_input.code.joinToString("")))
            call.enqueue(object: Callback8<TokenResponse, ConfirmEvent>(mDataEventBus){
                override fun onSuccess(data: TokenResponse?) {
                    Preferences(context).putPreference(Constants.PrefKeys.TOKEN_KEY, "Bearer " + data?.token)
                    mDataEventBus.post(ConfirmEvent())
                    hideProgress()
                    Toast.makeText(context, "Confirmed " + data?.token, Toast.LENGTH_SHORT).show()
                }
            })
        })
        ac_textview_resend_code.setOnClickListener({
            showProgress()
            val call = mCaller.resend(LoginData(mCountryCode, mPhone))
            call.enqueue(object: Callback8<ResendResponse,ResendEvent>(mDataEventBus){
                override fun onSuccess(data: ResendResponse?) {
                    mDataEventBus.post(ResendEvent())
                }
            })
        })
    }
}