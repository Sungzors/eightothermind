package com.phdlabs.sungwon.a8chat_android.api.event;

/**
 * Created by SungWon on 10/30/2017.
 */

public class PrivateChatCreateEvent extends Event {

    public PrivateChatCreateEvent() {
    }

    public PrivateChatCreateEvent(@SuccessCode int successCode, String key) {
        super(successCode, key);
    }

    public PrivateChatCreateEvent(String errorMessage) {
        super(errorMessage);
    }
}
