package com.phdlabs.sungwon.a8chat_android.structure.main.lobby

import com.phdlabs.sungwon.a8chat_android.model.Channel
import com.phdlabs.sungwon.a8chat_android.model.EventsEight
import com.phdlabs.sungwon.a8chat_android.model.Room

/**
 * Created by SungWon on 10/17/2017.
 */
class LobbyController(val mView: LobbyContract.View): LobbyContract.Controller {

    //connects to LobbyFragment

    private val mMyChannel = mutableListOf<Channel>()
    private val mEvents = mutableListOf<EventsEight>()
    private val mChannelsFollowed = mutableListOf<Channel>()
    private val mChannel = mutableListOf<Channel>()
    private val mChat = mutableListOf<Room>()

    init {
        mView.controller = this
    }

    override fun start() {
    }

    override fun resume() {
    }

    override fun pause() {
    }

    override fun stop() {
    }

    override fun getMyChannel(): MutableList<Channel> = mMyChannel

    override fun getEvents(): MutableList<EventsEight> = mEvents

    override fun getChannelsFollowed(): MutableList<Channel> = mChannelsFollowed

    override fun getChannel(): MutableList<Channel> = mChannel

    override fun getChat(): MutableList<Room> = mChat
}