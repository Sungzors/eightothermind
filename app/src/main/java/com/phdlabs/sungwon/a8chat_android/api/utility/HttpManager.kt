package com.phdlabs.sungwon.a8chat_android.api.utility

import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.phdlabs.sungwon.a8chat_android.BuildConfig
import io.realm.RealmObject
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Updated by JPAM on 12/20/2017
 *
 * Info:
 * Gson exception implemented for Realm mapping
 *
 * HTTP/2 support allows all requests to the same host to share a socket.
 * Connection pooling reduces request latency (if HTTP/2 isn’t available).
 * Transparent GZIP shrinks download sizes.
 * Response caching avoids the network completely for repeat requests.
 *
 * Source: http://square.github.io/okhttp/
 */

class HttpManager {

    /*Singleton*/
    companion object {
        var instance: HttpManager = HttpManager()
        private set
    }

    /*Properties*/
    private var retrofit: Retrofit? = null
    private var retrofitRx: Retrofit? = null
    private var httpClient: OkHttpClient? = null

    /*Initialization*/
    init {

        /*Http Client*/
        httpClient = OkHttpClient
                .Builder()
                .addInterceptor(HeadersInterceptor.instance)
                .addInterceptor(HttpLoggingInterceptor()
                        .setLevel(HttpLoggingInterceptor.Level.BODY))
                .build()

        /*Gson serialization with RealmObject exclusion strategy*/
        val gson: Gson = GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'hh:mm:ssZ")
                .setExclusionStrategies(object : ExclusionStrategy {

                    override fun shouldSkipClass(clazz: Class<*>?): Boolean = false

                    override fun shouldSkipField(f: FieldAttributes?): Boolean =
                            f?.declaredClass == RealmObject::class.java
                }).create()

        /*Retrofit Rx*/
        httpClient?.let {

            retrofitRx =  Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .baseUrl(BuildConfig.BASE_URL)
                    .client(it)
                    .build()

            /*Retrofit*/
            retrofit = Retrofit
                    .Builder()
                    .baseUrl(BuildConfig.BASE_URL)
                    .client(it)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()

        }
    }

    /*Getters*/
    fun getRetrofit(): Retrofit? = retrofit

    fun getRetrofitRx(): Retrofit? = retrofitRx

    fun getHttpClient(): OkHttpClient? = httpClient

}