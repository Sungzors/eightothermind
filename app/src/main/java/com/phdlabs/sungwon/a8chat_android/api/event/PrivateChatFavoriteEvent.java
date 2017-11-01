package com.phdlabs.sungwon.a8chat_android.api.event;

/**
 * Created by SungWon on 10/30/2017.
 */

public class PrivateChatFavoriteEvent extends Event {
    public PrivateChatFavoriteEvent() {
    }

    public PrivateChatFavoriteEvent(@SuccessCode int successCode, String key) {
        super(successCode, key);
    }

    public PrivateChatFavoriteEvent(String errorMessage) {
        super(errorMessage);
    }
}
