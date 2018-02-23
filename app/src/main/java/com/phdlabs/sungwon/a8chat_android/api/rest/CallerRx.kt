package com.phdlabs.sungwon.a8chat_android.api.rest

import com.phdlabs.sungwon.a8chat_android.api.data.*
import com.phdlabs.sungwon.a8chat_android.api.response.createEvent.EventPostResponse
import com.phdlabs.sungwon.a8chat_android.api.response.createChannel.ChannelResponse
import com.phdlabs.sungwon.a8chat_android.api.response.MediaResponse
import com.phdlabs.sungwon.a8chat_android.api.response.UserDataResponse
import com.phdlabs.sungwon.a8chat_android.api.response.contacts.ContactsInvitedResponse
import com.phdlabs.sungwon.a8chat_android.api.response.contacts.ContactsPostResponse
import com.phdlabs.sungwon.a8chat_android.api.response.contacts.UserFriendsResponse
import com.phdlabs.sungwon.a8chat_android.api.response.favorite.PrivateChatFavoriteResponse
import com.phdlabs.sungwon.a8chat_android.api.rest.Caller.TOKEN
import com.phdlabs.sungwon.a8chat_android.model.user.registration.RegistrationData
import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.http.*

/**
 * Created by paix on 12/20/17.
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

    /*CHANNEL*/
    @POST("/channels")
    fun postChannel(@Header(TOKEN) token: String, @Body channelPostData: ChannelPostData): Observable<ChannelResponse>

    /*EVENTS*/
    @POST("/events")
    fun postEvents(@Header(TOKEN) token: String, @Body eventPostData: EventPostData): Observable<EventPostResponse>

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

}