package com.phdlabs.sungwon.a8chat_android.db

import com.phdlabs.sungwon.a8chat_android.model.User

/**
 * Created by SungWon on 9/27/2017.
 */
class UserManager{

    //should contain calls involving user

    companion object {
        fun instance(): UserManager = UserManager()
    }

    var user: User? = null


}