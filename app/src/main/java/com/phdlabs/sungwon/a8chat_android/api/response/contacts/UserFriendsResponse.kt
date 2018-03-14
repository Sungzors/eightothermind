package com.phdlabs.sungwon.a8chat_android.api.response.contacts

import com.phdlabs.sungwon.a8chat_android.api.response.ErrorResponse
import com.phdlabs.sungwon.a8chat_android.model.contacts.Contact

/**
 * Created by JPAM on 2/14/18.
 * [UserFriendsResponse] if successful should return
 * an array of Friends that will be stored as contacts in Realm
 * - Only those affiliated in Eight will be available.
 */
class UserFriendsResponse : ErrorResponse() {

     var users: Array<Contact>? = null

}