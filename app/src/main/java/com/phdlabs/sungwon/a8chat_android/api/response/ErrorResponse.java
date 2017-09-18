package com.phdlabs.sungwon.a8chat_android.api.response;

import android.util.Log;

import com.phdlabs.sungwon.a8chat_android.api.utility.GsonHolder;

import retrofit2.Response;

/**
 * Created by SungWon on 9/18/2017.
 */

public class ErrorResponse extends StatusResponse {
    private String message;

    public ErrorResponse() {
    }

    public ErrorResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static ErrorResponse fromResponse(Response<?> response) {
        try {
            return GsonHolder.get().fromJson(response.errorBody().charStream(), ErrorResponse.class);
        } catch (Exception e) {
            Log.d("ErrorResponse", e.toString());
            return new ErrorResponse(e.getMessage());
        }
    }
}

