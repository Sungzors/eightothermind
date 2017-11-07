package com.phdlabs.sungwon.a8chat_android.api.event;

/**
 * Created by SungWon on 11/6/2017.
 */

public class RoomHistoryEvent extends Event {

    public RoomHistoryEvent() {
    }

    public RoomHistoryEvent(@SuccessCode int successCode, String key) {
        super(successCode, key);
    }

    public RoomHistoryEvent(String errorMessage) {
        super(errorMessage);
    }
}
