package com.phdlabs.sungwon.a8chat_android.api.data

import com.phdlabs.sungwon.a8chat_android.model.contacts.LocalContact

/**
 * Created by paix on 2/14/18.
 * [ContactsPostData] used to post local contact phone number
 * & validate with current Eight users through API
 */
data class ContactsPostData(val contactsArray: Array<out Any>)