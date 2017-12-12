package com.phdlabs.sungwon.a8chat_android.api.event;

/**
 * Created by SungWon on 12/6/2017.
 */

public class ChannelGetEvent extends Event {
    public ChannelGetEvent() {
    }

    public ChannelGetEvent(int successCode, String key) {
        super(successCode, key);
    }

    public ChannelGetEvent(String errorMessage) {
        super(errorMessage);
    }
}
