package com.anonymouser.book.presenter;

import com.anonymouser.book.bean.RankBean;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.adapter.Call;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by YandZD on 2017/7/25.
 */

public class RankPresenter {

    public Observable<RankBean> getData(String id) {
        return Observable.just(id)
                .map(new Function<String, RankBean>() {
                    @Override
                    public RankBean apply(@NonNull String s) throws Exception {
                        Call<String> call = OkGo.<String>get("http://api.zhuishushenqi.com/ranking/" + s)
                                .converter(new StringCallback() {
                                    @Override
                                    public void onSuccess(Response<String> response) {

                                    }
                                })
                                .adapt();
                        Response<String> response = call.execute();
                        String body = response.body() == null ? "" : response.body();
                        RankBean bean = new Gson().fromJson(body, RankBean.class);
                        return bean;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }


}
