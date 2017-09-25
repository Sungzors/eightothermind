package com.phdlabs.sungwon.a8chat_android.api.rest;

import com.phdlabs.sungwon.a8chat_android.api.data.LoginData;
import com.phdlabs.sungwon.a8chat_android.api.response.UserDataResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by SungWon on 9/18/2017.
 */

public interface Caller {
    //api endpoint holder

    final String TOKEN = "x-access-token";

    @POST("/users")
    Call<UserDataResponse> login(@Body LoginData loginData);
}
