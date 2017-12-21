package com.phdlabs.sungwon.a8chat_android.api.response;

import com.phdlabs.sungwon.a8chat_android.model.user.User;

/**
 * Created by SungWon on 9/21/2017.
 */

public class UserDataResponse extends ErrorResponse {

    private User user;

    public User getUser(){
        return user;
    }
}
