package com.phdlabs.sungwon.a8chat_android.api.event;

/**
 * Created by SungWon on 11/10/2017.
 */

public class MessageLocationSentEvent extends Event {

    public MessageLocationSentEvent() {
    }

    public MessageLocationSentEvent(int successCode, String key) {
        super(successCode, key);
    }

    public MessageLocationSentEvent(String errorMessage) {
        super(errorMessage);
    }
}
