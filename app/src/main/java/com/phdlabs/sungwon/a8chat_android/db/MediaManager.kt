package com.phdlabs.sungwon.a8chat_android.db

import com.phdlabs.sungwon.a8chat_android.api.rest.Rest
import com.phdlabs.sungwon.a8chat_android.db.user.UserManager
import com.phdlabs.sungwon.a8chat_android.model.media.Media
import com.vicpin.krealmextensions.saveAll
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by paix on 2/23/18.
 * [MediaManager]
 * Data Download & Upload between Eight API
 * @Realm for Data Caching
 */
class MediaManager {

    /*Singleton*/
    private object Holder {
        val instance: MediaManager = MediaManager()
    }

    companion object {
        val instance: MediaManager by lazy { Holder.instance }
    }

    /**
     * [getPrivateMedia]
     * Get shared media with a specific user
     * @param userId2 Id of the other user I share files with
     * @return Pari<Array<Media>, String>> (Media & Error response message)
     * @see Realm for cache
     * */
    fun getPrivateMedia(userId2: Int, callback: (Pair<Array<Media>?, String?>) -> Unit) {
        //Get user
        var collectedMedia: Array<Media>
        var errorResponse: String?
        UserManager.instance.getCurrentUser { isSuccess, user, token ->
            if (isSuccess) {
                token?.token?.let {
                    user?.id.let {
                        val call = Rest.getInstance().getmCallerRx().getSharedMediaPrivate(
                                token.token!!,
                                it!!,
                                userId2)
                        call.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({ response ->
                                    if (response.isSuccess) {
                                        //Shared Media
                                        response?.sharedMedia?.let {
                                            //Set private flag
                                            for (element in it) {
                                                element.sharedWithUserId = userId2
                                            }
                                            //Cache
                                            it.saveAll()
                                            collectedMedia = it
                                            callback(Pair(collectedMedia, null))
                                        }
                                    } else if (response.isError) {
                                        errorResponse = "No media"
                                        println("MEDIA MANAGER: No media found between users")
                                        callback(Pair(null, errorResponse))
                                    }
                                }, { throwable ->
                                    errorResponse = "Could not download media"
                                    println("MEDIA MANAGER: Error downloading Private Media")
                                    println("MEDIA MANAGER: " + throwable.localizedMessage)
                                    callback(Pair(null, errorResponse))
                                })
                    }
                }
            }
        }
    }

}