package com.phdlabs.sungwon.a8chat_android.api.rest;

import com.phdlabs.sungwon.a8chat_android.api.utility.HttpManager;

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
        if(mCallerRx == null) {
            mCallerRx = HttpManager.Companion.getInstance().getRetrofitRx().create(CallerRx.class);
        }
        return mCallerRx;
    }
}
