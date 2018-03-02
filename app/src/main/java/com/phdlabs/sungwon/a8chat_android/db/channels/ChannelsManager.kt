package com.phdlabs.sungwon.a8chat_android.db.channels

import com.phdlabs.sungwon.a8chat_android.api.rest.Rest
import com.phdlabs.sungwon.a8chat_android.db.user.UserManager
import com.phdlabs.sungwon.a8chat_android.model.channel.Channel
import com.phdlabs.sungwon.a8chat_android.model.channel.ChannelShowNest
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
     * [getUserChannels]
     * Updates the realm with the current user's channel
     * @param userId -> User to get channels from, if [null] will return current user Channels
     * @param refresh -> If true will download fresh data, else will read from @see Realm
     * @default scope is to public -> All channels
     * @return Pair<Array<Channel>?, String?>
     *     @see MyChannels, ErrorMessage
     * */
    fun getUserChannels(userId: Int?, refresh: Boolean, callback: (Pair<List<Channel>?, String?>) -> Unit) {
        UserManager.instance.getCurrentUser { isSuccess, user, token ->
            if (isSuccess) {
                user?.let {
                    if (refresh) { //API Query & caching
                        //Clear my cached "Channels"
                        Channel().delete { it.equalTo("user_creator_id", user.id) }
                        //Get Data
                        token?.token?.let {
                            //Which user
                            var userInfoId: Int = user.id!!
                            userId?.let {
                                userInfoId = it
                            }
                            //Call
                            val call = Rest.getInstance().getmCallerRx().getUserChannels(token.token!!, userInfoId)
                            call.subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({ response ->
                                        if (response.isSuccess) {
                                            response?.channels?.let {
                                                /**
                                                 * Save || Update -> [Room] & [Channel] info
                                                 * */
                                                for (room in it) {
                                                    //Update room data
                                                    updateRoom(room)
                                                    for (channel in room.channels) {
                                                        //Update channel data
                                                        channel?.save()
                                                    }
                                                }
                                                //Callback with Realm Query
                                                callback(Pair((Channel().query {
                                                    it.equalTo("user_creator_id", userInfoId)
                                                }), null))
                                            }
                                        } else if (response.isError) {
                                            callback(Pair(null, "could not download channels"))
                                        }
                                    }, { throwable ->
                                        callback(Pair(null, throwable.localizedMessage))
                                    })
                        }
                    } else { //Local query
                        userId?.let {
                            callback(Pair((Channel().query {
                                it.equalTo("user_creator_id", userId)
                            }), null))
                        } ?: run {
                            callback(Pair((Channel().query {
                                it.equalTo("user_creator_id", user.id)
                            }), null))
                        }
                    }
                }
            }
        }
    }

    /**
     * [getMyFollowedChannels]
     * @param refresh -> If true will download fresh data, else will read from @see Realm
     * Updates the realm with the User's followed channels
     * @return (Popular Channels, Pair(Unpopular_Unread_Channels, UnPopular_Read_Channels), Error Message)
     * @return (Array<Channel>?), Pair(<Array<Channel>?,Array<Channel>?>), String?)
     *     @see MyFollowedChannels
     *
     *
     * Only call [getMyFollowedChannels] Channels after calling [getUserChannels]
     * This is important as it updates the @Realm local copy with
     * the @see iFollow , @see unread_messages , @see isPopular & @see last_activity
     * */
    fun getMyFollowedChannels(refresh: Boolean, callback: (List<Channel>?, Pair<List<Channel>?, List<Channel>?>?, String?) -> Unit) {
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
                                                                //Only unpopular channels have unread messages & last activity
                                                                channelNestFollowResponse.unread_messages?.let {
                                                                    updatedChannel.unread_messages = it
                                                                    updatedChannel.iFollow = true
                                                                }
                                                                channelNestFollowResponse.last_activity?.let {
                                                                    updatedChannel.last_activity = it
                                                                }
                                                                updatedChannel.isPopular = channelNestFollowResponse.isPopular
                                                                updatedChannel.save()
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            //Query
                                            callback(
                                                    getPopularChannels(),
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
                                getPopularChannels(),
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
     * @see [getUserChannels]
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
            it.messages?.let {
                if (it.count() > 0) {
                    roomToUpdate.message = it[0]
                }
            }
            roomToUpdate.save()
        }
    }

    /**
     * REALM QUERIES for [ChannelsManager]
     * data sorting
     * */

    /**
     * Popular Channels
     * */
    fun getPopularChannels(): List<Channel>? {
        return Channel().query { channels ->
            channels.equalTo("isPopular", true)
        }
    }

    /**
     * All the channels I Follow
     * */
    fun getAllFollowedChannels(): List<Channel>? {
        return Channel().query { channels ->
            channels.equalTo("iFollow", true)
            channels.equalTo("isPopular", false)
        }
    }

    /**
     * All channels
     * */
    fun getAllChannels(): List<Channel>? = Channel().queryAll()

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












