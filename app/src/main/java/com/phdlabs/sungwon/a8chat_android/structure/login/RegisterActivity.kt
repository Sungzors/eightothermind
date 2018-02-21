package com.phdlabs.sungwon.a8chat_android.structure.login

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.telephony.PhoneNumberFormattingTextWatcher
import android.view.WindowManager
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.api.rest.Rest
import com.phdlabs.sungwon.a8chat_android.model.user.registration.RegistrationData
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import com.vicpin.krealmextensions.queryFirst
import com.vicpin.krealmextensions.save
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_register.*

/**
 * Created by SungWon on 9/24/2017.
 * Updated by JPAM on 12/18/2017 (Error Handling & Cache)
 */
class RegisterActivity : CoreActivity() {

    /*Properties*/
    private var isRegister: Boolean = true

    /*Layout*/
    override fun layoutId() = R.layout.activity_register

    override fun contentContainerId() = 0

    /*LifeCycle*/
    override fun onStart() {
        super.onStart()
        if (intent.getStringExtra(Constants.IntentKeys.LOGIN_KEY) != "register") {
            isRegister = false
        }
        setToolbarTitle("Phone Number")
        ar_phone.addTextChangedListener(PhoneNumberFormattingTextWatcher())
        setOnClickers()
    }

    private fun confirmation() {
        hideProgress()
        val intent = Intent(this, ConfirmActivity::class.java)
        intent.putExtra(Constants.IntentKeys.LOGIN_KEY, isRegister)
        startActivity(intent)
    }

    private fun setOnClickers() {
        ar_confirm_button.setOnClickListener({
            showProgress()

            /*Phone Input & Data Validation*/
            var phone: String? = ar_phone.text.toString()
            if (!phone.isNullOrEmpty()) {
                phone = phone?.replace("[^0-9]".toRegex(), "")
                /*Query Cached registered users on this phone*/
                var realm: Realm? = null
                try {
                    realm = Realm.getDefaultInstance()
                    val realmRegistrationData: RegistrationData? = RegistrationData().queryFirst { query ->
                        query.equalTo("phone", phone)
                    }
                    realmRegistrationData?.let {
                        /*Already registered*/
                        confirmation()
                    } ?: run {
                        /*Not registered*/
                        val registrationData = RegistrationData()
                        registrationData.phone = phone
                        registrationData.country_code = ar_ccp.selectedCountryCodeWithPlus
                        /*Network Call*/
                        val call = Rest.getInstance().getmCallerRx().login(registrationData)
                        call.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                        { response ->
                                            if (response.isSuccess) {
                                                /*Save to Realm*/
                                                registrationData.save()
                                                response.user.save()
                                                /*Transition to Confirm Activity*/
                                                confirmation()
                                            } else if (response.isError) {
                                                hideProgress()
                                                showError(response.message)
                                            }
                                        }, { throwable ->
                                    hideProgress()
                                    if (isRegister) {
                                        showError("Could not create account, try again later")
                                    } else {
                                        showError("Could not sign in, try again later")
                                    }
                                    println("Error in Log in: " + throwable.message)
                                })
                    }
                } catch (e: Throwable) {
                    /*Do not leave realm transactions open*/
                    hideProgress()
                    if (realm != null && realm.isInTransaction) {
                        realm.cancelTransaction()
                    }
                    //Stack trace
                    print("REALM ERROR: " + e.stackTrace)

                } finally {
                    /*Close realm INSTANCE*/
                    realm?.close()
                }
            } else {
                /*Error*/
                hideProgress()
                showError("8 needs a phone number for registration")
            }
        })
    }
}