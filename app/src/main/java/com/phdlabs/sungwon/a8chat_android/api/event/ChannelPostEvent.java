package com.phdlabs.sungwon.a8chat_android.api.event;

/**
 * Created by SungWon on 11/30/2017.
 */

public class ChannelPostEvent extends Event {
    public ChannelPostEvent() {
    }

    public ChannelPostEvent(int successCode, String key) {
        super(successCode, key);
    }

    public ChannelPostEvent(String errorMessage) {
        super(errorMessage);
    }
}
