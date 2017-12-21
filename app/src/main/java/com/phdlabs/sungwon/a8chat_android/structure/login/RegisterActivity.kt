package com.phdlabs.sungwon.a8chat_android.structure.login

import android.content.Intent
import android.telephony.PhoneNumberFormattingTextWatcher
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.api.data.LoginData
import com.phdlabs.sungwon.a8chat_android.api.event.LoginEvent
import com.phdlabs.sungwon.a8chat_android.api.response.UserDataResponse
import com.phdlabs.sungwon.a8chat_android.api.rest.Rest
import com.phdlabs.sungwon.a8chat_android.api.utility.Callback8
import com.phdlabs.sungwon.a8chat_android.db.EventBusManager
import com.phdlabs.sungwon.a8chat_android.db.UserManager
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import com.phdlabs.sungwon.a8chat_android.utility.Preferences
import kotlinx.android.synthetic.main.activity_register.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

/**
 * Created by SungWon on 9/24/2017.
 * Updated by JPAM on 12/18/2017
 */
class RegisterActivity : CoreActivity(){

    /*Properties*/
    private var isRegister: Boolean = true

    //TODO: Remove Event Bus
    val mDataEventBus: EventBus = EventBusManager.instance().mDataEventBus

    /*Layout*/
    override fun layoutId() = R.layout.activity_register
    override fun contentContainerId() = 0

    /*LifeCycle*/
    override fun onStart() {
        super.onStart()

        //TODO: Remove EventBus
        mDataEventBus.register(this)

        if (intent.getStringExtra(Constants.IntentKeys.LOGIN_KEY) != "register"){
            isRegister = false
        }
        setToolbarTitle("Phone Number")
        ar_phone.addTextChangedListener(PhoneNumberFormattingTextWatcher())
        setOnClickers()
    }

    override fun onStop() {
        super.onStop()

        //TODO: Remove event bus
        mDataEventBus.unregister(this)
    }

    //TODO: Remove Event Bus subscriptions
    @Subscribe
    public fun onEventUIThread(event: LoginEvent){
        if(event.isSuccess){
            hideProgress()

            val intent = Intent(this, ConfirmActivity::class.java)
            intent.putExtra(Constants.IntentKeys.LOGIN_KEY, isRegister)
            intent.putExtra(Constants.IntentKeys.LOGIN_CC, ar_ccp.selectedCountryCodeWithPlus)
            intent.putExtra(Constants.IntentKeys.LOGIN_PHONE, ar_phone.text.toString())
            startActivity(intent)

        } else {
            showError(event.errorMessage)
        }
    }

    private fun setOnClickers(){
        ar_confirm_button.setOnClickListener({
            showProgress()

            var phone = ar_phone.text.toString()
            phone = phone.replace("[^0-9]".toRegex(),"")

            //TODO: Check if user has registered on realm


            val call = Rest.getInstance().caller.login(LoginData(ar_ccp.selectedCountryCodeWithPlus, phone))
            call.enqueue(object: Callback8<UserDataResponse, LoginEvent>(mDataEventBus){

                override fun onSuccess(data: UserDataResponse?) {
                    /*Local DB*/

                    Preferences(context).putPreference(Constants.PrefKeys.USER_ID, data!!.user.id)
                    UserManager.instance.user = data.user
                    mDataEventBus.post(LoginEvent())
                }

            })
        })
    }
}