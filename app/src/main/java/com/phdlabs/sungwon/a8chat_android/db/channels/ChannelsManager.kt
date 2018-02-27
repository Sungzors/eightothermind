package com.phdlabs.sungwon.a8chat_android.db.channels

import com.phdlabs.sungwon.a8chat_android.api.rest.Rest
import com.phdlabs.sungwon.a8chat_android.db.user.UserManager
import com.phdlabs.sungwon.a8chat_android.model.channel.Channel
import com.phdlabs.sungwon.a8chat_android.model.room.Room
import com.vicpin.krealmextensions.delete
import com.vicpin.krealmextensions.query
import com.vicpin.krealmextensions.queryAll
import com.vicpin.krealmextensions.save
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by paix on 2/26/18.
 * [ChannelsManager]
 * Used for downloading & caching [Room] [Channel] information
 */
class ChannelsManager {

    /*Singleton*/
    private object Holder {
        val instance: ChannelsManager = ChannelsManager()
    }

    companion object {
        val instance: ChannelsManager by lazy { Holder.instance }
    }

    /**
     * [getMyChannels]
     * Updates the realm with the current user's channel
     * @default scope is to public -> All channels
     * @return Pair<Array<Channel>?, String?>
     *     @see MyChannels, ErrorMessage
     * */
    fun getMyChannels(refresh: Boolean, callback: (Pair<List<Channel>?, String?>) -> Unit) {
        UserManager.instance.getCurrentUser { isSuccess, user, token ->
            if (isSuccess) {
                user?.let {
                    if (refresh) { //API Query & caching
                        //Clear my cached "Channels"
                        Channel().delete { it.equalTo("user_creator_id", user.id) }
                        //Get Data
                        token?.token?.let {
                            val call = Rest.getInstance().getmCallerRx().getMyChannels(token.token!!, user.id!!)
                            call.subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({ response ->
                                        if (response.isSuccess) {
                                            response?.channels?.let {
                                                /**
                                                 * Save || Update -> [Room] & [Channel] info
                                                 * */
                                                for (room in it) {
                                                    //Room data
                                                    val roomToUpdate = Room()
                                                    roomToUpdate.id = room.id
                                                    roomToUpdate.channel = room.channel
                                                    roomToUpdate.event = room.event
                                                    roomToUpdate.groupChat = room.groupChat
                                                    roomToUpdate.privateChat = room.privateChat
                                                    roomToUpdate.locked = room.locked
                                                    roomToUpdate.participantsId = room.participantsId
                                                    roomToUpdate.last_activity = room.last_activity
                                                    roomToUpdate.last_activity_in_associated_channel_event_or_groupChat = room.last_activity_in_associated_channel_event_or_groupChat
                                                    roomToUpdate.createdAt = room.createdAt
                                                    roomToUpdate.updatedAt = room.updatedAt
                                                    roomToUpdate.save()
                                                    //Channel data
                                                    for (channel in room.channels) {
                                                        channel?.save()
                                                    }
                                                }
                                                //Callback with Realm Query
                                                callback(Pair((Channel().query { it.equalTo("user_creator_id", user.id) }), null))
                                            }
                                        } else if (response.isError) {
                                            callback(Pair(null, "could not download channels"))
                                        }
                                    }, { throwable ->
                                        callback(Pair(null, throwable.localizedMessage))
                                    })
                        }
                    } else { //Local query
                        callback(Pair((Channel().query { it.equalTo("user_creator_id", user.id) }), null))
                    }
                }
            }
        }
    }
}