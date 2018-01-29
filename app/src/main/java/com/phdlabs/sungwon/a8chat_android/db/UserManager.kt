package com.phdlabs.sungwon.a8chat_android.db

import com.phdlabs.sungwon.a8chat_android.api.rest.Rest
import com.phdlabs.sungwon.a8chat_android.model.user.User
import com.phdlabs.sungwon.a8chat_android.model.user.registration.Token
import com.vicpin.krealmextensions.queryFirst
import com.vicpin.krealmextensions.save
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by SungWon on 9/27/2017.
 * Updated by JPAM on 12/27/2017
 */
class UserManager {

    /*Singleton*/
    companion object {
        val instance: UserManager = UserManager()
    }

    /*Properties*/
    var user: User? = null

    /**[getCurrentUser]
     * Method for retrieving current user from cached data or pull if non-existent
     * @callback (success, User?, Token?)
     * */
    fun getCurrentUser(callback: (Boolean, User?, Token?) -> Unit) {

        val token: Token? = Token().queryFirst()
        token?.let {
            User().queryFirst()?.let {
                callback(true, it, token)
            } ?: run {
                /*No available user -> try to fetch from server*/
                val call = Rest.getInstance().getmCallerRx().getUser(it.token!!, user?.id!!)
                call.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ response ->
                            if (response.isSuccess) {
                                /*Cache user in Realm*/
                                response.user.save()
                                callback(true, response.user, token)
                                print("UserManager: Success")
                            } else if (response.isError) {
                                /*Server call failed*/
                                callback(false, null, null)
                                print("UserManager: server call failed")
                            }
                        }, { throwable ->
                            println("Error downloading user info in UserManager: " + throwable.message)
                        })
            }
        } ?: run {
            /*No available token*/
            callback(false, null, null)
            println("UserManager: No token, return user to Log In screen")
        }
    }

}