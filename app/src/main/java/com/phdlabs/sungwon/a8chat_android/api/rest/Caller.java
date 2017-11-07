package com.phdlabs.sungwon.a8chat_android.api.rest;

import com.phdlabs.sungwon.a8chat_android.api.data.LoginData;
import com.phdlabs.sungwon.a8chat_android.api.data.PrivateChatCreateData;
import com.phdlabs.sungwon.a8chat_android.api.data.PrivateChatPatchData;
import com.phdlabs.sungwon.a8chat_android.api.data.SendMessageStringData;
import com.phdlabs.sungwon.a8chat_android.api.data.UserData;
import com.phdlabs.sungwon.a8chat_android.api.data.VerifyData;
import com.phdlabs.sungwon.a8chat_android.api.response.ErrorResponse;
import com.phdlabs.sungwon.a8chat_android.api.response.MediaResponse;
import com.phdlabs.sungwon.a8chat_android.api.response.ResendResponse;
import com.phdlabs.sungwon.a8chat_android.api.response.RoomHistoryResponse;
import com.phdlabs.sungwon.a8chat_android.api.response.RoomResponse;
import com.phdlabs.sungwon.a8chat_android.api.response.TokenResponse;
import com.phdlabs.sungwon.a8chat_android.api.response.UserDataResponse;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by SungWon on 9/18/2017.
 *
 * https://eight-backend.herokuapp.com/swagger/
 */

public interface Caller {
    //api endpoint holder

    final String TOKEN = "Authorization";

    @POST("/users")
    Call<UserDataResponse> login(@Body LoginData loginData);

    @GET("/users/{userid}")
    Call<UserDataResponse> getUser(@Header(TOKEN) String token, @Path("userid") int userid);

    @PATCH("/users/{userid}")
    Call<UserDataResponse> updateUser(@Header(TOKEN) String token, @Path("userid") int userid, @Body UserData userData);

    @POST("/auth/verify")
    Call<TokenResponse> verify(@Body VerifyData verifyData);

    @POST("/auth/resend")
    Call<ResendResponse> resend(@Body LoginData loginData);

    @POST("/media")
    Call<MediaResponse> userPicPost(@Header(TOKEN) String token, @Body RequestBody data);

    @POST("/privateChats")
    Call<RoomResponse> createPrivateChatRoom(@Header(TOKEN) String token, @Body PrivateChatCreateData body);

    @DELETE("/privateChats/{roomId}")
    Call<RoomResponse> deletePrivateChatRoom(@Header(TOKEN) String token, @Path("roomId") int roomId);

    @PATCH("/privateChats/{roomId}/favorite")
    Call<RoomResponse> favoritePrivateChatRoom(@Header(TOKEN) String token, @Path("roomId") int roomId, @Body PrivateChatPatchData body);

    @PATCH("/privateChats/{roomId}/incognito_mode")
    Call<RoomResponse> hidePrivateChatRoom(@Header(TOKEN) String token, @Path("roomId") int roomId, @Body PrivateChatPatchData body);

    @GET("/privateChats/{roomId}/user/{userId}/messages")
    Call<RoomHistoryResponse> getMessageHistory(@Header(TOKEN) String token, @Path("roomId") int roomId, @Path("userId") int userId);

    @POST("/messages/string")
    Call<ErrorResponse> sendMessageString(@Header(TOKEN)String token, @Body SendMessageStringData data);


}
