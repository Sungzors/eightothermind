package com.phdlabs.sungwon.a8chat_android.structure.main.lobby

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import com.phdlabs.sungwon.a8chat_android.api.event.Event
import com.phdlabs.sungwon.a8chat_android.api.response.ChatsRetrievalResponse
import com.phdlabs.sungwon.a8chat_android.api.rest.Caller
import com.phdlabs.sungwon.a8chat_android.api.rest.Rest
import com.phdlabs.sungwon.a8chat_android.api.utility.Callback8
import com.phdlabs.sungwon.a8chat_android.db.EventBusManager
import com.phdlabs.sungwon.a8chat_android.db.channels.ChannelsManager
import com.phdlabs.sungwon.a8chat_android.db.events.EventsManager
import com.phdlabs.sungwon.a8chat_android.db.user.UserManager
import com.phdlabs.sungwon.a8chat_android.model.channel.Channel
import com.phdlabs.sungwon.a8chat_android.model.room.Room
import com.phdlabs.sungwon.a8chat_android.structure.event.create.EventCreateActivity
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import com.vicpin.krealmextensions.saveAll
import org.greenrobot.eventbus.EventBus

/**
 * Created by SungWon on 10/17/2017.
 * Updated by JPAM on 1/31/2018
 */
class LobbyController(val mView: LobbyContract.View,
                      private var refresh: Boolean) : LobbyContract.Controller {

    /*Properties*/
    private var mMyChannel = mutableListOf<Channel>()
    private var mEvents = mutableListOf<Room>()
    private var mChannelsFollowed = mutableListOf<Channel>()
    private val mChannel = mutableListOf<Channel>()
    private val mChat = mutableListOf<Room>()

    private var mLocation: Pair<Double, Double> = Pair(0.0, 0.0)

    private val mCaller: Caller
    private val mEventBus: EventBus
    private var mLocationManager: LocationManager? = null

    init {
        mView.controller = this
        mCaller = Rest.getInstance().caller
        mEventBus = EventBusManager.instance().mDataEventBus
        mLocationManager = mView.getContext()?.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
    }

    override fun start() {
        mView.getContext()?.let {
            /*Location permissions*/
            if (ActivityCompat.checkSelfPermission(
                            it, Constants.AppPermissions.FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(
                            it, Constants.AppPermissions.COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
            ) {
                mView.hideProgress()
                requestLocationPermissions()
                return
            }
        }
        try {
            mLocationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener)
        } catch (ex: SecurityException) {
            println("No location available: " + ex.message)
        }
    }

    override fun resume() {
        callMyChannels(refresh)
        callChats()
    }

    override fun pause() {

    }

    override fun stop() {
    }

    /**
     * [locationListener] handles the current location update
     * once & then stops tracking location once the user leaves
     * the [EventCreateActivity] lifecycle
     * */
    private val locationListener: LocationListener = object : LocationListener {
        /*Current Location*/
        override fun onLocationChanged(location: Location) {

            if(mLocation.first - location.latitude > 0.001 || mLocation.second - location.longitude > 0.001){

                mLocation = Pair(location.latitude, location.longitude)
                if(mView != null){
                    callEvent(true)
                }

            }
        }

        /*Not necessary to handle on single location update*/
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}

        /*Not necessary to handle on single location update*/
        override fun onProviderEnabled(provider: String) {}

        /*Not necessary to handle on single location update*/
        override fun onProviderDisabled(provider: String) {}
    }

    /**
     * [requestLocationPermissions]
     * Requests FineLocation & Includes CoarseLocation for battery saving
     * if the first is not available
     * Callback is handled on Activity's[onRequestPermissionsResult]
     * */
    private fun requestLocationPermissions() {
        val whatPermissions = arrayOf(Constants.AppPermissions.FINE_LOCATION,
                Constants.AppPermissions.COARSE_LOCATION)
        mView.getContext()?.let {
            //Request Permissions
            if (ContextCompat.checkSelfPermission(it, whatPermissions.get(0)) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(it, whatPermissions.get(1)) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(mView.getActivityDirect(), whatPermissions, Constants.PermissionsReqCode.LOCATION_REQ_CODE)
            }
        }
    }

    private fun callMyChannels(refresh: Boolean) {
        mView.showProgress()
        ChannelsManager.instance.getUserChannels(null, refresh, { response ->
            response.second?.let {
                //Error
                mView.hideProgress()
                /*When no channels are available it triggers a localized error message not wanted*/
                //mView.showError(it)
                callFollow(true)
            } ?: run {
                mView.hideProgress()
                response.first?.let {
                    //Channels
                    mMyChannel.clear()
                    mMyChannel = it.toMutableList()
                    if (mMyChannel.size > 0) {
                        //UI
                        mView.setUpChannelRecycler(mMyChannel)
                        callFollow(true)
                    }else {
                        callFollow(true)
                    }
                }
            }
        })
    }

    //TODO: Refactor Events API
    private fun callEvent(refresh: Boolean) {
        mView.showProgress()
        EventsManager.instance.getEvents(refresh, mLocation.first, mLocation.second, { response ->
            response.second?.let {
                // Error
                mView.hideProgress()
                /*When no events are available it triggers a localized error message not wanted*/
                //mView.showError(it)
            } ?: run {
                mView.hideProgress()
                response.first?.let {
                    //Events
                    for (rooms in it){
                        if(rooms.isEventActive){
                            mEvents.add(rooms)
                        } else {
                            mChat.add(rooms)
                        }
                    }
                    if (mEvents.size > 0) {
                        //UI
                        mView.setUpEventsRecycler(mEvents)
                    }
                }
            }
        })
    }

    private fun callFollow(refresh: Boolean) {
        mView.setSeparatorCounter(mMyChannel.size - 1)
        mView.showProgress()
        ChannelsManager.instance.getMyFollowedChannelsWithFlags(refresh, { _, followedChannels, errorMessage ->
            errorMessage?.let {
                //Error
                mView.hideProgress()
                /*When no followed channels are available it triggers a localized error message not wanted*/
                //mView.showError(it)
            } ?: run {
                mView.hideProgress()
                mChannelsFollowed.clear()
                //Channels I Follow
                followedChannels?.let {
                    //Unread
                    it.first?.let {
                        mChannelsFollowed.addAll(it)
                    }
                    //Read
                    it.second?.let {
                        mChannelsFollowed.addAll(it)
                    }
                }
                //UI
                if (mChannelsFollowed.size > 0) {
                    mView.addFollowedChannels(mChannelsFollowed) //TODO: Sould be called after getting my channels
                }
            }
        })
    }

    private fun callChats() {
        UserManager.instance.getCurrentUser { success, user, token ->
            if (success) {
                mChat.clear()
                val call = mCaller.getAllChats(token?.token, user?.id!!)
                call.enqueue(object : Callback8<ChatsRetrievalResponse, Event>(mEventBus) {
                    override fun onSuccess(data: ChatsRetrievalResponse?) {
                        mChat.addAll(data!!.chats!!)
                        if (mChat.size > 0) {
                            mChat.saveAll()
                            mView.setUpChatRecycler(mChat)
                        }
                    }
                })
            }
        }

    }

    override fun setRefreshFlag(shouldRefresh: Boolean) {
        refresh = shouldRefresh
    }

    override fun getRefreshFlag(): Boolean = refresh

    override fun refreshAll() {
        callMyChannels(true)
        callEvent(true)
        callFollow(true)
        callChats()
    }

    override fun getMyChannels(): MutableList<Channel> = mMyChannel

    override fun getEvents(): MutableList<Room> = mEvents

    override fun getChannelsFollowed(): MutableList<Channel> = mChannelsFollowed

    override fun getChannel(): MutableList<Channel> = mChannel

    override fun getChat(): MutableList<Room> = mChat

}