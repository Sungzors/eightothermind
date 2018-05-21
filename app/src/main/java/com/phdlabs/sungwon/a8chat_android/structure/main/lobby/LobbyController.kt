package com.phdlabs.sungwon.a8chat_android.structure.main.lobby

import android.widget.Toast
import com.phdlabs.sungwon.a8chat_android.api.rest.Caller
import com.phdlabs.sungwon.a8chat_android.api.rest.Rest
import com.phdlabs.sungwon.a8chat_android.db.channels.ChannelsManager
import com.phdlabs.sungwon.a8chat_android.db.room.RoomManager
import com.phdlabs.sungwon.a8chat_android.db.user.UserManager
import com.phdlabs.sungwon.a8chat_android.model.channel.Channel
import com.phdlabs.sungwon.a8chat_android.model.room.Room
import com.phdlabs.sungwon.a8chat_android.model.user.User
import com.phdlabs.sungwon.a8chat_android.model.user.registration.Token
import com.phdlabs.sungwon.a8chat_android.structure.main.LobbyContract

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

    //User
    private var mUser: User? = null
    private var mToken: Token? = null
    private var mUserManager: UserManager

    //API
    private var mChannelManager: ChannelsManager
    private var mRoomManager: RoomManager
    private val mCaller: Caller

    init {
        //Controller
        mView.controller = this
        //User
        mUserManager = UserManager.instance
        mUserManager.getCurrentUser { success, user, token ->
            if (success) {
                mUser = user
                mToken = token
            }
        }
        //API
        mRoomManager = RoomManager.instance
        mCaller = Rest.getInstance().caller
        mChannelManager = ChannelsManager.instance
    }

    override fun onViewCreated() {
        mView.setUpChannelRecycler()
        mView.setUpEventsRecycler()
        mView.setUpChatRecycler()
    }

    override fun start() {
    }

    override fun resume() {
        callMyChannels(refresh)
        callChats(refresh)

    }

    override fun pause() {
    }

    override fun stop() {
        //TODO: Dispose network resources
        mChannelManager.clearDisposables()
        mUserManager.clearDisposables()
        mRoomManager.clearDisposables()
    }

    /**
     * [callMyChannels]
     * Retrieve cached channels or refresh from API depending on user navigation
     * */
    private fun callMyChannels(refresh: Boolean) {
        //mView.showProgress()
        mChannelManager.getUserChannels(null, refresh, { response ->
            response.second?.let {
                //Error
                //Retrieve Followed Channels
                getFollowedChannels(true)
            } ?: run {
                //mView.hideProgress()
                response.first?.let {
                    //Channels
                    mMyChannel.clear()
                    mMyChannel = it.toMutableList()
                    if (mMyChannel.size > 0) {
                        //UI
                        mView.refreshMyChannels()
                        getFollowedChannels(true)
                    } else {
                        getFollowedChannels(true)
                    }
                }
            }
        })
    }

    //FIXME: Review Event call
    private fun callEvent(refresh: Boolean) {
        //mView.showProgress() //FIXME: Crashes on fast changes through tabs
//        EventsManager.instance.getEvents(refresh, mLocation.first, mLocation.second, { response ->
//            response.second?.let {
//                // Error
//                //mView.hideProgress()
//                /*When no events are available it triggers a localized error message not wanted*/
//                //mView.showError(it)
//            } ?: run {
//                //mView.hideProgress()
//                response.first?.let {
//                    //Events
//                    var chatCount = 0
//                    for (rooms in it) {
//                        if (rooms.isEventActive) {
//                            mEvents.add(rooms)
//                        } else {
//                            if (!mChat.contains(rooms)) {
//                                mChat.add(rooms)
//                            }
//                            chatCount++
//                        }
//                    }
//                    if (mEvents.size > 0) {
//                        //UI
//                        mView.setUpEventsRecycler(mEvents)
//                    }
//                    if (chatCount > 0) {
//                        mView.refreshChat()
//                    }
//                }
//            }
//        })
    }

    /**
     * [getFollowedChannels]
     * Retrieve cached followed channels or refresh from API
     * */
    private fun getFollowedChannels(refresh: Boolean) {
        //Channel separator
        mView.setSeparatorCounter(mMyChannel.size - 1)
        mChannelManager.getMyFollowedChannelsWithFlags(refresh, { _, followedChannels, errorMessage ->
            errorMessage?.let {
                //Error
                Toast.makeText(mView.getContext(), "Start following channels", Toast.LENGTH_SHORT).show()
            } ?: run {
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
                    mView.refreshFollowedChannels()
                }
            }
        })
    }

    //TODO: Switch chat calls to RX
    private fun callChats(refresh: Boolean) {
        mRoomManager.getPrivateAndGroupChats(refresh) {
            it.second?.let {
                //Error
                Toast.makeText(mView.getContext(), "Start a private chat", Toast.LENGTH_SHORT).show()
            } ?: run {
                it.first?.let {
                    if (it.count() > 0) {
                        mChat.clear()
                        mChat.addAll(it)
                        mView.refreshChat()
                    }

                }
            }
        }
    }

    //FIXME: Review all refreshing actions

    override fun setRefreshFlag(shouldRefresh: Boolean) {
        refresh = shouldRefresh
    }

    override fun getRefreshFlag(): Boolean = refresh

    //FIXME: Not used -> Maybe a swipe refresh view
    override fun refreshAll() {
        callMyChannels(true)
        callEvent(true)
        getFollowedChannels(true)
        callChats(true)
    }

    //TODO: Review getters
    override fun getMyChannels(): MutableList<Channel> = mMyChannel

    override fun getEvents(): MutableList<Room> = mEvents

    override fun getChannelsFollowed(): MutableList<Channel> = mChannelsFollowed

    override fun getChannel(): MutableList<Channel> = mChannel

    override fun getChat(): MutableList<Room> = mChat

}