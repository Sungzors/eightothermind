package com.phdlabs.sungwon.a8chat_android.api.utility

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

/**
 * Created by JPAM on 12/20/17.
 * Headers Interceptor for HttpManager
 */
class HeadersInterceptor : Interceptor {

    companion object {

        /*Instance*/
        var instance: HeadersInterceptor = HeadersInterceptor()
            private set
        var CONTENT_TYPE: String = "Content-Type"
        var APPLICATION_JSON: String = "application/json"
    }

    override fun intercept(chain: Interceptor.Chain?): Response? {
        val request: Request? = chain?.request()
        val requestBuilder: Request.Builder? = request?.newBuilder()
        requestBuilder?.let {
            it.addHeader(CONTENT_TYPE, APPLICATION_JSON)
            return chain.proceed(it.build())
        }
        return null
    }
}