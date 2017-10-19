package com.phdlabs.sungwon.a8chat_android.utility;

import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

import com.github.glomadrian.codeinputlib.CodeInput;

/**
 * Created by SungWon on 10/17/2017.
 */

public class CustomCodeInput extends CodeInput {

    public CustomCodeInput(Context context) {
        super(context);
    }

    public CustomCodeInput(Context context, AttributeSet attributeset) {
        super(context, attributeset);
    }

    public CustomCodeInput(Context context, AttributeSet attributeset, int defStyledAttrs) {
        super(context, attributeset, defStyledAttrs);
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        outAttrs.actionLabel = null;
        outAttrs.inputType = InputType.TYPE_CLASS_PHONE;
        outAttrs.imeOptions = EditorInfo.IME_ACTION_DONE;
        return new BaseInputConnection(this, false);
    }
}
