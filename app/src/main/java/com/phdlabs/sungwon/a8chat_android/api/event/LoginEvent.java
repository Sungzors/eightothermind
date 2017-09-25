package com.phdlabs.sungwon.a8chat_android.api.event;

/**
 * Created by SungWon on 9/22/2017.
 */

public class LoginEvent extends Event {

    public LoginEvent() {
    }

    public LoginEvent(@SuccessCode int successCode, String key) {
        super(successCode, key);
    }

    public LoginEvent(String errorMessage) {
        super(errorMessage);
    }
}
