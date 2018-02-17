package com.phdlabs.sungwon.a8chat_android.structure.myProfile.detail

import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.view.View
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.db.UserManager
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreFragment
import com.phdlabs.sungwon.a8chat_android.utility.camera.CircleTransform
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_my_profile.*
import java.util.*

/**
 * Created by paix on 2/16/18.
 * [MyProfileFragment]
 * managed from MainActivity Tab Bar
 */
class MyProfileFragment : CoreFragment() {

    /*Layout*/
    override fun layoutId(): Int = R.layout.fragment_my_profile

    /*Companion*/
    companion object {
        fun newInstance() = MyProfileFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //User information
        displayUserInfo()
        //Set Clickers
        setClickers()

    }

    /*User Info -> Top card*/
    private fun displayUserInfo() {
        //Display cached info
        UserManager.instance.getCurrentUser { success, user, _ ->
            if (success) {
                user?.let {
                    //Display Profile picture
                    Picasso.with(context)
                            .load(it.avatar)
                            .resize(70, 70)
                            .onlyScaleDown()
                            .centerCrop()
                            .transform(CircleTransform())
                            .into(fmp_picture)
                    //Display Info
                    val fullName:Pair<Boolean,String?> = it.hasFullName()
                    if (fullName.first){
                       fmp_name.text = fullName.second
                    }else {
                        fmp_name.text = it.first_name?: "n/a"
                    }
                    fmp_phone_number.text = PhoneNumberUtils.formatNumber(it.phone, Locale.getDefault().country)?:"n/a"
                    fmp_language.text = "Language: " + it.languages_spoken?.get(0)?.stringValue?: "n/a"
                }
            }
        }
    }

    /*Clickable UI*/
    private fun setClickers() {

    }

}