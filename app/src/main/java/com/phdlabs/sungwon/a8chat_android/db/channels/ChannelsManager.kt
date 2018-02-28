package com.phdlabs.sungwon.a8chat_android.db.channels

import com.phdlabs.sungwon.a8chat_android.api.rest.Rest
import com.phdlabs.sungwon.a8chat_android.db.user.UserManager
import com.phdlabs.sungwon.a8chat_android.model.channel.Channel
import com.phdlabs.sungwon.a8chat_android.model.channel.ChannelShowNest
import com.phdlabs.sungwon.a8chat_android.model.room.Room
import com.vicpin.krealmextensions.delete
import com.vicpin.krealmextensions.query
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
     * @param refresh -> If true will download fresh data, else will read from @see Realm
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
                                                    //Update room
                                                    updateRoom(room)
                                                    //Channel data
                                                    for (channel in room.channels) {
                                                        channel?.user_creator_id?.let {
                                                            if (it == user.id!!){
                                                                channel.save()
                                                            }
                                                        }
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

    /**
     * [getMyFollowedChannels]
     * @param refresh -> If true will download fresh data, else will read from @see Realm
     * Updates the realm with the User's followed channels
     * @return (Pair(Popular_Unread_Channels, Popular_Read_Channels), Pair(Unpopular_Unread_Channels, UnPopular_Read_Channels), Error Message)
     * @return (Pair(<Array<Channel>?,Array<Channel>?>), Pair(<Array<Channel>?,Array<Channel>?>), String?)
     *     @see MyFollowedChannels
     *
     *
     * Only call [getMyFollowedChannels] Channels after calling [getMyChannels]
     * This is important as it updates the @Realm local copy with
     * the @see iFollow , @see unread_messages , @see isPopular & @see last_activity
     * */
    fun getMyFollowedChannels(refresh: Boolean, callback: (Pair<List<Channel>?, List<Channel>?>?, Pair<List<Channel>?, List<Channel>?>?, String?) -> Unit) {
        UserManager.instance.getCurrentUser { isSuccess, user, token ->
            if (isSuccess) {
                user?.let {
                    if (refresh) { //API Query & caching
                        token?.token?.let {
                            val call = Rest.getInstance().getmCallerRx().getMyFollowedChannels(it, user.id!!)
                            call.subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({ response ->
                                        if (response.isSuccess) {
                                            //Save channels
                                            response.channels?.let {
                                                for (channelNestFollowResponse in it) {
                                                    channelNestFollowResponse?.channel?.let {
                                                        val updatedChannel = it
                                                        updatedChannel.user_creator_id?.let {
                                                            if (it != user.id!!) {
                                                                updatedChannel.unread_messages = channelNestFollowResponse.unread_messages
                                                                updatedChannel.last_activity = channelNestFollowResponse.last_activity
                                                                updatedChannel.isPopular = channelNestFollowResponse.isPopular
                                                                updatedChannel.iFollow = true
                                                                updatedChannel.save()
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            //Query
                                            callback(
                                                    Pair(getCachedPopularUnreadFollowedChannels(), getCachedPopularReadFollowedChannels()),
                                                    Pair(getCachedUnpopularUnreadFollowedChannels(), getCachedUnpopularReadFollowedChannels()),
                                                    null
                                            )
                                        } else if (response.isError) {
                                            callback(null, null, "could not download favorite channels")
                                        }
                                    }, { throwable ->
                                        callback(null, null, throwable.localizedMessage)
                                    })
                        }
                    } else { //Local Query
                        callback(
                                Pair(getCachedPopularUnreadFollowedChannels(), getCachedPopularReadFollowedChannels()),
                                Pair(getCachedUnpopularUnreadFollowedChannels(), getCachedUnpopularReadFollowedChannels()),
                                null
                        )
                    }
                }
            }
        }
    }

    /**
     * [updateRoom]
     * Used to update room information on pulled [Channel]
     * @see [getMyChannels]
     *
     * */
    private fun updateRoom(channelShowNest: ChannelShowNest?) {
        channelShowNest?.let {
            val roomToUpdate = Room()
            roomToUpdate.id = it.id
            roomToUpdate.channel = it.channel
            roomToUpdate.event = it.event
            roomToUpdate.groupChat = it.groupChat
            roomToUpdate.privateChat = it.privateChat
            roomToUpdate.locked = it.locked
            roomToUpdate.participantsId = it.participantsId
            roomToUpdate.last_activity = it.last_activity
            roomToUpdate.last_activity_in_associated_channel_event_or_groupChat = it.last_activity_in_associated_channel_event_or_groupChat
            roomToUpdate.createdAt = it.createdAt
            roomToUpdate.updatedAt = it.updatedAt
            roomToUpdate.save()
        }
    }

    /**
     * REALM QUERIES for [ChannelsManager]
     * data sorting
     * */

    /**
     * Popular & Unread Followed Channels
     * */
    private fun getCachedPopularUnreadFollowedChannels(): List<Channel>? {
        return Channel().query { channels ->
            channels.equalTo("iFollow", true)
            channels.equalTo("isPopular", true)
            channels.equalTo("unread_messages", true)
        }
    }

    /**
     * Popular & Read Followed Channels
     * */
    private fun getCachedPopularReadFollowedChannels(): List<Channel>? {
        return Channel().query { channels ->
            channels.equalTo("iFollow", true)
            channels.equalTo("isPopular", true)
            channels.equalTo("unread_messages", false)
        }
    }

    /**
     * Unpopular & Unread Followed Channels
     * */
    private fun getCachedUnpopularUnreadFollowedChannels(): List<Channel>? {
        return Channel().query { channels ->
            channels.equalTo("iFollow", true)
            channels.equalTo("isPopular", false)
            channels.equalTo("unread_messages", true)
        }
    }

    /**
     * Unpopular & Read Followed Channels
     * */
    private fun getCachedUnpopularReadFollowedChannels(): List<Channel>? {
        return Channel().query { channels ->
            channels.equalTo("iFollow", true)
            channels.equalTo("isPopular", false)
            channels.equalTo("unread_messages", false)
        }
    }

}












