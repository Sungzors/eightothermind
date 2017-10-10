package com.phdlabs.sungwon.a8chat_android.utility;

/**
 * Created by SungWon on 10/9/2017.
 */

public interface Function<Input, Result> {
    Result apply(Input inputValue);
}
