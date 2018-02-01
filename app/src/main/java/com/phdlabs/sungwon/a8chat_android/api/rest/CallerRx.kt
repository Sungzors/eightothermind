package com.phdlabs.sungwon.a8chat_android.api.rest

import com.phdlabs.sungwon.a8chat_android.api.data.ChannelPostData
import com.phdlabs.sungwon.a8chat_android.api.data.EventPostData
import com.phdlabs.sungwon.a8chat_android.api.data.UserData
import com.phdlabs.sungwon.a8chat_android.api.response.createEvent.EventPostResponse
import com.phdlabs.sungwon.a8chat_android.api.response.createChannel.ChannelResponse
import com.phdlabs.sungwon.a8chat_android.api.response.MediaResponse
import com.phdlabs.sungwon.a8chat_android.api.response.UserDataResponse
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

}