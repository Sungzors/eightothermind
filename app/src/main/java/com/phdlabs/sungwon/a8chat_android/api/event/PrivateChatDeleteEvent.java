package com.phdlabs.sungwon.a8chat_android.api.event;

/**
 * Created by SungWon on 10/30/2017.
 */

public class PrivateChatDeleteEvent extends Event {

    public PrivateChatDeleteEvent() {
    }

    public PrivateChatDeleteEvent(@SuccessCode int successCode, String key) {
        super(successCode, key);
    }

    public PrivateChatDeleteEvent(String errorMessage) {
        super(errorMessage);
    }
}
