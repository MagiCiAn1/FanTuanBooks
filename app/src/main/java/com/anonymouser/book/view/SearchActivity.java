package com.anonymouser.book.view;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.aitangba.swipeback.SwipeBackActivity;
import com.anonymouser.book.BookApp;
import com.anonymouser.book.R;
import com.anonymouser.book.adapter.SearchBookAdapter;
import com.anonymouser.book.bean.SearchBookInfoBean;
import com.anonymouser.book.presenter.HomePresenter;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import taobe.tec.jcc.JChineseConvertor;

/**
 * Created by YandZD on 2017/7/24.
 */

public class SearchActivity extends SwipeBackActivity {

    @BindView(R.id.searchBookList)
    RecyclerView mSearchBookList;
    @BindView(R.id.etSearch)
    EditText etSearch;
    @BindView(R.id.ivClear)
    ImageView ivClear;
    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;

    HomePresenter mPresenter;
    SearchBookAdapter mAdapter;


    TextWatcher mSearchTextListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (TextUtils.isEmpty(s)) {
                ivClear.setVisibility(View.INVISIBLE);
            } else {
                ivClear.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    public static Intent getSearchIntent(Context context, String bookName) {
        Intent intent = new Intent(context, SearchActivity.class);
        intent.putExtra("bookName", bookName);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setContentView(R.layout.activity_search);

        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        mPresenter = new HomePresenter();

        init();

        String bookName = getIntent().getStringExtra("bookName");
        if (!TextUtils.isEmpty(bookName)) {
            etSearch.setText(bookName);
            onSearch(null);
        }
    }

    @Override
    public void onDestroy() {
        etSearch.removeTextChangedListener(mSearchTextListener);
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }


    private void init() {
        etSearch.addTextChangedListener(mSearchTextListener);
        mAdapter = new SearchBookAdapter();
        mSearchBookList.setLayoutManager(new LinearLayoutManager(this));
        mSearchBookList.setAdapter(mAdapter);

        etSearch.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                // 修改回车键功能
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    // 先隐藏键盘
                    actionSearch();
                }

                return false;
            }
        });
    }

    @OnClick(R.id.ivReturn)
    public void onReturn(View view) {
        finish();
    }

    @OnClick(R.id.ivSearch)
    public void onSearch(View view) {
        actionSearch();
    }

    private void actionSearch() {
        String word = etSearch.getText().toString();
        if (!TextUtils.isEmpty(word)) {
            try {
                word = JChineseConvertor.getInstance().t2s(word);
            } catch (Exception e) {

            }

            setRotateLoading(true);
            mPresenter.searchBook(word);

            Tracker tracker = ((BookApp) getApplication()).getDefaultTracker();
            tracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Action")
                    .setAction("Search_Book")
                    .build());
        }
    }


    @OnClick(R.id.ivClear)
    public void onClear(View view) {
        etSearch.setText("");
    }

    @Subscribe
    public void searchBookResults(ArrayList<SearchBookInfoBean> beans) {
        if (beans != null) {
            mAdapter.setData(beans);
        }
        if (beans != null && beans.size() == 0) {
            Toast.makeText(this, "没有相关小说", Toast.LENGTH_SHORT).show();
        }
        setRotateLoading(false);

        if (SearchActivity.this
                .getCurrentFocus() != null) {
            ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(SearchActivity.this
                                    .getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public void setRotateLoading(boolean isShow) {
        if (isShow) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.GONE);
        }
    }

}
