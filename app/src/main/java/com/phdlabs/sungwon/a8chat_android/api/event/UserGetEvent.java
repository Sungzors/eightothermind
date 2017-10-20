package com.phdlabs.sungwon.a8chat_android.api.event;

/**
 * Created by SungWon on 10/19/2017.
 */

public class UserGetEvent extends Event {

    public UserGetEvent() {
    }

    public UserGetEvent(@SuccessCode int successCode, String key) {
        super(successCode, key);
    }

    public UserGetEvent(String errorMessage) {
        super(errorMessage);
    }
}
