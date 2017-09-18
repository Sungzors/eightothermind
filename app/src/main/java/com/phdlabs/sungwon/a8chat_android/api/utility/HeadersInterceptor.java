package com.phdlabs.sungwon.a8chat_android.api.utility;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by SungWon on 9/18/2017.
 */

public class HeadersInterceptor implements Interceptor {

    public static final String CONTENT_TYPE = "Content-Type";
    public static final String APPLICATION_JSON = "application/json";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request.Builder builder = request.newBuilder();
        builder.addHeader(CONTENT_TYPE, APPLICATION_JSON);
        return chain.proceed(builder.build());
    }
}
