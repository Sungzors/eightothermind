package com.phdlabs.sungwon.a8chat_android.api.rest;

/**
 * Created by SungWon on 9/18/2017.
 */

public class Rest {
    //caller manager
    private static Rest mInstance = new Rest();
    private Caller mCaller;

    public static Rest getInstance(){
        return mInstance;
    }

    private Rest(){

    }

    public Caller getCaller(){
        if(mCaller == null){
            mCaller = HttpManager.getInstance().getRetrofit().create(Caller.class);
        }
        return mCaller;
    }
}
