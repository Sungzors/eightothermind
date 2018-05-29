package com.phdlabs.sungwon.a8chat_android.api.event;

/**
 * Created by SungWon on 12/13/2017.
 */

public class ChannelPostGetEvent extends Event {
    public ChannelPostGetEvent() {
    }

    public ChannelPostGetEvent(int successCode, String key) {
        super(successCode, key);
    }

    public ChannelPostGetEvent(String errorMessage) {
        super(errorMessage);
    }
}
