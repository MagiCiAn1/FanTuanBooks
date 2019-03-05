package com.anonymouser.book.view;

import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.aitangba.swipeback.SwipeBackActivity;
import com.anonymouser.book.R;
import com.anonymouser.book.utlis.http.ServiceApi;
import com.lzy.okgo.model.Response;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * 反馈页面
 * Created by YandZD on 2017/8/10.
 */

public class FeedbackActivity extends SwipeBackActivity {

    ArrayList<Disposable> mDisposables = new ArrayList<>();
    EditText etFeedback, etFeedbackEmali;
    CountDownTimer mTimer;
    SweetAlertDialog mDialog;
    int i = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setContentView(R.layout.activity_feedback);

        etFeedback = (EditText) findViewById(R.id.etFeedback);
        etFeedbackEmali = (EditText) findViewById(R.id.etEmail);

    }

    public void onSubmit(View view) {
        String msg = etFeedback.getText().toString();
        String email = etFeedbackEmali.getText().toString();
        if (TextUtils.isEmpty(msg)) return;
        if (TextUtils.isEmpty(email)) {
            msg = msg + "|---------email:" + email;
        }

        mDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
                .setTitleText("Loading");
        mDialog.show();
        mDialog.setCancelable(false);

        mTimer = new CountDownTimer(800 * 7, 800) {
            public void onTick(long millisUntilFinished) {
                // you can change the progress bar color by ProgressHelper every 800 millis
                i++;
                switch (i) {
                    case 0:
                        mDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.blue_btn_bg_color));
                        break;
                    case 1:
                        mDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.material_deep_teal_50));
                        break;
                    case 2:
                        mDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.success_stroke_color));
                        break;
                    case 3:
                        mDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.material_deep_teal_20));
                        break;
                    case 4:
                        mDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.material_blue_grey_80));
                        break;
                    case 5:
                        mDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.warning_stroke_color));
                        break;
                    case 6:
                        mDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.success_stroke_color));
                        break;
                }
            }

            public void onFinish() {
                i = -1;
                mDialog.setTitleText("提交失败！")
                        .setConfirmText("确定")
                        .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                for (Disposable disposable : mDisposables) {
                    disposable.dispose();
                }
            }


        }.start();

        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
        ServiceApi.feedback(time + ": " + msg)
                .subscribe(new Observer<Response<String>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mDisposables.add(d);
                    }

                    @Override
                    public void onNext(@NonNull Response<String> stringResponse) {
                        //成功
                        mTimer.cancel();
                        mDialog.setTitleText("提交成功！")
                                .setConfirmText("确定")
                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        //失败
                        mTimer.onFinish();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog = null;
        }
        for (Disposable disposable : mDisposables) {
            disposable.dispose();
        }
    }

    public void onReturn(View view) {
        finish();
    }
}
