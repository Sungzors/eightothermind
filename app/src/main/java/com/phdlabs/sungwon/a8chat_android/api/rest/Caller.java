package com.phdlabs.sungwon.a8chat_android.api.rest;

import com.phdlabs.sungwon.a8chat_android.api.data.CommentPatchData;
import com.phdlabs.sungwon.a8chat_android.api.data.CommentPostData;
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
import com.phdlabs.sungwon.a8chat_android.api.response.ChannelFollowResponse;
import com.phdlabs.sungwon.a8chat_android.api.response.ChannelShowArrayResponse;
import com.phdlabs.sungwon.a8chat_android.api.response.ChatsRetrievalResponse;
import com.phdlabs.sungwon.a8chat_android.api.response.CommentArrayResponse;
import com.phdlabs.sungwon.a8chat_android.api.response.CommentResponse;
import com.phdlabs.sungwon.a8chat_android.api.response.ErrorResponse;
import com.phdlabs.sungwon.a8chat_android.api.response.EventRetrievalResponse;
import com.phdlabs.sungwon.a8chat_android.api.response.PrivateChatResponse;
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
import retrofit2.http.Query;

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

    @PATCH("/channels/{channelId}/follow")
    Call<RoomResponse> followChannel(@Header(TOKEN) String token, @Path("channelId") int channelId, @Body FollowUserData data);

    @GET("/channels/{roomId}/user/{userId}/messages")
    Call<RoomHistoryResponse> getChannelPosts(@Header(TOKEN) String token, @Path("roomId") int roomId, @Path("userId") int userId, @Query("messageId") String messageId);

    @PATCH("/channels/{messageId}/like/{userId}/user")
    Call<ErrorResponse> likePost(@Header(TOKEN) String token, @Path("messageId") int messageId, @Path("userId") int userId);

    @POST("/auth/verify")
    Call<TokenResponse> verify(@Body VerifyData verifyData);

    @POST("/auth/resend")
    Call<ResendResponse> resend(@Body LoginData loginData);

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

    @GET("/users/{userId}/privateChats")
    Call<PrivateChatResponse> getPrivateChats(@Header(TOKEN) String token, @Path("userId") int userid);

    @GET("/users/{userId}/groupChats")
    Call<UserDataResponse> getGroupChats(@Header(TOKEN) String token, @Path("userId") int userid);

    @GET("/users/{userId}/private&group_chats")
    Call<ChatsRetrievalResponse> getAllChats(@Header(TOKEN) String token, @Path("userId") int userid);

    @GET("/users/{userId}/channels")
    Call<ChannelShowArrayResponse> getAssociatedChannels(@Header(TOKEN) String token, @Path("userId") int userid);

//    @GET("/users/{userId}/events")
//    Call<> TODO:Event, talk to Tomer about wtf happened

    @GET("/users/{userId}/events/with_flags")
    Call<EventRetrievalResponse> getEvents(@Header(TOKEN) String token, @Path("userId") int userId);

    @GET("/users/{userId}/channels/follows")
    Call<ChannelFollowResponse> getFollowChannel(@Header(TOKEN) String token, @Path("userId") int userId);

    @POST("/comments/{messageId}")
    Call<CommentResponse> postComment(@Header(TOKEN) String token, @Path("messageId") int messageId, @Body CommentPostData data);

    @PATCH("/comments/{commentId}/edit")
    Call<CommentResponse> patchComment(@Header(TOKEN) String token, @Path("commentId") int commentId, @Body CommentPatchData data);

    @GET("/comments/{messageId}/user/{userId}")
    Call<CommentArrayResponse> getComments(@Header(TOKEN) String token, @Path("messageId") int messageId, @Path("userId") int userId, @Query("commentId") String commentId);

    @GET("/events/{roomId}/user/{userId}/messages")
    Call<RoomHistoryResponse> getEventHistory(@Header(TOKEN) String token, @Path("roomId") int roomId, @Path("userId") int userId);
}
