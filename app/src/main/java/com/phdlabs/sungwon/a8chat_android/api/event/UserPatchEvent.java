package com.phdlabs.sungwon.a8chat_android.api.event;

/**
 * Created by SungWon on 10/9/2017.
 */

public class UserPatchEvent extends Event {

    public UserPatchEvent() {
    }

    public UserPatchEvent(@SuccessCode int successCode, String key) {
        super(successCode, key);
    }

    public UserPatchEvent(String errorMessage) {
        super(errorMessage);
    }
}
