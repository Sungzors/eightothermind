package com.phdlabs.sungwon.a8chat_android.db

import com.phdlabs.sungwon.a8chat_android.model.channel.Channel
import com.phdlabs.sungwon.a8chat_android.model.message.Message
import com.phdlabs.sungwon.a8chat_android.model.room.Room

/**
 * Created by SungWon on 12/4/2017.
 * temp until realm is set up
 */
class TemporaryManager {
    companion object {
        val instance: TemporaryManager = TemporaryManager()
    }

    val mChannelList = mutableListOf<Channel>()

    fun getChannel(channelId: Int): Channel?{
        for(channel in mChannelList){
            if (channel.id == channelId){
                return channel
            }
        }
        return null
    }

    val mMessageList = mutableListOf<Message>()

    fun getMessage(messageId: Int): Message?{
        for(message in mMessageList){
            if(message.id == messageId){
                return message
            }
        }
        return null
    }

    val mRoomList = mutableListOf<Room>()

    fun getRoom(roomId: Int): Room?{
        for(room in mRoomList){
            if(room.id == roomId){
                return room
            }
        }
        return null
    }

    val mMemberList = mutableListOf<Triple<Int, String, String>>()

    fun isMemberChecked(userId: Int): Boolean {
        for(member in mMemberList){
            if(member.first==userId){
                return true
            }
        }
        return false
    }

}