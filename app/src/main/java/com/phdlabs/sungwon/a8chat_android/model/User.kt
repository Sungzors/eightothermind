package com.phdlabs.sungwon.a8chat_android.model

import java.util.*

/**
 * Created by SungWon on 9/21/2017.
 */
data class User(val id: Int, var first_name: String, var last_name: String){

    var phone: String? = null
    var country_code: String? = null
    var email: String? = null
    var languages_spoken = mutableListOf<String>()
    var country: String? = null
    var profile_picture_string: String = ""
    var avatar: String = ""
    var position = mutableListOf<String>() //TODO: subject to change to location
    var verified: Boolean = true
    val passcode: String? = null
    val passcode_expiration: Date? = null
    var socket_id: String? = null
}