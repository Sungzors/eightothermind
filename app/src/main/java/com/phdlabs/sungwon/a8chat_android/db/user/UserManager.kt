package com.phdlabs.sungwon.a8chat_android.db.user

import com.phdlabs.sungwon.a8chat_android.api.data.notifications.UserFBToken
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

    /**
     * [getSpecificUserInfo]
     * Retrieve information for a specific user & Map it to a User Object
     * @return User @see Realm , ErrorMessage
     * */
    fun getSpecificUserInfo(userId: Int, callback: (User?, String?) -> Unit) {
        //Current User
        getCurrentUser { success, user, token ->
            token?.token?.let {
                val call = Rest.getInstance().getmCallerRx().getUser(it, userId)
                call.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ response ->
                            if (response.isSuccess) {
                                response?.user?.let {
                                    callback(it, null)
                                }
                            } else if (response.isError) {
                                callback(null, "Did not find user")
                            }
                        }, { throwable ->
                            callback(null, throwable.localizedMessage)
                        })
            }
        }
    }

    /**
     * [updateFirebaseToken]
     * Update firebase token called by [EightFirebaseInstanceIdService]
     * - Called if InstanceID token is updated or compromised
     * */
    fun updateFirebaseToken(firebaseToken: String) {
        getCurrentUser { success, user, token ->
            if (success) {
                user?.let {
                    token?.token?.let {
                        val call = Rest.getInstance().getmCallerRx().updateFBToken(it, user.id!!, UserFBToken(firebaseToken))
                        call.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({ response ->
                                    if (response.isSuccess) {
                                        response.user?.let {
                                            it.save()
                                        }
                                    } else if (response.isError) {
                                        //Ignore
                                        //println("Error updating Firebase Token")
                                        //TODO: Setup Schedulers to ask for token again
                                    }
                                }, {
                                    //Ignore
                                    //println(it.localizedMessage)
                                    //TODO: Setup Schedulers to ask for token again
                                })
                    }
                }
            }
        }
    }

}