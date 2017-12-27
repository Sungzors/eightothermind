package com.phdlabs.sungwon.a8chat_android.db

import android.widget.Toast
import com.phdlabs.sungwon.a8chat_android.api.rest.Rest
import com.phdlabs.sungwon.a8chat_android.model.user.User
import com.phdlabs.sungwon.a8chat_android.model.user.registration.Token
import com.vicpin.krealmextensions.queryFirst
import com.vicpin.krealmextensions.save
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.Realm

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
     * Method for retrieving current user from cached data or pull if non existeng
     * @callback (success, User?)
     * */
    fun getCurrentUser(callback: (Boolean, User?, Token?) -> Unit) {

        val token: Token? = Token().queryFirst()
        token?.let {
            User().queryFirst()?.let {
                callback(true, it,token)
            } ?: run {
                /*No available user -> try to fetch from server*/
                val call = Rest.getInstance().getmCallerRx().getUser(it.token!!,user?.id!!)
                call.subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { response ->
                            if(response.isSuccess) {
                                /*Cache user in Realm*/
                                response.user.save()
                                callback(true, response.user, token)
                                print("UserManager: Success")
                            }else if (response.isError) {
                                /*Server call failed*/
                                callback(false, null, null)
                                print("UserManager: server call failed")
                            }
                        }
            }
        } ?: run {
            /*No available token*/
            callback(false, null, null)
            print("UserManager: No token, return user to Log In screen")
        }

//        var realm: Realm? = null
//        try {
//            realm = Realm.getDefaultInstance()
//            val currentUser: User? = User().queryFirst()
//            currentUser?.let {
//                callback(true, it,)
//            } ?: run {
//                val token = Token().queryFirst()
//                token?.token?.let {
//                    val call = Rest.getInstance().getmCallerRx().getUser(it, currentUser!!.id!!)
//                    call.subscribeOn(Schedulers.newThread())
//                            .observeOn(AndroidSchedulers.mainThread())
//                            .subscribe { response ->
//                                if (response.isSuccess) {
//                                    /*Cache user in realm*/
//                                    response.user.save()
//                                    print("User downloaded & cached in realm")
//                                    callback(true, response.user)
//                                } else if (response.isError) {
//                                    /*Server call failed*/
//                                    print("Server call failed for user in UserManager")
//                                    callback(false, null)
//                                }
//                            }
//
//                } ?: run {
//                    /*No available token*/
//                    print("NO available token in UserManager")
//                    callback(false, null)
//                }
//            }
//        } catch (e: Throwable) {
//            if (realm != null && realm.isInTransaction) {
//                realm.cancelTransaction()
//            }
//        } finally {
//            realm?.close()
//        }
    }


}