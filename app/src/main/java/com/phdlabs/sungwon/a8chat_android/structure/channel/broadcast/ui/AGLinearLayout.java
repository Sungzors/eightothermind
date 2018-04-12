package com.phdlabs.sungwon.a8chat_android.structure.channel.broadcast.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Created by JPAM on 4/11/18.
 * Obtained from Agora.io Example App -> Android Open Live
 */
public class AGLinearLayout extends LinearLayout {
    public AGLinearLayout(Context context) {
        super(context);
    }

    public AGLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public AGLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(@NonNull MotionEvent event) {
        return ViewUtil.checkDoubleTouchEvent(event, this) || super.dispatchTouchEvent(event);
    }

    @Override
    public boolean dispatchKeyEvent(@NonNull KeyEvent event) {
        return ViewUtil.checkDoubleKeyEvent(event, this) || super.dispatchKeyEvent(event);
    }
}