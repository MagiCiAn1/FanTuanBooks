package com.anonymouser.book.view;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.aitangba.swipeback.SwipeBackActivity;
import com.anonymouser.book.R;
import com.anonymouser.book.adapter.RankAdapter;
import com.anonymouser.book.bean.RankBean;
import com.anonymouser.book.presenter.RankPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by YandZD on 2017/7/25.
 */

public class RankActivity extends SwipeBackActivity {
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.title)
    TextView tvTitle;

    RankAdapter mAdapter;

    RankPresenter mPresenter = new RankPresenter();

    public static Intent getRankIntent(Context context, String id) {
        Intent intent = new Intent(context, RankActivity.class);
        intent.putExtra("id", id);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setContentView(R.layout.activity_rank);

        ButterKnife.bind(this);

        String id = getIntent().getStringExtra("id");

        init(id);
    }

    private void init(String id) {
        mAdapter = new RankAdapter();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
        mPresenter.getData(id)
                .subscribe(new Observer<RankBean>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull RankBean rankBean) {
                        if (rankBean == null) return;
                        tvTitle.setText(rankBean.getRanking().getShortTitle());
                        mAdapter.setData(rankBean.getRanking().getBooks());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @OnClick(R.id.ivReturn)
    public void onReturn(View view) {
        finish();
    }

}
