package com.phdlabs.sungwon.a8chat_android.structure.main.lobby

import com.phdlabs.sungwon.a8chat_android.model.EventsEight
import com.phdlabs.sungwon.a8chat_android.model.channel.Channel
import com.phdlabs.sungwon.a8chat_android.model.room.Room
import com.phdlabs.sungwon.a8chat_android.structure.core.BaseController
import com.phdlabs.sungwon.a8chat_android.structure.core.BaseView

/**
 * Created by SungWon on 10/17/2017.
 */
interface LobbyContract {

    interface View: BaseView<Controller>{
        fun setUpMyChannelRecycler()
        fun setUpEventsRecycler()
        fun setUpChannelsFollowedRecycler()
        fun setUpChannelRecycler()
        fun setUpChatRecycler()

        fun updateMyChannelRecycler()
        fun updateEventsRecycler()
        fun updateChannelsFollowedRecycler()
        fun updateChannelRecycler()
        fun updateChatRecycler()
    }

    interface Controller: BaseController {
        fun getMyChannel(): MutableList<Channel>
        fun getEvents(): MutableList<EventsEight>
        fun getChannelsFollowed(): MutableList<Channel>
        fun getChannel(): MutableList<Channel>
        fun getChat(): MutableList<Room>
    }
}