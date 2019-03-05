package com.anonymouser.book;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.anonymouser.book.event.BookInfoEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by YandZD on 2017/7/14.
 */

public class TestActivity extends Activity {

    private TextView tvTest;
    private Handler mhandler;

//    private Disposable mDisposable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        EventBus.getDefault().register(this);

//        ImageView iv = (ImageView) findViewById(R.id.iv);
//        ImgLoad.baseLoadImg("http://qidian.qpic.cn/qdbimg/349573/1004174819/180");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void getBookInfoEvent(BookInfoEvent info) {
        System.out.println(info.obj.getList());
    }
}
