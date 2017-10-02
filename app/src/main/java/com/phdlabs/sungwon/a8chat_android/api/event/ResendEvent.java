package com.phdlabs.sungwon.a8chat_android.api.event;

/**
 * Created by SungWon on 9/27/2017.
 */

public class ResendEvent extends Event{

    public ResendEvent() {
    }

    public ResendEvent(@SuccessCode int successCode, String key) {
        super(successCode, key);
    }

    public ResendEvent(String errorMessage) {
        super(errorMessage);
    }
}
