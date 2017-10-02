package com.phdlabs.sungwon.a8chat_android.api.rest;

import com.phdlabs.sungwon.a8chat_android.api.data.LoginData;
import com.phdlabs.sungwon.a8chat_android.api.data.VerifyData;
import com.phdlabs.sungwon.a8chat_android.api.response.ResendResponse;
import com.phdlabs.sungwon.a8chat_android.api.response.TokenResponse;
import com.phdlabs.sungwon.a8chat_android.api.response.UserDataResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by SungWon on 9/18/2017.
 */

public interface Caller {
    //api endpoint holder

    final String TOKEN = "Authorization";

    @POST("/users")
    Call<UserDataResponse> login(@Body LoginData loginData);

    @POST("/auth/verify")
    Call<TokenResponse> verify(@Body VerifyData verifyData);

    @POST("/auth/resend")
    Call<ResendResponse> resend(@Body LoginData loginData);
}
