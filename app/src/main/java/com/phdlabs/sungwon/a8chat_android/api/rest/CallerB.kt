package com.phdlabs.sungwon.a8chat_android.api.rest

import com.phdlabs.sungwon.a8chat_android.api.data.LoginData
import com.phdlabs.sungwon.a8chat_android.model.user.User
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Created by paix on 12/20/17.
 */
interface CallerB {

    @POST("/users")
    fun login(@Body user: User): Observable<User>


//    @POST("/users")
//    Call<UserDataResponse> login(@Body LoginData loginData);
//
//    @GET("/users/{userid}")
//    Call<UserDataResponse> getUser(@Header(TOKEN) String token, @Path("userid") int userid);
//
//    @PATCH("/users/{userid}")
//    Call<UserDataResponse> updateUser(@Header(TOKEN) String token, @Path("userid") int userid, @Body UserData userData);

}