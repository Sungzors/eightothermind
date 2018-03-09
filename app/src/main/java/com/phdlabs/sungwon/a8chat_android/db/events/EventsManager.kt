package com.phdlabs.sungwon.a8chat_android.db.events

import com.phdlabs.sungwon.a8chat_android.api.event.Event
import com.phdlabs.sungwon.a8chat_android.api.rest.Rest
import com.phdlabs.sungwon.a8chat_android.db.user.UserManager
import com.phdlabs.sungwon.a8chat_android.model.event.EventsEight
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import com.vicpin.krealmextensions.query
import com.vicpin.krealmextensions.saveAll
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by paix on 2/27/18.
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

    fun getEvents(refresh: Boolean, callback: (Pair<List<EventsEight>?, String?>) -> Unit) {
        UserManager.instance.getCurrentUser { success, user, token ->
            if (success) {
                user?.let {
                    if (refresh) { //API Query & Caching
                        token?.token?.let {
                            val call = Rest.getInstance().getmCallerRx().getUserEventsWithFlags(it, user.id!!)
                            call.subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({ response ->
                                        if (response.isSuccess) {
                                            //Save to realm
                                            response.events?.saveAll()
                                            //Return events with Flag
                                            callback(Pair(response.events?.toList(), null))
                                        } else if (response.isError) {
                                            callback(Pair(null, "could not download events"))
                                        }
                                    }, { throwable ->
                                        callback(Pair(null, throwable.localizedMessage))
                                    })
                        }
                    } else { //Local Query
                        //Realm Query
                        val allEventsEight: ArrayList<EventsEight> = arrayListOf()
                        //Query For all events
                        val createdEvents = EventsEight().query {
                            equalTo("association",
                                    Constants.EventAssociation.EVENT_CREATED)
                        }
                        allEventsEight += createdEvents
                        val fullAccessEvents = EventsEight().query {
                            equalTo("association",
                                    Constants.EventAssociation.EVENT_FULL_PARTICIPANT)
                        }
                        allEventsEight += fullAccessEvents
                        val readOnly = EventsEight().query {
                            equalTo("association",
                                    Constants.EventAssociation.EVENT_READ_ONLY)
                        }
                        allEventsEight += readOnly
                        //Callback
                        if (allEventsEight.count() > 0) {
                            callback(Pair(allEventsEight, null))
                        } else {
                            callback(Pair(null, "did not find events"))
                        }
                    }
                }
            }
        }
    }

}