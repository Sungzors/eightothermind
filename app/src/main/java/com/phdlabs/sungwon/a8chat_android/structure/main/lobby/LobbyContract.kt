package com.phdlabs.sungwon.a8chat_android.structure.main.lobby

import com.phdlabs.sungwon.a8chat_android.model.event.EventsEight
import com.phdlabs.sungwon.a8chat_android.model.channel.Channel
import com.phdlabs.sungwon.a8chat_android.model.room.Room
import com.phdlabs.sungwon.a8chat_android.model.user.User
import com.phdlabs.sungwon.a8chat_android.model.user.registration.Token
import com.phdlabs.sungwon.a8chat_android.structure.core.BaseController
import com.phdlabs.sungwon.a8chat_android.structure.core.BaseView

/**
 * Created by SungWon on 10/17/2017.
 */
interface LobbyContract {

    interface View: BaseView<Controller>{
        fun setUpMyChannelRecycler(myChannels: MutableList<Channel>)
        fun setUpEventsRecycler(events: MutableList<EventsEight>)
        fun setUpChannelsFollowedRecycler(channelsFollowed: MutableList<Channel>)
        fun setUpChannelRecycler(allChannels: MutableList<Channel>)
        fun setUpChatRecycler(chats: MutableList<Room>)
    }

    interface Controller: BaseController {
        fun getMyChannel(): MutableList<Channel>
        fun getEvents(): MutableList<EventsEight>
        fun getChannelsFollowed(): MutableList<Channel>
        fun getChannel(): MutableList<Channel>
        fun getChat(): MutableList<Room>
        fun refreshAll()
        fun setRefreshFlag(shouldRefresh: Boolean)
        fun getRefreshFlag(): Boolean
    }
}