package com.phdlabs.sungwon.a8chat_android.structure.main.lobby

import com.phdlabs.sungwon.a8chat_android.model.channel.Channel
import com.phdlabs.sungwon.a8chat_android.model.room.Room
import com.phdlabs.sungwon.a8chat_android.structure.core.BaseController
import com.phdlabs.sungwon.a8chat_android.structure.core.BaseView
import com.phdlabs.sungwon.a8chat_android.structure.main.MainActivity

/**
 * Created by SungWon on 10/17/2017.
 */
interface LobbyContract {

    interface View: BaseView<Controller>{
        fun setUpChannelRecycler(myChannels: MutableList<Channel>)
        fun addFollowedChannels(followedChannels: MutableList<Channel>)
        fun setUpEventsRecycler(events: MutableList<Room>)
        fun setUpChatRecycler(chats: MutableList<Room>)
        fun setSeparatorCounter(pos: Int)

        fun getActivityDirect(): MainActivity

        fun refreshChat()
    }

    interface Controller: BaseController {
        fun getMyChannels(): MutableList<Channel>
        fun getChannelsFollowed(): MutableList<Channel>
        fun getEvents(): MutableList<Room>
        fun getChannel(): MutableList<Channel>
        fun getChat(): MutableList<Room>
        fun refreshAll()
        fun setRefreshFlag(shouldRefresh: Boolean)
        fun getRefreshFlag(): Boolean
        fun callForEvent()
    }
}