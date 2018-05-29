package com.phdlabs.sungwon.a8chat_android.api.rest;

import com.phdlabs.sungwon.a8chat_android.api.utility.HttpManager;

import io.reactivex.internal.functions.Functions;
import io.reactivex.plugins.RxJavaPlugins;

/**
 * Created by SungWon on 9/18/2017.
 * Updated by JPAM 12/20/2017
 */

public class Rest {
    //caller manager
    private static Rest mInstance = new Rest();
    private Caller mCaller;
    private CallerRx mCallerRx;

    public static Rest getInstance() {
        return mInstance;
    }

    private Rest() {

    }

    public Caller getCaller() {
        if (mCaller == null) {
            mCaller = HttpManager.Companion.getInstance().getRetrofit().create(Caller.class);
        }
        return mCaller;
    }

    public CallerRx getmCallerRx() {
        if (mCallerRx == null) {
            mCallerRx = HttpManager.Companion.getInstance().getRetrofitRx().create(CallerRx.class);
        }
        //Avoid memory exceptions for low RAM memory phones
        RxJavaPlugins.setErrorHandler(Functions.<Throwable>emptyConsumer());
        return mCallerRx;
    }
}
