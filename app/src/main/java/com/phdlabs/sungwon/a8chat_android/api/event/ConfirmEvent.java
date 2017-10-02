package com.phdlabs.sungwon.a8chat_android.api.event;

/**
 * Created by SungWon on 9/26/2017.
 */

public class ConfirmEvent extends Event {

    public ConfirmEvent() {
    }

    public ConfirmEvent(@SuccessCode int successCode, String key) {
        super(successCode, key);
    }

    public ConfirmEvent(String errorMessage) {
        super(errorMessage);
    }
}
