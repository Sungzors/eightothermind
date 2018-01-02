package com.phdlabs.sungwon.a8chat_android.api.event;

/**
 * Created by SungWon on 12/30/2017.
 */

public class ChannelFollowEvent extends Event {

    public ChannelFollowEvent() {
    }

    public ChannelFollowEvent(int successCode, String key) {
        super(successCode, key);
    }

    public ChannelFollowEvent(String errorMessage) {
        super(errorMessage);
    }
}
