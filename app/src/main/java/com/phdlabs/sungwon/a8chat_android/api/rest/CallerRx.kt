package com.phdlabs.sungwon.a8chat_android.api.rest

import com.phdlabs.sungwon.a8chat_android.api.data.*
import com.phdlabs.sungwon.a8chat_android.api.data.channel.ChannelPostData
import com.phdlabs.sungwon.a8chat_android.api.data.channel.CommentPostData
import com.phdlabs.sungwon.a8chat_android.api.response.*
import com.phdlabs.sungwon.a8chat_android.api.response.channels.LikeResponse
import com.phdlabs.sungwon.a8chat_android.api.response.channels.MyChannelRoomsResponse
import com.phdlabs.sungwon.a8chat_android.api.response.channels.delete.DeleteChannelResponse
import com.phdlabs.sungwon.a8chat_android.api.response.channels.edit.ChannelEditResponse
import com.phdlabs.sungwon.a8chat_android.api.response.channels.follow.ChannelFollowResponse
import com.phdlabs.sungwon.a8chat_android.api.response.channels.post.ChannelPostResponse
import com.phdlabs.sungwon.a8chat_android.api.response.channels.search.SearchChannelsResponse
import com.phdlabs.sungwon.a8chat_android.api.response.channels.unfollow.UnFollowChannelResponse
import com.phdlabs.sungwon.a8chat_android.api.response.contacts.ContactsInvitedResponse
import com.phdlabs.sungwon.a8chat_android.api.response.contacts.ContactsPostResponse
import com.phdlabs.sungwon.a8chat_android.api.response.contacts.UserFriendsResponse
import com.phdlabs.sungwon.a8chat_android.api.response.createChannel.ChannelResponse
import com.phdlabs.sungwon.a8chat_android.api.response.createEvent.EventPostResponse
import com.phdlabs.sungwon.a8chat_android.api.response.eightEvents.EventRetrievalResponse
import com.phdlabs.sungwon.a8chat_android.api.response.favorite.PrivateChatFavoriteResponse
import com.phdlabs.sungwon.a8chat_android.api.response.media.FileResponse
import com.phdlabs.sungwon.a8chat_android.api.response.media.MediaResponse
import com.phdlabs.sungwon.a8chat_android.api.response.messages.FavoriteMessageResponse
import com.phdlabs.sungwon.a8chat_android.api.response.room.EnterLeaveRoomResponse
import com.phdlabs.sungwon.a8chat_android.api.rest.Caller.TOKEN
import com.phdlabs.sungwon.a8chat_android.model.user.User
import com.phdlabs.sungwon.a8chat_android.model.user.registration.RegistrationData
import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.http.*

/**
 * Created by JPAM on 12/20/17.
 * API calls using RXJava with purpose of User Mapping & Local Cache system
 * [CallerRx] User Observable functions for Retrofit, Kotlin & Rx mapping Realm

 */
interface CallerRx {


    /*USER*/
    @POST("/users")
    fun login(@Body registrationData: RegistrationData): Observable<UserDataResponse>

    @PATCH("/users/{userid}")
    fun updateUser(@Header(TOKEN) token: String, @Path("userid") userId: Int, @Body userData: UserData): Observable<UserDataResponse>

    @GET("/users/{userid}")
    fun getUser(@Header(TOKEN) token: String, @Path("userid") userId: Int): Observable<UserDataResponse>

    @GET("/users/{userId}/friends")
    fun getUserFriends(@Header(TOKEN) token: String, @Path("userId") userId: Int): Observable<UserFriendsResponse>

    /*MEDIA*/
    @Multipart
    @POST("/media")
    fun uploadMedia(@Header(TOKEN) token: String, @Part image: MultipartBody.Part): Observable<MediaResponse>

    @GET("/media/shared/{userId1}/{userId2}")
    fun getSharedMediaPrivate(@Header(TOKEN) token: String, @Path("userId1") userId1: Int,
                              @Path("userId2") userId2: Int): Observable<MediaResponse>

    @POST("/messages/media")
    fun postChannelMediaPost(@Header(TOKEN) token: String, @Body multipartBody: MultipartBody, @Query("post") post: Boolean): Observable<ChannelPostResponse>

    @POST("/messages/string")
    fun postChannelMessagePost(@Header(TOKEN) token: String, @Body message: SendMessageStringData, @Query("post") post: Boolean): Observable<ChannelPostResponse>

    /*FILES*/
    /**
     * [shareFile]
     * Can be used to share a single or multiple files
     * @Query selfDestruct -> Message self destruction after an ammount of time
     * @Query post -> Determines if this is a post on a channel
     * @Query schedule -> A channel File post can be scheduled to be posted in a date
     * @Query minutes -> Number of minutes before the message self destructs
     * */
    @POST("/messages/file")
    fun shareFile(@Header(TOKEN) token: String, @Body multipartBody: MultipartBody,
                  @Query("selfDestruct") selfDestruct: Boolean?, @Query("post") post: Boolean?,
                  @Query("schedule") schedule: Boolean?, @Query("minutes") minutes: Int?): Observable<FileResponse>

    @PATCH("/messages/{messageId}/favorite")
    fun favoriteMessagio(@Header(TOKEN) token: String, @Path("messageId") messageId: Int, @Body data: FavoriteData): Observable<ErrorResponse>

    @DELETE("/messages/{messageId}/user/{userId}")
    fun deleteMessagio(@Header(TOKEN) token: String, @Path("messageId") messageId: Int, @Path("userId") userId: Int): Observable<ErrorResponse>

    @GET("/rooms/{roomId}/favorite_messages/user/{userId}")
    fun getRoomFaveMsg(@Header(TOKEN) token: String, @Path("roomId") roomId: Int, @Path("userId") userId: Int) : Observable<FavoriteMessageResponse>

    /*CHANNEL*/
    //Create new channel
    @POST("/channels")
    fun postChannel(@Header(TOKEN) token: String, @Body channelPostData: ChannelPostData): Observable<ChannelResponse>

    //Get all user channels
    @GET("/users/{userId}/channels")
    fun getUserChannels(@Header(TOKEN) token: String, @Path("userId") userId: Int): Observable<MyChannelRoomsResponse>

    //Get channels I follow
    @GET("/users/{userId}/channels/follows/with_flags")
    fun getMyFollowedChannels(@Header(TOKEN) token: String, @Path("userId") userId: Int): Observable<ChannelFollowResponse>

    //Get All channel Posts
    @GET("/channels/{roomId}/user/{userId}/messages")
    fun getChannelPosts(@Header(TOKEN) token: String, @Path("roomId") roomId: Int,
                        @Path("userId") userId: Int, @Query("messageId") messageId: String): Observable<RoomHistoryResponse>

    @PATCH("/channels/{messageId}/like/{userId}/user")
    fun likePost(@Header(TOKEN) token: String, @Path("messageId") messageId: Int,
                 @Path("userId") userId: Int, @Query("unlike") unlike: Boolean?): Observable<LikeResponse>

    @GET("/channels/search")
    fun searchChannel(@Header(TOKEN) token: String, @Query("search") search: String?): Observable<SearchChannelsResponse>

    @PATCH("/channels/{channelId}/follow")
    fun followChannel(@Header(TOKEN) token: String, @Path("channelId") channelId: Int,
                      @Body userIds: MultipartBody): Observable<RoomResponse>

    /**
     * Used for leaving groupchat & un-following channel
     * */
    @DELETE("/groupChats/{roomId}/users/{userId}")
    fun unfollowChannel(@Header(TOKEN) token: String, @Path("roomId") roomId: Int,
                        @Path("userId") userId: Int): Observable<UnFollowChannelResponse>

    /**
     * [getPostComments]
     * Get last 40 comments from a Channel Post
     * @Query used for pagination
     * */
    @GET("/comments/{messageId}/user/{userId}")
    fun getPostComments(@Header(TOKEN) token: String, @Path("messageId") messageId: Int,
                        @Path("userId") userId: Int, @Query("commentId") commentId: String): Observable<CommentArrayResponse>

    /**
     * [commentOnChannelPost]
     * Post a comment in a Message flagged as a Post
     * */
    @POST("/comments/{messageId}")
    fun commentOnChannelPost(@Header(TOKEN) token: String, @Path("messageId") messageId: String,
                             @Body commentPostData: CommentPostData): Observable<CommentArrayResponse>

    /**
     * [updateChannel]
     * Update Channel with new info & new media
     * @param channelId
     * */
    @PATCH("/channels/{channelId}")
    fun updateChannel(@Header(TOKEN) token: String, @Path("channelId") channelId: Int,
                      @Body channelPostData: ChannelPostData): Observable<ChannelEditResponse>

    /**
     * [deleteChannel]
     * Delete Channel
     * @param channelId [Int]
     * */
    @DELETE("/channels/{channelId}")
    fun deleteChannel(@Header(TOKEN) token: String, @Path("channelId") channelId: Int): Observable<DeleteChannelResponse>


    /*EVENTS*/
    /**[postEvents]
     * @Post new event from current user
     * */
    @POST("/events")
    fun postEvents(@Header(TOKEN) token: String, @Body eventPostData: EventPostData): Observable<EventPostResponse>

    /**
     * [getUserEventsWithFlags]
     * @Get current user's events with flags
     * @created -> User created the event
     * @fullParticipant ->  User is able to send messages
     * @readOnly -> User can only read the event
     * */
    @GET("/users/{userId}/events/with_flags")
    fun getUserEventsWithFlags(@Header(TOKEN) token: String, @Path("userId") userId: Int): Observable<EventRetrievalResponse>

    /*PRIVATE CHATS*/

    /**
     * [getPrivateChats]
     * @Get current user's private chat
     * Should be cached at download.
     * */
    @GET("/users/{userId}/privateChats")
    fun getPrivateChats(@Header(TOKEN) token: String, @Path("userId") userId: Int): Observable<PrivateChatResponse>

    /**
     * [getChatHistory]
     * Used to retrieve all messages in a PrivateChat thread
     * */
    @GET("/privateChats/{roomId}/user/{userId}/messages")
    fun getChatHistory(@Header(TOKEN) token: String, @Path("roomId") roomId: Int, @Path("userId") userId: Int): Observable<RoomHistoryResponse>

    /*CONTACTS*/
    /**
     * [getEightContactsPhoneNumbers]
     * @Body Array of [LocalContact]
     * */
    @POST("/users/{userId}/check_contacts_use_eight")
    fun getEightContactsPhoneNumbers(@Header(TOKEN) token: String,
                                     @Path("userId") userId: Int,
                                     @Body contactPostData: Array<out Any>): Observable<ContactsPostResponse>

    /**
     * [inviteContactsToEight]
     * @Body Array of [LocalContact]
     * */
    @POST("/users/{userId}/invite_to_eight")
    fun inviteContactsToEight(@Header(TOKEN) token: String,
                              @Path("userId") userId: Int,
                              @Body contactsPostData: Array<out Any>): Observable<ContactsInvitedResponse>


    /*ROOM*/
    @PATCH("/privateChats/{roomId}/favorite")
    fun favoritePrivateChatRoom(@Header(TOKEN) token: String, @Path("roomId") roomId: Int,
                                @Body privateChatPatchData: PrivateChatPatchData): Observable<PrivateChatFavoriteResponse>

    @GET("/rooms/{roomId}")
    fun getRoomById(@Header(TOKEN) token: String, @Path("roomId") roomId: Int): Observable<RoomResponse>

    /*GROUP*/
    @POST("/groupChats")
    fun createGroupChat(@Header(TOKEN) token: String,
                        @Body data: GroupChatPostData): Observable<GroupChatPostResponse>

    /*NOTIFICATIONS*/

    /**
     * [updateReceipt]
     * Turns on or off read receipts for every chat the user is in
     * TODO: Implement this with UI changes & User Settings
     * */
    @PATCH("/users/{userId}/receipts")
    fun updateReceipt(@Header(TOKEN) token: String, @Path("userId") userId: Int,
                      @Body receiptData: ReceiptPatchData): Observable<User>


    /*ROOM CONTROL*/

    /**
     * [enterRoom]
     * Alerts the API the user has entered a Room
     * */
    @PATCH("/users/{userId}/enter/{roomId}")
    fun enterRoom(@Header(TOKEN) token: String, @Path("userId") userId: Int,
                  @Path("roomId") roomId: Int): Observable<EnterLeaveRoomResponse>

    /**
     * [leaveRoom]
     * Alerts the API the user has left a Room
     * */
    @PATCH("users/{userId}/leave/{roomId}")
    fun leaveRoom(@Header(TOKEN) token: String, @Path("userId") userId: Int,
                  @Path("roomId") roomId: Int): Observable<EnterLeaveRoomResponse>
}