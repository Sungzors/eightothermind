package com.phdlabs.sungwon.a8chat_android.structure.login

import android.widget.Toast
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.api.data.LoginData
import com.phdlabs.sungwon.a8chat_android.api.event.LoginEvent
import com.phdlabs.sungwon.a8chat_android.api.response.UserDataResponse
import com.phdlabs.sungwon.a8chat_android.api.rest.Rest
import com.phdlabs.sungwon.a8chat_android.api.utility.Callback8
import com.phdlabs.sungwon.a8chat_android.db.EventBusManager
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import kotlinx.android.synthetic.main.activity_register.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

/**
 * Created by SungWon on 9/24/2017.
 */
class RegisterActivity : CoreActivity(){

    var isRegister: Boolean = true
    val mDataEventBus: EventBus = EventBusManager.instance().mDataEventBus
    override fun layoutId() = R.layout.activity_register

    override fun contentContainerId() = 0

    override fun onStart() {
        super.onStart()
        mDataEventBus.register(this)
        val intent = getIntent()
        if (intent.getStringExtra(Constants.LOGIN_KEY) != "register"){
            isRegister = false
        }
        setOnClickers()
    }

    override fun onStop() {
        super.onStop()
        mDataEventBus.unregister(this)
    }

    @Subscribe
    public fun onEventUIThread(event: LoginEvent){
        if(event.isSuccess){
            //TODO: do confirmation
            Toast.makeText(this, "stuff done", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setOnClickers(){
        ar_confirm_button.setOnClickListener({
            val call = Rest.getInstance().caller.login(LoginData(ar_ccp.selectedCountryCodeWithPlus, ar_phone.text.toString()))
            call.enqueue(object: Callback8<UserDataResponse, LoginEvent>(mDataEventBus){
                override fun onSuccess(data: UserDataResponse?) {
                    mDataEventBus.post(LoginEvent())
                }
            })
        })
    }
}