package com.phdlabs.sungwon.a8chat_android.api.rest

import com.phdlabs.sungwon.a8chat_android.api.data.*
import com.phdlabs.sungwon.a8chat_android.api.data.channel.BroadcastData
import com.phdlabs.sungwon.a8chat_android.api.data.channel.ChannelPostData
import com.phdlabs.sungwon.a8chat_android.api.data.channel.CommentPostData
import com.phdlabs.sungwon.a8chat_android.api.data.notifications.UserFBToken
import com.phdlabs.sungwon.a8chat_android.api.response.*
import com.phdlabs.sungwon.a8chat_android.api.response.channels.LikeResponse
import com.phdlabs.sungwon.a8chat_android.api.response.channels.MyChannelRoomsResponse
import com.phdlabs.sungwon.a8chat_android.api.response.channels.broadcast.EndBroadcastResponse
import com.phdlabs.sungwon.a8chat_android.api.response.channels.broadcast.StartBroadcastResponse
import com.phdlabs.sungwon.a8chat_android.api.response.channels.comments.PostCommentResponse
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
import com.phdlabs.sungwon.a8chat_android.api.response.eightEvents.EventResponse
import com.phdlabs.sungwon.a8chat_android.api.response.eightEvents.EventRetrievalResponse
import com.phdlabs.sungwon.a8chat_android.api.response.favorite.PrivateChatFavoriteResponse
import com.phdlabs.sungwon.a8chat_android.api.response.media.FileResponse
import com.phdlabs.sungwon.a8chat_android.api.response.media.MediaResponse
import com.phdlabs.sungwon.a8chat_android.api.response.messages.FavoriteMessageResponse
import com.phdlabs.sungwon.a8chat_android.api.response.messages.FavoriteSelfMessageResponse
import com.phdlabs.sungwon.a8chat_android.api.response.notifications.ClearBadgeCountResponse
import com.phdlabs.sungwon.a8chat_android.api.response.privateChat.PrivateChatResponse
import com.phdlabs.sungwon.a8chat_android.api.response.room.EnterLeaveRoomResponse
import com.phdlabs.sungwon.a8chat_android.api.response.user.GlobalSettingsResponse
import com.phdlabs.sungwon.a8chat_android.api.response.user.UserDataResponse
import com.phdlabs.sungwon.a8chat_android.api.rest.Caller.TOKEN
import com.phdlabs.sungwon.a8chat_android.model.room.Room
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

    /**
     * Update User Profile
     * */
    @PATCH("/users/{userId}")
    fun updateUser(@Header(TOKEN) token: String, @Path("userId") userId: Int, @Body userData: UserData): Observable<UserDataResponse>

    /**
     * Update Firebase Token
     * */
    @PATCH("/users/{userId}")
    fun updateFirebaseToken(@Header(TOKEN) token: String, @Path("userId") userId: Int, @Body userFBToken: UserFBToken): Observable<UserDataResponse>

    @GET("/users/{userId}")
    fun getUser(@Header(TOKEN) token: String, @Path("userId") userId: Int): Observable<UserDataResponse>

    @GET("/users/{userId}/friends")
    fun getUserFriends(@Header(TOKEN) token: String, @Path("userId") userId: Int): Observable<UserFriendsResponse>

    //TODO: Get user global settings


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

    /*If the client wishes to unfavorite a message, set the query parameter of unfavorite to true*/
    @PATCH("/messages/{messageId}/favorite")
    fun favoriteMessagio(@Header(TOKEN) token: String, @Path("messageId") messageId: Int, @Body data: FavoriteData, @Query("unfavorite") unfavorite: Boolean): Observable<StatusResponse>

    @DELETE("/messages/{messageId}/user/{userId}")
    fun deleteMessagio(@Header(TOKEN) token: String, @Path("messageId") messageId: Int, @Path("userId") userId: Int): Observable<ErrorResponse>

    @GET("/rooms/{roomId}/favorite_messages/user/{userId}")
    fun getRoomFaveMsg(@Header(TOKEN) token: String, @Path("roomId") roomId: Int, @Path("userId") userId: Int): Observable<FavoriteMessageResponse>

    @GET("/users/{userId}/messages/favorite")
    fun getUserFaveMsg(@Header(TOKEN) token: String, @Path("userId") userId: Int): Observable<FavoriteSelfMessageResponse>

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

    //Like post
    @PATCH("/channels/{messageId}/like/{userId}/user")
    fun likePost(@Header(TOKEN) token: String, @Path("messageId") messageId: Int,
                 @Path("userId") userId: Int, @Query("unlike") unlike: Boolean?): Observable<LikeResponse>

    //Search Channels
    @GET("/channels/search")
    fun searchChannel(@Header(TOKEN) token: String, @Query("search") search: String?): Observable<SearchChannelsResponse>

    //Follow Channel
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
                             @Body commentPostData: CommentPostData): Observable<PostCommentResponse>

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

    /**
     * [startBroadcast]
     * Start Live Broadcast
     * - This API call sends notifications to the channel followers & returns the message information
     * created on the Channel feed
     * - Connects the Sockets update-chat-broadcast for commenting on the live feed
     * */
    @POST("/messages/broadcast/start")
    fun startBroadcast(@Header(TOKEN) token: String, @Body broadcastData: BroadcastData, @Query("post") post: Boolean): Observable<StartBroadcastResponse>

    /**
     * [finishBroadcast]
     * - This API call finishes the live broadcast access
     * - Disconnects the Sockets update-chat-broadcast for commenting on the live feed
     * */
    @PATCH("/messages/{messageId}/broadcast/finish")
    fun finishBroadcast(@Header(TOKEN) token: String, @Path("messageId") messageId: String,
                        @Body broadcastData: BroadcastData): Observable<EndBroadcastResponse>

    /*EVENTS*/
    /**[postEvents]
     * @Post new event from current user
     * */
    @POST("/events")
    fun postEvents(@Header(TOKEN) token: String, @Body eventPostData: EventPostData): Observable<EventPostResponse>

    /**
     * [getEvents]
     * @Get all events to classify them locally
     * */
    @GET("/events")
    fun getEvents(@Header(TOKEN) token: String): Observable<EventResponse>

    /**
     * [getUserEvents]
     * @Get retrieves user created events
     * */
    @GET("/users/{userId}/events")
    fun getUserEvents(@Header(TOKEN) token: String,
                      @Path("userId") userId: Int,
                      @Query("availableToJoin") nearby: Boolean,
                      @Query("active") active: Boolean,
                      @Query("inactive") inactive: Boolean,
                      @Query("lat") lat: Double,
                      @Query("lng") lng: Double): Observable<EventRetrievalResponse>

/*NOT WORKING ON BACKEND*/
//    @GET("/events/nearby/{userId}/user")
//    fun getNearbyEvents(@Header(TOKEN) token: String, @Path("userId") userId: Int, @Query("lat") lat: Long, @Query("lng") lng: Long): Observable<EventNearbyResponse>

    /*PRIVATE CHATS*/

    /**
     * [getPrivateChats]
     * @Get current user's private chat
     * WARNING -> NOT CURRENTLY USED - IT DOESN'T CONFORM TO [Room] Model
     * */
    @GET("/users/{userId}/privateChats")
    fun getPrivateChats(@Header(TOKEN) token: String, @Path("userId") userId: Int): Observable<PrivateChatResponse>

    /**
     * [getPrivateAndGroupChats]
     * @Get current user's private chats & group chats
     * Used for current [Room] Model
     * */
    @GET("/users/{userId}/private&group_chats")
    fun getPrivateAndGroupChats(@Header(TOKEN) token: String, @Path("userId") userId: Int): Observable<ChatsRetrievalResponse>

    /**
     * [getChatHistory]
     * Used to retrieve all messages in a PrivateChat thread
     * */
    @GET("/privateChats/{roomId}/user/{userId}/messages")
    fun getChatHistory(@Header(TOKEN) token: String, @Path("roomId") roomId: Int, @Path("userId") userId: Int): Observable<RoomHistoryResponse>

    @GET("/privateChats/{roomId}/user/{userId}/messages")
    fun getChatHistory(@Header(TOKEN) token: String, @Path("roomId") roomId: Int, @Path("userId") userId: Int, @Query("messageId") messageId: Int): Observable<RoomHistoryResponse>

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

    /**
     * [sendPrivateChatMessage]
     * Send Message in a Private Chat between two users
     * */
    @POST("/messages/string")
    fun sendMessageString(@Header(TOKEN) token: String, @Body message: SendMessageStringData): Observable<ErrorResponse>


    /*ROOM*/
    @PATCH("/privateChats/{roomId}/favorite")
    fun favoritePrivateChatRoom(@Header(TOKEN) token: String, @Path("roomId") roomId: Int,
                                @Body privateChatPatchData: PrivateChatPatchData): Observable<PrivateChatFavoriteResponse>

    @GET("/rooms/{roomId}")
    fun getRoomById(@Header(TOKEN) token: String, @Path("roomId") roomId: Int): Observable<RoomResponse>

    @PATCH("/rooms/{roomId}/user/{userId}/notifications")
    fun toggleNotification(@Header(TOKEN) token: String, @Path("roomId") roomId: Int, @Path("userId") userId: Int, @Query("message_notification") notif: Boolean): Observable<ErrorResponse>

    /*GROUP*/
    @POST("/groupChats")
    fun createGroupChat(@Header(TOKEN) token: String,
                        @Body data: GroupChatPostData): Observable<GroupChatPostResponse>

    /*NOTIFICATIONS*/

    //TODO: Notification Settins on Each Settings Screen -> Channel, PrivateChat, GroupChat & Event

    /**
     * Clear Notification badge count when the user enteres the App
     * */
    @PATCH("/users/{userId}/clear_badge_count")
    fun clearNotificationBadgeCount(@Header(TOKEN) token: String, @Path("userId") userId: Int): Observable<ClearBadgeCountResponse>

    /**
     * [changeGlobalNotificationSettings]
     * Used to change global notifications settings from User Profile
     * All query parameters are Strings based on Boolean values -> true || false
     * @query messageNotifications
     * @query likeNotifications
     * @query commentNotifications
     * @query userAddedNotification
     * */
    @PATCH("/users/{userId}/notifications/global")
    fun changeGlobalNotificationSettings(@Header(TOKEN) token: String, @Path("userId") userId: Int,
                                         @Query("message_notifications") messageNotifications: String?,
                                         @Query("like_notifications") likeNotifications: String?,
                                         @Query("comment_notifications") commentNotifications: String?,
                                         @Query("user_added_notifications") userAddedNotification: String?): Observable<UserDataResponse>

    /**
     * []
     * Read user's global settings for caching
     * */
    @GET("/users/{userId}/settings/global")
    fun readGlobalNotificationSettings(@Header(TOKEN) token: String, @Path("userId") userId: Int): Observable<GlobalSettingsResponse>

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


    @GET("/users/{userId}/{roomId}/twilio/video/auth")
    fun getAccessTokenTwilio(@Header(TOKEN) token: String, @Path("userId") userId: Int,
                             @Path("roomId") roomId: Int): Observable<UserDataResponse>
}