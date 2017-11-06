package com.mask.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;

import com.mask.monitor.OnKeyDownListener;

/**
 * 作者：omni20170501
 */

@SuppressLint("AppCompatCustomView")
public class ListenerKeyBackEditText extends EditText {
    public static int i = 0;
    OnKeyDownListener onKeyDownListener;
    public ListenerKeyBackEditText(Context context) {
        super(context);
    }


    public ListenerKeyBackEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == 1) {
            Log.i("main_activity", "键盘向下 ");
            if (onKeyDownListener!=null){
                onKeyDownListener.OnKeyDown(keyCode,event);
            }
            super.onKeyPreIme(keyCode, event);
            return false;
        }
        return super.onKeyPreIme(keyCode, event);
    }

    public void setOnKeyDownListener(OnKeyDownListener onKeyDownListener) {
        this.onKeyDownListener = onKeyDownListener;
    }
}
