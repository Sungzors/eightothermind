package com.phdlabs.sungwon.a8chat_android.structure.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import com.phdlabs.sungwon.a8chat_android.structure.myProfile.update.MyProfileUpdateActivity
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import com.transitionseverywhere.TransitionManager
import com.transitionseverywhere.extra.Scale
import kotlinx.android.synthetic.main.activity_terms_conditions.*


/**
 * Created by JPAM on 4/17/18.
 * Privacy Policy
 * Terms & Conditions
 */
class DisclosureActivity : CoreActivity() {

    /*Layout*/
    override fun layoutId(): Int = R.layout.activity_terms_conditions

    override fun contentContainerId(): Int = 0

    /*Properties*/
    private var mWebView: WebView? = null

    /*LifeCycle*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupClickers()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //Profile updated
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.RequestCodes.TC_PP_REQUEST_CODE) {
            setResult(Activity.RESULT_OK)
            finish()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            when (keyCode) {
                KeyEvent.KEYCODE_BACK -> {
                    mWebView?.let {
                        it.goBack()
                        showWebView(false)
                    }
                    return true
                }
            }

        }
        return super.onKeyDown(keyCode, event)
    }

    /*UI*/
    private fun setupClickers() {
        //Web View
        mWebView = findById(R.id.atc_web_view)
        //Link to Terms & Conditions
        atc_terms_conditions.setOnClickListener {
            showWebView(true)
            mWebView?.loadUrl(Constants.Websites.NAKED_APPS)
            mWebView?.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    view?.loadUrl(Constants.Websites.NAKED_APPS)
                    return true
                }
            }
        }
        //Link to Privacy Policy
        atc_privacy_policy.setOnClickListener {
            showWebView(true)
            mWebView?.loadUrl(Constants.Websites.NAKED_APPS)
            mWebView?.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    view?.loadUrl(Constants.Websites.NAKED_APPS)
                    return true
                }
            }
        }

        //Accept Terms & Conditions
        atc_switch_accept_tc.setOnCheckedChangeListener { compoundButton, isChecked ->
            hideNextButton(isChecked)
        }
        //Accept Privacy Policy
        atc_switch_accept_pp.setOnCheckedChangeListener { compoundButton, isChecked ->
            hideNextButton(isChecked)
        }
        //Next -> Update Profile
        atc_button_next.setOnClickListener {
            val intent = Intent(this, MyProfileUpdateActivity::class.java)
            setResult(Activity.RESULT_OK)
            startActivityForResult(intent, Constants.RequestCodes.MY_PROFILE_UPDATE_REGISTER)
        }
    }

    private fun hideNextButton(shouldHide: Boolean) {
        TransitionManager.beginDelayedTransition(atc_animation_container, Scale())
        atc_button_next.visibility = (if (shouldHide) View.VISIBLE else View.GONE)
    }

    private fun showWebView(shouldShow: Boolean) {
        mWebView?.visibility = (if (shouldShow) View.VISIBLE else View.GONE)
        atc_disclosure_container.visibility = (if (!shouldShow) View.VISIBLE else View.GONE)
        atc_disclosure_links_container.visibility = (if (!shouldShow) View.VISIBLE else View.GONE)
        atc_accept_tc_container.visibility = (if (!shouldShow) View.VISIBLE else View.GONE)
        atc_divider.visibility = (if (!shouldShow) View.VISIBLE else View.GONE)
        atc_accept_pp_container.visibility = (if (!shouldShow) View.VISIBLE else View.GONE)
        atc_animation_container.visibility = (if (!shouldShow) View.VISIBLE else View.GONE)
    }

}