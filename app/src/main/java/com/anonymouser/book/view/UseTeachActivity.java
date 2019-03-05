package com.anonymouser.book.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.aitangba.swipeback.SwipeBackActivity;
import com.anonymouser.book.R;

/**
 * Created by YandZD on 2017/8/31.
 */

public class UseTeachActivity extends SwipeBackActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_use_teach);
    }

    public void onReturn(View view) {
        finish();
    }
}
