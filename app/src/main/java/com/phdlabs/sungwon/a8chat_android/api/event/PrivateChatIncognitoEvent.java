package com.phdlabs.sungwon.a8chat_android.api.event;

/**
 * Created by SungWon on 10/30/2017.
 */

public class PrivateChatIncognitoEvent extends Event {
    public PrivateChatIncognitoEvent() {
    }

    public PrivateChatIncognitoEvent(@SuccessCode int successCode, String key) {
        super(successCode, key);
    }

    public PrivateChatIncognitoEvent(String errorMessage) {
        super(errorMessage);
    }
}
