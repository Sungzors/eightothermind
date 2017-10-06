package com.phdlabs.sungwon.a8chat_android.api.event;

/**
 * Created by SungWon on 10/4/2017.
 */

public class MediaEvent extends Event {

    public MediaEvent() {
    }

    public MediaEvent(@SuccessCode int successCode, String key) {
        super(successCode, key);
    }

    public MediaEvent(String errorMessage) {
        super(errorMessage);
    }
}
