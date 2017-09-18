package com.phdlabs.sungwon.a8chat_android.api.utility;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by SungWon on 9/18/2017.
 */

public class HttpManager {
    private static HttpManager mInstance = new HttpManager();

    private final Retrofit mRetrofit;
    private final OkHttpClient mHttpClient;

    public static HttpManager getInstance() {
        return mInstance;
    }

    private HttpManager(){
        mHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new HeadersInterceptor())
                .addInterceptor(new HttpLoggingInterceptor()
                        .setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'hh:mm:ssZ").create();//2017-05-08T14:24:08+0000
        mRetrofit = new Retrofit.Builder()
                //TODO: generate with baseurl
//                .baseUrl(BuildConfig.BASE_URL)
                .client(mHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public Retrofit getRetrofit() {
        return mRetrofit;
    }

    public OkHttpClient getHttpClient() {
        return mHttpClient;
    }
}
