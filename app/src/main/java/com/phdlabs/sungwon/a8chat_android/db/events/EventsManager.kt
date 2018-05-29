package com.phdlabs.sungwon.a8chat_android.db.events

import com.phdlabs.sungwon.a8chat_android.api.event.Event
import com.phdlabs.sungwon.a8chat_android.api.rest.Rest
import com.phdlabs.sungwon.a8chat_android.db.user.UserManager
import com.phdlabs.sungwon.a8chat_android.model.event.EventsEight
import com.phdlabs.sungwon.a8chat_android.model.room.Room
import com.vicpin.krealmextensions.queryAll
import com.vicpin.krealmextensions.save
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * Created by JPAM on 2/27/18.
 * [EventsManager]
 * Used for downloading & Caching [Event] & [Room]
 */
class EventsManager {

    /*Singleton*/
    private object Holder {
        val instance: EventsManager = EventsManager()
    }

    /*Companion*/
    companion object {
        val instance: EventsManager by lazy { Holder.instance }
    }

    /*Properties*/
    private lateinit var mUserManager: UserManager

    init {
        mUserManager = UserManager.instance
    }

    /**
     * [disposables]
     * List of disposables used for memory management during user navigation
     * */
    val disposables: MutableList<Disposable> = mutableListOf()

    /**
     * [clearDisposables]
     * Release API RX Call resources for memory management
     * */
    fun clearDisposables() {
        for (disposable in disposables) {
            if (!disposable.isDisposed) {
                disposable.dispose()
            }
        }
        disposables.clear()
    }

    fun getAllEvents(refresh: Boolean, lat: Double, lng: Double, callback: (Pair<List<EventsEight>?, String?>) -> Unit) {

        if (refresh) {
            mUserManager.getCurrentUser({ success, user, token ->
                if (success) {
                    user?.let {
                        token?.token?.let {
                            val call = Rest.getInstance().getmCallerRx().getEvents(it)
                            disposables.add(call
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({ response ->
                                        if (response.isSuccess) {
                                            response?.events?.let { events ->
                                                for (event in events) {
                                                    //Fetch events in range && my events
                                                    if (isEventNearby(lat, lng, event) || event.user_creator_id == user.id) {
                                                        event.save()
                                                    }
                                                }
                                                callback(Pair(queryMyEvents(), null))
                                            }
                                        } else if (response.isError) {
                                            callback(Pair(null, "no events"))
                                        }
                                    }, { throwable ->
                                        callback(Pair(null, throwable.localizedMessage))
                                    })
                            )
                        }
                    }
                }
            })
        } else {
            callback(Pair(queryMyEvents(), null))
        }
    }

    private fun isEventNearby(lat: Double, lng: Double, event: EventsEight): Boolean {
        val earthRadius = 6371
        var eventLat = lat
        var eventLng = lng

        event.active?.let {
            if (it) {

                //Get Event location
                event.location?.coordinates?.let {
                    /**
                     * Realm list stores first latitude & then longitude, placing them
                     * backwards in the array.
                     * @param latitude array[1]
                     * @param longitude array[0]
                     * */
                    it[1]?.doubleValue?.let {
                        eventLat = it
                    }
                    it[0]?.doubleValue?.let {
                        eventLng = it
                    }
                }

                /**
                 * Law of cosines:	d = acos( sin φ1 ⋅ sin φ2 + cos φ1 ⋅ cos φ2 ⋅ cos Δλ ) ⋅ R
                 * */
                return if (lat != eventLat && lng != eventLng) {
                    val a = ((Math.acos(
                            (Math.sin(lat) * Math.sin(eventLat)) + (Math.cos(lat) * Math.cos(eventLat) * Math.cos(eventLng - lng)))) * earthRadius)
                    if (a > 0 && a < 300) { //300 mts for event range
                        println("METERS AWAY FROM EVENT: $a")
                        true
                    } else {
                        false
                    }
                } else {
                    false
                }
            } else {
                return false
            }
        }
        return false
    }

    fun queryMyEvents(): List<EventsEight>? = EventsEight().queryAll()

}