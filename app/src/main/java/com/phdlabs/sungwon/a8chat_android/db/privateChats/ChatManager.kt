package com.phdlabs.sungwon.a8chat_android.db.privateChats

import com.phdlabs.sungwon.a8chat_android.model.room.Room

/**
 * Created by JPAM on 3/2/18.
 * Network & Caching for [Message] [Room]in Private Chats
 */
class ChatManager {

    /*Singleton*/
    private object Holder {
        val instance = ChatManager()
    }

    companion object {
        val instance by lazy { Holder.instance }
    }

    /**
     * [getPrivateChats]
     * @param refresh Download or read from cache
     * @return Array of [Room] Private Chats
     * */
    fun getPrivateChats(refresh: Boolean, callback: (List<Room>) -> Unit) {
        //TODO: Get messages from the private chat rooms

        //        UserManager.instance.getCurrentUser { success, user, token ->
//            if (success) {
//                mChat.clear()
//                val call = mCaller.getAllChats(token?.token, user?.id!!)
//                call.enqueue(object : Callback8<ChatsRetrievalResponse, Event>(mEventBus) {
//                    override fun onSuccess(data: ChatsRetrievalResponse?) {
//                        mChat.addAll(data!!.chats!!)
//                        if (mChat.size > 0) {
//                            mChat.saveAll()
//                            mView.setUpChatRecycler(mChat)
//                        }
//                    }
//                })
//            }
//        }
    }

}