package com.phdlabs.sungwon.a8chat_android.api.event;

/**
 * Created by SungWon on 11/3/2017.
 */

public class MessageSentEvent extends Event {

    public MessageSentEvent() {
    }

    public MessageSentEvent(@SuccessCode int successCode, String key) {
        super(successCode, key);
    }

    public MessageSentEvent(String errorMessage) {
        super(errorMessage);
    }
}
