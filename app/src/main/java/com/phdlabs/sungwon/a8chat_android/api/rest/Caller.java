package com.phdlabs.sungwon.a8chat_android.api.rest;

import com.phdlabs.sungwon.a8chat_android.api.data.channel.CommentPatchData;
import com.phdlabs.sungwon.a8chat_android.api.data.channel.CommentPostData;
import com.phdlabs.sungwon.a8chat_android.api.data.FollowUserData;
import com.phdlabs.sungwon.a8chat_android.api.data.LoginData;
import com.phdlabs.sungwon.a8chat_android.api.data.PrivateChatCreateData;
import com.phdlabs.sungwon.a8chat_android.api.data.PrivateChatPatchData;
import com.phdlabs.sungwon.a8chat_android.api.data.SendMessageChannelData;
import com.phdlabs.sungwon.a8chat_android.api.data.SendMessageContactData;
import com.phdlabs.sungwon.a8chat_android.api.data.SendMessageGeneralData;
import com.phdlabs.sungwon.a8chat_android.api.data.SendMessageMoneyData;
import com.phdlabs.sungwon.a8chat_android.api.data.SendMessageStringData;
import com.phdlabs.sungwon.a8chat_android.api.data.VerifyData;
import com.phdlabs.sungwon.a8chat_android.api.response.ChannelArrayResponse;
import com.phdlabs.sungwon.a8chat_android.api.response.ChatsRetrievalResponse;
import com.phdlabs.sungwon.a8chat_android.api.response.CommentResponse;
import com.phdlabs.sungwon.a8chat_android.api.response.ErrorResponse;
import com.phdlabs.sungwon.a8chat_android.api.response.privateChat.PrivateChatResponse;
import com.phdlabs.sungwon.a8chat_android.api.response.ResendResponse;
import com.phdlabs.sungwon.a8chat_android.api.response.RoomHistoryResponse;
import com.phdlabs.sungwon.a8chat_android.api.response.RoomResponse;
import com.phdlabs.sungwon.a8chat_android.api.response.TokenResponse;
import com.phdlabs.sungwon.a8chat_android.api.response.user.UserDataResponse;

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
 * <p>
 * https://eight-backend.herokuapp.com/swagger/
 */

public interface Caller {
    //api endpoint holder

    final String TOKEN = "Authorization";

    //get channels only for testing
    @GET("/channels")
    Call<ChannelArrayResponse> getChannel(@Header(TOKEN) String token);

    @POST("/auth/verify")
    Call<TokenResponse> verify(@Body VerifyData verifyData);

    @POST("/auth/resend")
    Call<ResendResponse> resend(@Body LoginData loginData);

    @POST("/privateChats")
    Call<RoomResponse> createPrivateChatRoom(@Header(TOKEN) String token, @Body PrivateChatCreateData body);

    @DELETE("/privateChats/{roomId}")
    Call<RoomResponse> deletePrivateChatRoom(@Header(TOKEN) String token, @Path("roomId") int roomId);

    @PATCH("/privateChats/{roomId}/incognito_mode")
    Call<RoomResponse> hidePrivateChatRoom(@Header(TOKEN) String token, @Path("roomId") int roomId, @Body PrivateChatPatchData body);


    @GET("/groupChats/{roomId}/user/{userId}/messages")
    Call<RoomHistoryResponse> getGCMessageHistory(@Header(TOKEN) String token, @Path("roomId") int roomId, @Path("userId") int userId);

    @POST("/messages/string")
    Call<ErrorResponse> sendMessageString(@Header(TOKEN) String token, @Body SendMessageStringData data);

    @POST("/messages/media")
    Call<ErrorResponse> sendMessageMedia(@Header(TOKEN) String token, @Body RequestBody data);

    @POST("/messages/money")
    Call<ErrorResponse> sendMessageMoney(@Header(TOKEN) String token, @Body SendMessageMoneyData data);

    @POST("/messages/share/location")
    Call<ErrorResponse> sendMessageLocation(@Header(TOKEN) String token, @Body SendMessageGeneralData data);

    @POST("/messages/share/contact")
    Call<ErrorResponse> sendMessageContact(@Header(TOKEN) String token, @Body SendMessageContactData data);

    @POST("/messages/share/channel")
    Call<ErrorResponse> sendMessageChannel(@Header(TOKEN) String token, @Body SendMessageChannelData data);

    @POST("/messages/share/groupChat")
    Call<ErrorResponse> sendMessageGroupChat(@Header(TOKEN) String token, @Body SendMessageContactData data);

    @GET("/users/{userId}/groupChats")
    Call<UserDataResponse> getGroupChats(@Header(TOKEN) String token, @Path("userId") int userid);

    @GET("/users/{userId}/private&group_chats")
    Call<ChatsRetrievalResponse> getAllChats(@Header(TOKEN) String token, @Path("userId") int userid);


    @GET("/events/{roomId}/user/{userId}/messages")
    Call<RoomHistoryResponse> getEventHistory(@Header(TOKEN) String token, @Path("roomId") int roomId, @Path("userId") int userId);
}
