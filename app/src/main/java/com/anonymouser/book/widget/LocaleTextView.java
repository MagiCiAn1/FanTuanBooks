package com.anonymouser.book.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

import com.anonymouser.book.BookApp;

import taobe.tec.jcc.JChineseConvertor;

/**
 * 设置textview 繁简体
 * Created by YandZD on 2017/8/15.
 */

public class LocaleTextView extends TextView {
    public LocaleTextView(Context context) {
        super(context);
    }

    public LocaleTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LocaleTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setText(String text) {
        if (!BookApp.isSimple) {
            try {
                text = JChineseConvertor.getInstance().s2t(text);
            } catch (Exception e) {
            }
        }
        super.setText(text);
    }

}
