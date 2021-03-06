package com.phdlabs.sungwon.a8chat_android.db.channels

import com.phdlabs.sungwon.a8chat_android.api.data.channel.BroadcastData
import com.phdlabs.sungwon.a8chat_android.api.data.channel.ChannelPostData
import com.phdlabs.sungwon.a8chat_android.api.data.channel.CommentPostData
import com.phdlabs.sungwon.a8chat_android.api.rest.Rest
import com.phdlabs.sungwon.a8chat_android.db.user.UserManager
import com.phdlabs.sungwon.a8chat_android.model.channel.Channel
import com.phdlabs.sungwon.a8chat_android.model.channel.ChannelShowNest
import com.phdlabs.sungwon.a8chat_android.model.channel.Comment
import com.phdlabs.sungwon.a8chat_android.model.channel.NewlyCreatedComment
import com.phdlabs.sungwon.a8chat_android.model.message.Message
import com.phdlabs.sungwon.a8chat_android.model.room.Room
import com.vicpin.krealmextensions.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody

/**
 * Created by JPAM on 2/26/18.
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


    /*Properties*/
    private var mUserManager: UserManager

    init {
        mUserManager = UserManager.instance
    }

    /**
     * [disposables]
     * List of disposables used for memory management during user navigation
     * */
    val disposables: MutableList<Disposable> = mutableListOf()

    /**
     * [clearDisposables]
     * Release API RX Call resources for memory management
     * */
    fun clearDisposables() {
        for (disposable in disposables) {
            if (!disposable.isDisposed) {
                disposable.dispose()
            }
        }
        disposables.clear()
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
        mUserManager.getCurrentUser { isSuccess, user, token ->
            if (isSuccess) {
                user?.let {
                    if (refresh) { //API Query & caching
                        //Clear my cached "Channels"
                        Channel().delete { equalTo("user_creator_id", user.id) }
                        //Get Data
                        token?.token?.let {
                            //Which user
                            var userInfoId: Int = user.id!!
                            userId?.let {
                                userInfoId = it
                            }
                            //Call
                            val call = Rest.getInstance().getmCallerRx().getUserChannels(token.token!!, userInfoId)
                            disposables.add(call.subscribeOn(Schedulers.io())
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
                                                    equalTo("user_creator_id", userInfoId)
                                                }), null))
                                            }
                                        } else if (response.isError) {
                                            callback(Pair(null, "could not download channels"))
                                        }
                                    }, { throwable ->
                                        callback(Pair(null, throwable.localizedMessage))
                                    }))
                        }
                    } else { //Local query
                        userId?.let {
                            callback(Pair((Channel().query {
                                equalTo("user_creator_id", userId)
                            }), null))
                        } ?: run {
                            callback(Pair((Channel().query {
                                equalTo("user_creator_id", user.id)
                            }), null))
                        }
                    }
                }
            }
        }
        mUserManager.clearDisposables()
    }

    /**
     * [getMyFollowedChannelsWithFlags]
     * @param refresh -> If true will download fresh data, else will read from @see Realm
     * Updates the realm with the User's followed channels
     * @return (Popular Channels, Pair(Unpopular_Unread_Channels, UnPopular_Read_Channels), Error Message)
     * @return (Array<Channel>?), Pair(<Array<Channel>?,Array<Channel>?>), String?)
     *     @see MyFollowedChannels
     *
     *
     * Only call [getMyFollowedChannelsWithFlags] Channels after calling [getUserChannels]
     * This is important as it updates the @Realm local copy with
     * the @see iFollow , @see unread_messages , @see isPopular & @see last_activity
     *
     * WARNING: Currently not working with backend
     * */
    fun getMyFollowedChannelsWithFlags(refresh: Boolean, callback: (List<Channel>?, Pair<List<Channel>?, List<Channel>?>?, String?) -> Unit) {
        mUserManager.getCurrentUser { isSuccess, user, token ->
            if (isSuccess) {
                user?.let {
                    if (refresh) { //API Query & caching
                        token?.token?.let {
                            val call = Rest.getInstance().getmCallerRx().getMyFollowedChannels(it, user.id!!)
                            disposables.add(call.subscribeOn(Schedulers.io())
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
                                                    queryPopularChannels(),
                                                    Pair(queryCachedUnpopularUnreadFollowedChannels(), queryCachedUnpopularReadFollowedChannels()),
                                                    null
                                            )
                                        } else if (response.isError) {
                                            callback(null, null, "could not download favorite channels")
                                        }
                                    }, { throwable ->
                                        callback(null, null, throwable.localizedMessage)
                                    }))
                        }
                    } else { //Local Query
                        callback(
                                queryPopularChannels(),
                                Pair(queryCachedUnpopularUnreadFollowedChannels(), queryCachedUnpopularReadFollowedChannels()),
                                null
                        )
                    }
                }
            }
        }
        mUserManager.clearDisposables()
    }


    /**
     * [getChannelPosts]
     * Retrieve last 40 messages from a Channel
     * @return Pair<List<Message>?, String?> == Pair<PostsList, ErrorMessage>
     * @see Realm
     * */
    fun getChannelPosts(refresh: Boolean, roomId: Int, query: String?, callback: (Pair<List<Message>?, String?>) -> Unit) {
        mUserManager.getCurrentUser { isSuccess, user, token ->
            if (isSuccess) {
                user?.let {
                    token?.token?.let {
                        if (refresh) {
                            val call = Rest.getInstance().getmCallerRx().getChannelPosts(
                                    it,
                                    roomId, user.id!!,
                                    query ?: ""
                            )
                            disposables.add(call.subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({ response ->
                                        if (response.isSuccess) { //Success
                                            response.messages?.allMessages?.let {
                                                callback(Pair(it.toList(), null))
                                                it.saveAll()
                                            }
                                        } else if (response.isError) { //Error
                                            callback(Pair(null, "No posts found"))
                                        }
                                    }, { throwable ->
                                        callback(Pair(null, throwable.localizedMessage))
                                    }))
                        } else {
                            //Local Query
                            callback(Pair(queryChannelMessages(roomId), null))
                        }
                    }
                }
            }
        }
        mUserManager.clearDisposables()
    }

    /**
     * [searchChannels]
     * Query for channels
     * @param query -> Single to multiple characters string
     * @callback Pair(Channels?, ErrorMessage?)
     * */
    fun searchChannels(query: String?, callback: (Pair<List<Channel>?, String?>) -> Unit) {
        mUserManager.getCurrentUser { success, user, token ->
            if (success) {
                user?.let {
                    token?.token?.let {
                        val call = Rest.getInstance().getmCallerRx().searchChannel(it, query)
                        disposables.add(call.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({ response ->
                                    if (response.isSuccess) {
                                        response.channels?.let {
                                            callback(Pair(it.toList(), null))
                                        }
                                    } else if (response.isError) {
                                        callback(Pair(null, "No Channels for query"))
                                    }
                                }, { throwable ->
                                    callback(Pair(null, throwable.localizedMessage))
                                }))
                    }
                }
            }
        }
        mUserManager.clearDisposables()
    }

    /**
     * [getPostComments]
     * Retrieve last 40 comments from a Channel Post
     * @return List<Comment>
     * @see Realm
     * */
    fun getPostComments(messageId: Int, callback: (Pair<List<Comment>?, String?>) -> Unit) {
        mUserManager.getCurrentUser { success, user, token ->
            if (success) {
                user?.let {
                    token?.token?.let {
                        val call = Rest.getInstance().getmCallerRx().getPostComments(it, messageId, user.id!!, "")
                        disposables.add(call.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({ response ->
                                    if (response.isSuccess) {
                                        response.comments?.let {
                                            callback(Pair(it.toList(), null))
                                        }
                                    } else if (response.isError) {
                                        callback(Pair(null, "Could not download comments"))
                                    }
                                }, { throwable ->
                                    callback(Pair(null, throwable.localizedMessage))
                                }))
                    }
                }
            }

        }
        mUserManager.clearDisposables()
    }

    /**
     * [commentOnChannelsPost]
     * @return Pair<Array of comments, ErrorMessage>
     * This API call is used inside the [ChannelPostShowController] with the Socket channel function
     * */
    fun commentOnChannelsPost(messageId: String, comment: String, callback: (Pair<NewlyCreatedComment?, String?>) -> Unit) {
        mUserManager.getCurrentUser { success, user, token ->
            if (success) {
                user?.let {
                    token?.token?.let {
                        val call = Rest.getInstance().getmCallerRx().commentOnChannelPost(
                                it, messageId,
                                CommentPostData(user.id.toString(), comment)
                        )
                        disposables.add(call.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({ response ->
                                    if (response.isSuccess) {
                                        callback(Pair(response.newlyCreatedComment, null))
                                    } else if (response.isError) {
                                        callback(Pair(null, "Could not download comments"))
                                    }
                                }, { throwable ->
                                    throwable.printStackTrace()
                                    callback(Pair(null, "You have to follow the channel to be able to comment"))
                                }))
                    }
                }
            }
        }
        mUserManager.clearDisposables()
    }

    /**
     * [likeUnlikePost]
     * Like or Unlike a Post[Message] inside a Channel
     * @param messageId -> Message to be liked || unliked
     * @param unlike -> Indicate if it's an unlike
     * @Query Boolean to indicate like || unlike
     * */
    fun likeUnlikePost(messageId: Int, unlike: Boolean) {
        mUserManager.getCurrentUser { success, user, token ->
            if (success) {
                user?.let {
                    token?.token?.let {
                        if (unlike) {
                            val call = Rest.getInstance().getmCallerRx().likePost(it, messageId, user.id!!, unlike)
                            disposables.add(call.subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({ response ->
                                        if (response.success == true) {
                                            println("Message: " + response.responseMsg)
                                        }

                                    }, {
                                        println("Error liking messsage: " + it.localizedMessage)
                                    }))
                        } else {
                            val call = Rest.getInstance().getmCallerRx().likePost(it, messageId, user.id!!, null)
                            disposables.add(call.subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({ response ->
                                        if (response.success == true) {
                                            println("Message: " + response.responseMsg)
                                        }

                                    }, {
                                        println("Error liking messsage: " + it.localizedMessage)
                                    }))
                        }
                        disposables.clear()
                    }
                }
            }
        }
        mUserManager.clearDisposables()
    }

    /**
     * [likeBroadcastPost]
     * Like a broadcast as Post[Message] inside a Channel Live Broadcast
     * @param messageId -> Message to be liked & simultaneously un-liked
     * This behaviour handles multiple liking on a Live Broadcast & will produce continuous animation
     * */
    fun likeBroadcastPost(messageId: Int) {
        mUserManager.getCurrentUser { success, user, token ->
            if (success) {
                user?.let {
                    token?.token?.let {
                        val call = Rest.getInstance().getmCallerRx().likePost(it, messageId, user.id!!, null)
                        disposables.add(call.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({ response ->
                                    if (response.success == true) {
                                        println("Message: " + response.responseMsg)
                                        //Unlike post after liking so the user can like it again on the broadcast (continuous)
                                        likeUnlikePost(messageId, true)
                                    }
                                }, {
                                    println("Error liking messsage: " + it.localizedMessage)
                                })
                        )
                    }
                }
            }
        }
        mUserManager.clearDisposables()
    }


    /**
     * [followChannel]
     * Used to follow channel*
     * If successful the function will cache the new room information
     *
     * @param channelId Int?
     * @param participantId: List<Int>?
     * @callback success -> Will return a success or error Boolean value of the transaction
     * */
    fun followChannel(channelId: Int, participantId: Int, callback: (String) -> Unit) {
        mUserManager.getCurrentUser { success, user, token ->
            if (success) {
                user?.let {
                    token?.token?.let {
                        val multipartBodyPart = MultipartBody.Part.createFormData(
                                "userIds[]",
                                "$participantId"
                        )
                        val multipartForm = MultipartBody.Builder().setType(MultipartBody.FORM)
                                .addPart(multipartBodyPart)
                                .build()
                        val call = Rest.getInstance().getmCallerRx().followChannel(it, channelId, multipartForm)
                        disposables.add(call.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({
                                    if (it.isSuccess) {
                                        it?.room?.let {
                                            it.save()
                                            callback("Following Channel")
                                        }
                                    } else if (it.isError) {
                                        callback("Could not follow channel at this time")
                                    }
                                }, { throwable ->
                                    callback(throwable.localizedMessage)
                                }))
                    }
                }
            }
        }
        mUserManager.clearDisposables()
    }

    /**
     * [unfollowChannel]
     * Usued to un-follow a specific channel
     *
     * @param roomId
     * @param userId
     * @return message: String? of currently un-followed channel
     * */
    fun unfollowChannel(roomId: Int, callback: (String?) -> Unit) {
        mUserManager.getCurrentUser { success, user, token ->
            if (success) {
                user?.let {
                    token?.token?.let {
                        val call = Rest.getInstance().getmCallerRx().unfollowChannel(it, roomId, user.id!!)
                        disposables.add(call.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({ response ->
                                    if (response.isSuccess) {
                                        //Remove iFollow channel property for Lobby purposes
                                        Room().queryFirst { equalTo("id", roomId) }?.let {
                                            it.channel?.let {
                                                if (it) {
                                                    Channel().queryFirst { equalTo("room_id", roomId) }?.let {
                                                        if (it.iFollow) {
                                                            it.iFollow = false
                                                            it.save()
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        //Callback
                                        response?.message?.let(callback)
                                    } else if (response.isError) {
                                        callback("Could not un-follow channel")
                                    }
                                }, { throwable ->
                                    callback(throwable.localizedMessage)
                                }))
                    }
                }
            }
        }
        mUserManager.clearDisposables()
    }

    /**
     * [startBroadcast]
     * Start Live Video Broadcast
     * Note: Broadcasts are treated as posts to enable live commenting functionality while on the call
     * @param roomId
     * @callback Pair<[Message] [ErrorMessage]> with information about the current live broadcast access
     * */
    fun startBroadcast(roomId: Int, callback: (Pair<Message?, String?>) -> Unit) {
        mUserManager.getCurrentUser { success, user, token ->
            if (success) {
                user?.let {
                    token?.token?.let {
                        val call = Rest.getInstance().getmCallerRx()
                                .startBroadcast(it, BroadcastData(user.id!!, roomId), true)
                        disposables.add(call.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({
                                    if (it.isSuccess) {
                                        callback(Pair(it.newlyCreatedMsg, null))
                                    } else if (it.isError) {
                                        callback(Pair(null, "Couldn't start Broadcast"))
                                    }
                                }, { throwable ->
                                    callback(Pair(null, throwable.localizedMessage))
                                })
                        )
                    }
                }
            }
        }
        mUserManager.clearDisposables()
    }

    /**
     * [finishBroadcast]
     * Finish Live Video Broadcast
     * @param roomId
     * @callback Pair<[Message] [ErrorMessage]> with information about the finished live broadcast
     * */
    fun finishBroadcast(roomId: Int, messageId: Int, callback: (Pair<Message?, String?>) -> Unit) {
        mUserManager.getCurrentUser { success, user, token ->
            if (success) {
                user?.let {
                    token?.token?.let {
                        val call = Rest.getInstance().getmCallerRx()
                                .finishBroadcast(it, messageId.toString(), BroadcastData(user.id!!, roomId))
                        disposables.add(call.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({
                                    if (it.isSuccess) {
                                        callback(Pair(it.message, null))
                                    } else if (it.isError) {
                                        callback(Pair(null, "Could not finish broadcast"))
                                    }
                                }, { throwable ->
                                    callback(Pair(null, throwable.localizedMessage))
                                })
                        )
                    }
                }
            }
        }
        mUserManager.clearDisposables()
    }

    /**
     * [updateChannel]
     * Update a current owner channel with new media update
     * @param channelId [Int]
     * @param channelPostData [ChannelPostData]
     * @callback Pair<Channel?, String?> -> Pair<UpdatedChannel, ErrorMessage>
     *     TODO: REFACTOR -> It's not working properly
     * */
    fun updateChannel(channelId: Int, channelPostData: ChannelPostData, callback: (Pair<Channel?, String?>) -> Unit) {
        mUserManager.getCurrentUser { success, user, token ->
            if (success) {
                user?.let {
                    token?.token?.let {
                        val call = Rest.getInstance().getmCallerRx().updateChannel(it, channelId, channelPostData)
                        disposables.add(call.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({ response ->
                                    if (response.isSuccess) {
                                        response?.channel?.let {
                                            it.save()
                                            callback(Pair(it, null))
                                        }
                                    } else if (response.isError) {
                                        callback(Pair(null, "Could not update channel"))
                                    }
                                }, { throwable ->
                                    callback(Pair(null, throwable.localizedMessage))
                                }))
                    }
                }
            }
        }
        mUserManager.clearDisposables()
    }

    /**
     * [deleteChannel]
     * Delete channel from Channel settings
     * @param channelId [Int]
     * */
    fun deleteChannel(channelId: Int, callback: (String?) -> Unit) {
        mUserManager.getCurrentUser { success, user, token ->
            if (success) {
                user?.let {
                    token?.token?.let {
                        val call = Rest.getInstance().getmCallerRx().deleteChannel(it, channelId)
                        disposables.add(call.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({ response ->
                                    if (response.isSuccess) {
                                        response.message?.let(callback)
                                    } else if (response.isError) {
                                        callback("Could not delete Channel")
                                    }
                                }, { throwable ->
                                    callback(throwable.localizedMessage)
                                }))
                    }
                }
            }
        }
        mUserManager.clearDisposables()
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
     * My Channels
     * */
    fun queryMyChannels(): List<Channel>? {
        return Channel().query {
            UserManager.instance.getCurrentUser { success, user, token ->
                if (success) {
                    user?.id?.let {
                        equalTo("user_creator_id", it)
                    }
                }
            }
        }
    }

    /**
     * Popular Channels
     * */
    fun queryPopularChannels(): List<Channel>? {
        return Channel().query {
            equalTo("isPopular", true)
        }
    }

    /**
     * All the channels I Follow
     * */
    fun queryFollowedChannels(): List<Channel>? {
        return Channel().query {
            equalTo("iFollow", true)
            equalTo("isPopular", false)
        }
    }

    /**
     * All channels
     * */
    fun queryAllChannels(): List<Channel>? = Channel().queryAll()

    /**
     * Unpopular & Unread Followed Channels
     * */
    private fun queryCachedUnpopularUnreadFollowedChannels(): List<Channel>? {
        return Channel().query {
            equalTo("iFollow", true)
            equalTo("isPopular", false)
            equalTo("unread_messages", true)
        }
    }

    /**
     * Unpopular & Read Followed Channels
     * */
    private fun queryCachedUnpopularReadFollowedChannels(): List<Channel>? {
        return Channel().query {
            equalTo("iFollow", true)
            equalTo("isPopular", false)
            equalTo("unread_messages", false)
        }
    }


    /**
     * [queryChannelMessages]
     * Used for reading and updating local post & channel's messages copy
     * Primarily used to manage likes & comments within [MyChannelActivity] & [ChannelPostShowActivity]
     * */
    fun queryChannelMessages(roomId: Int): List<Message>? {
        return Message().query {
            equalTo("roomId", roomId)
        }
    }

    /**
     * [querySingleChannelWithChannelId]
     * @return channel that matches the provided ID
     * */
    fun querySingleChannelWithChannelId(channelId: Int): Channel? =
            Channel().queryFirst { equalTo("id", channelId) }

    fun querySingleChannelWithRoomId(roomId: Int): Channel? =
            Channel().queryFirst { equalTo("room_id", roomId) }


    /**
     * [queryChannelMessagesByType]
     * Used to retrieve the messages from a Channel & then Query Media OR Files based on Message Type
     * @param roomId
     * @param type
     * @return List<Message>?
     * */
    fun queryChannelMessagesByType(roomId: Int, type: String): List<Message>? {
        return Message().query {
            equalTo("roomId", roomId)
            equalTo("type", type)
        }
    }

}














