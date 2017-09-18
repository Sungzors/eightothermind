package com.phdlabs.sungwon.a8chat_android.api.response;

/**
 * Created by SungWon on 9/18/2017.
 */

public class StatusResponse {

    public static final String STATUS_ERROR = "false";
    public static final String STATUS_SUCCESS = "true";

    private String success;

    public String getStatus() {
        return success;
    }

    public boolean isError() {
        return STATUS_ERROR.equals(success);
    }

    public boolean isSuccess() {
        return STATUS_SUCCESS.equals(success);
    }
}
