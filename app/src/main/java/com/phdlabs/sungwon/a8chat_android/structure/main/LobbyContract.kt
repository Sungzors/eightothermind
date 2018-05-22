package com.phdlabs.sungwon.a8chat_android.structure.main

import android.location.Location
import com.phdlabs.sungwon.a8chat_android.model.channel.Channel
import com.phdlabs.sungwon.a8chat_android.model.event.EventsEight
import com.phdlabs.sungwon.a8chat_android.model.room.Room
import com.phdlabs.sungwon.a8chat_android.structure.core.BaseController
import com.phdlabs.sungwon.a8chat_android.structure.core.BaseView

/**
 * Created by SungWon on 10/17/2017.
 */
interface LobbyContract {

    //Lobby VC
    interface View : BaseView<Controller> {
        //UI
        fun setUpChannelRecycler()

        fun setUpEventsRecycler()
        fun setUpChatRecycler()
        fun setSeparatorCounter(pos: Int)

        //Activity
        fun activity(): MainActivity

        /*Refresh UI*/
        fun refreshChat()

        fun refreshMyChannels()
        fun refreshFollowedChannels()
        fun refreshEvents()

    }

    interface Controller : BaseController {
        fun onViewCreated()
        fun getMyChannels(): MutableList<Channel>
        fun getChannelsFollowed(): MutableList<Channel>
        fun getEvents(): MutableList<Room>
        fun callEvents(refresh: Boolean, location: Location)
        fun getChat(): MutableList<Room>
        fun refreshAll()
        fun setRefreshFlag(shouldRefresh: Boolean)
        fun getRefreshFlag(): Boolean
    }

    //Pop up Menu VC
    interface Overlay {
        interface View : BaseView<Controller> {
            fun getActivityDirect(): MainActivity
        }

        interface Controller : BaseController {

        }
    }
}