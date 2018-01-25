package com.phdlabs.sungwon.a8chat_android.api.rest

import com.phdlabs.sungwon.a8chat_android.api.data.UserData
import com.phdlabs.sungwon.a8chat_android.api.response.MediaResponse
import com.phdlabs.sungwon.a8chat_android.api.response.UserDataResponse
import com.phdlabs.sungwon.a8chat_android.api.rest.Caller.TOKEN
import com.phdlabs.sungwon.a8chat_android.model.user.registration.RegistrationData
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

/**
 * Created by paix on 12/20/17.
 * API calls using RXJava with purpose of User Mapping & Local Cache system
 */
interface CallerRx {

    /*User Observable functions for Retrofit, Kotlin & Rx mapping Realm*/
    @POST("/users")
    fun login(@Body registrationData: RegistrationData): Observable<UserDataResponse>

    @PATCH("/users/{userid}")
    fun updateUser(@Header(TOKEN) token: String, @Path("userid") userId: Int, @Body userData: UserData): Observable<UserDataResponse>

    @GET("/users/{userid}")
    fun getUser(@Header(TOKEN) token: String, @Path("userid") userId: Int): Observable<UserDataResponse>

    /*Media*/
    @POST("/media")
    fun userPostPic(@Header(TOKEN) token: String, @Body data: RequestBody): Observable<MediaResponse>

    @Multipart
    @POST("/media")
    fun uploadMedia(@Header(TOKEN) token: String, @Part image: MultipartBody.Part): Observable<MediaResponse>

}