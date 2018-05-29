package com.phdlabs.sungwon.a8chat_android.api.event;

/**
 * Created by SungWon on 12/5/2017.
 */

public class PostLikeEvent extends Event {

    public PostLikeEvent() {
    }

    public PostLikeEvent(int successCode, String key) {
        super(successCode, key);
    }

    public PostLikeEvent(String errorMessage) {
        super(errorMessage);
    }
}
