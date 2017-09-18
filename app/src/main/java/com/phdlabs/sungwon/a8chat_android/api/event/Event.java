package com.phdlabs.sungwon.a8chat_android.api.event;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by SungWon on 9/18/2017.
 */

public abstract class Event {

    public static final int SUCCESS = 0;
    public static final int ERROR = 1;

    @IntDef({SUCCESS, ERROR})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SuccessCode{

    }

    private boolean isSuccess;
    private String key;
    private String errorMessage;

    public Event(){
        isSuccess = true;
        key = "";
    }

    //Event constructor for Events requiring key. 0 for success, 1 for error, String for the Key
    public Event(@SuccessCode int successCode, String key){
        if(successCode == Event.SUCCESS){
            isSuccess = true;
            this.key = key;
        } else if (successCode == Event.ERROR){
            isSuccess = false;
            this.key = key;
        }
    }

    public Event(String errorMessage){
        this.errorMessage = errorMessage;
        isSuccess = false;
        key = "";
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
