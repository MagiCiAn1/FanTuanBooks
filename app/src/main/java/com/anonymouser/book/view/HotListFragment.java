package com.anonymouser.book.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anonymouser.book.R;
import com.anonymouser.book.adapter.HotListAdapter;
import com.anonymouser.book.bean.GenderBean;
import com.anonymouser.book.utlis.http.ServiceApi;
import com.anonymouser.book.view.lazyFragment.LazyFragment;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.adapter.Call;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by YandZD on 2017/7/13.
 */

public class HotListFragment extends LazyFragment {

    @BindView(R.id.maleList)
    LinearLayout maleList;
    @BindView(R.id.femaleList)
    LinearLayout femaleList;

//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View rootView = inflater.inflate(R.layout.fragment_hot_list,null);
//        ButterKnife.bind(this, rootView);
//        getData();
//
//        return rootView;
//    }

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.fragment_hot_list);

        ButterKnife.bind(this, getContentView());

        getData();
    }

    private void getData() {
        Observable.just("")
                .map(new Function<String, GenderBean>() {
                    @Override
                    public GenderBean apply(@NonNull String s) throws Exception {
//                        Call<String> call = OkGo.<String>get("http://api.zhuishushenqi.com/ranking/gender")
//                                .converter(new StringCallback() {
//                                    @Override
//                                    public void onSuccess(Response<String> response) {
//
//                                    }
//                                })
//                                .adapt();
//                        Response<String> response = call.execute();
//                        String body = response.body() == null ? "" : response.body();

                        String body = "{\"male\":[{\"_id\":\"54d42d92321052167dfb75e3\",\"title\":\"追书最热榜 Top100\",\"cover\":\"/ranking-cover/142319144267827\",\"collapse\":false,\"monthRank\":\"564d820bc319238a644fb408\",\"totalRank\":\"564d8494fe996c25652644d2\",\"shortTitle\":\"最热榜\"},{\"_id\":\"54d42e72d9de23382e6877fb\",\"title\":\"本周潜力榜\",\"cover\":\"/ranking-cover/142319166399949\",\"collapse\":false,\"monthRank\":\"564eee3ea82e3ada6f14b195\",\"totalRank\":\"564eeeabed24953671f2a577\",\"shortTitle\":\"潜力榜\"},{\"_id\":\"564547c694f1c6a144ec979b\",\"title\":\"读者留存率 Top100\",\"cover\":\"/ranking-cover/144738093413066\",\"collapse\":false,\"monthRank\":\"564d898f59fd983667a5e3fa\",\"totalRank\":\"564d8a004a15bb8369d9e28d\",\"shortTitle\":\"留存榜\"},{\"_id\":\"564eb878efe5b8e745508fde\",\"title\":\"追书完结榜 Top100\",\"cover\":\"/ranking-cover/144799960841612\",\"collapse\":false,\"monthRank\":\"564eb12c3edb8b45511139ff\",\"totalRank\":\"564eea0b731ade4d6c509493\",\"shortTitle\":\"完结榜\"},{\"_id\":\"57eb86f0ef9e5a8f20543d7d\",\"title\":\"VIP排行榜\",\"cover\":\"/ranking-cover/14750532964058\",\"collapse\":false,\"totalRank\":\"5848d4a4602c289505581f6f\",\"monthRank\":\"57eb9283f031bfcc219389af\",\"shortTitle\":\"VIP榜\"},{\"_id\":\"582ed5fc93b7e855163e707d\",\"title\":\"圣诞热搜榜\",\"cover\":\"/ranking-cover/147946444450686\",\"collapse\":true,\"shortTitle\":\"圣诞榜\"},{\"_id\":\"564ef4f985ed965d0280c9c2\",\"title\":\"百度热搜榜\",\"cover\":\"/ranking-cover/142319217152210\",\"collapse\":true,\"shortTitle\":\"百度榜\"},{\"_id\":\"564d8003aca44f4f61850fcd\",\"title\":\"掌阅热销榜\",\"cover\":\"/ranking-cover/142319217152210\",\"collapse\":true,\"shortTitle\":\"掌阅榜\"},{\"_id\":\"564d80457408cfcd63ae2dd0\",\"title\":\"书旗热搜榜\",\"shortTitle\":\"书旗榜\",\"cover\":\"/ranking-cover/142319217152210\",\"collapse\":true},{\"_id\":\"54d430e9a8cb149d07282496\",\"title\":\"17K 鲜花榜\",\"shortTitle\":\"17K榜\",\"cover\":\"/ranking-cover/142319217152210\",\"collapse\":true},{\"_id\":\"54d4306c321052167dfb75e4\",\"title\":\"起点月票榜\",\"cover\":\"/ranking-cover/142319217152210\",\"collapse\":true,\"shortTitle\":\"起点榜\"},{\"_id\":\"54d430962c12d3740e4a3ed2\",\"title\":\"纵横月票榜\",\"cover\":\"/ranking-cover/142319217152210\",\"collapse\":true,\"shortTitle\":\"纵横榜\"},{\"_id\":\"54d4312d5f3c22ae137255a1\",\"title\":\"和阅读原创榜\",\"cover\":\"/ranking-cover/142319217152210\",\"collapse\":true,\"shortTitle\":\"和阅读榜\"},{\"_id\":\"5732aac02dbb268b5f037fc4\",\"title\":\"逐浪点击榜\",\"cover\":\"/ranking-cover/146293830220772\",\"collapse\":true,\"shortTitle\":\"逐浪榜\"}],\"female\":[{\"_id\":\"54d43437d47d13ff21cad58b\",\"title\":\"追书最热榜 Top100\",\"cover\":\"/ranking-cover/142319314350435\",\"collapse\":false,\"monthRank\":\"564d853484665f97662d0810\",\"totalRank\":\"564d85b6dd2bd1ec660ea8e2\",\"shortTitle\":\"最热榜\"},{\"_id\":\"54d43709fd6ec9ae04184aa5\",\"title\":\"本周潜力榜\",\"cover\":\"/ranking-cover/142319383473238\",\"collapse\":false,\"monthRank\":\"564eee77e3a44c9f0e5fd7ae\",\"totalRank\":\"564eeeca5e6ba6ae074f10ec\",\"shortTitle\":\"潜力榜\"},{\"_id\":\"5645482405b052fe70aeb1b5\",\"title\":\"读者留存率 Top100\",\"cover\":\"/ranking-cover/144738102858782\",\"collapse\":false,\"monthRank\":\"564d8b6b36d10ccd6951195d\",\"totalRank\":\"564d8c37752bcca16a976168\",\"shortTitle\":\"留存榜\"},{\"_id\":\"564eb8a9cf77e9b25056162d\",\"title\":\"追书完结榜 Top100\",\"cover\":\"/ranking-cover/144799965747841\",\"collapse\":false,\"monthRank\":\"564ee8ec146f8f1739777740\",\"totalRank\":\"564eeae6c3345baa6bf06e38\",\"shortTitle\":\"完结榜\"},{\"_id\":\"57eb959df60eb7e21fb3a8b7\",\"title\":\"VIP排行榜\",\"cover\":\"/ranking-cover/147505705336267\",\"collapse\":false,\"monthRank\":\"57eb98afcf7ddb81588947b4\",\"totalRank\":\"57eb98fe1ae40b985292051e\",\"shortTitle\":\"VIP榜\"},{\"_id\":\"582fb5c412a401a20ea50275\",\"title\":\"圣诞热搜榜\",\"cover\":\"/ranking-cover/14795217326220\",\"collapse\":true,\"shortTitle\":\"圣诞榜\"},{\"_id\":\"564d80d0e8c613016446c5aa\",\"title\":\"掌阅热销榜\",\"cover\":\"/ranking-cover/142319217152210\",\"collapse\":true,\"shortTitle\":\"掌阅榜\"},{\"_id\":\"564d81151109835664770ad7\",\"title\":\"书旗热搜榜\",\"shortTitle\":\"书旗榜\",\"cover\":\"/ranking-cover/142319217152210\",\"collapse\":true},{\"_id\":\"550b841715db45cd4b022107\",\"title\":\"17K订阅榜\",\"shortTitle\":\"17K榜\",\"cover\":\"/ranking-cover/142319217152210\",\"collapse\":true},{\"_id\":\"550b836229cd462830ff4d1d\",\"title\":\"起点粉红票榜\",\"cover\":\"/ranking-cover/142319217152210\",\"collapse\":true,\"shortTitle\":\"起点榜\"},{\"_id\":\"550b8397de12381038ad8c0b\",\"title\":\"潇湘月票榜\",\"cover\":\"/ranking-cover/142319217152210\",\"collapse\":true,\"shortTitle\":\"潇湘榜\"}],\"ok\":true}";

                        GenderBean bean;
                        if (!TextUtils.isEmpty(body)) {
                            bean = new Gson().fromJson(body, GenderBean.class);
                        } else {
                            throw new Exception();
                        }

                        return bean;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GenderBean>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull GenderBean s) {
                        View view;
                        TextView tvTitle;
                        for (GenderBean.FemaleBean bean : s.getFemale()) {
                            view = LayoutInflater.from(getActivity()).inflate(R.layout.item_hot_list, femaleList, false);
                            tvTitle = (TextView) view.findViewById(R.id.tvTitle);
                            tvTitle.setText(bean.getShortTitle());
                            tvTitle.setTag(bean.get_id());
                            tvTitle.setClickable(true);
                            tvTitle.setOnClickListener(itemClick);
                            femaleList.addView(view);
                        }

                        for (GenderBean.MaleBean bean : s.getMale()) {
                            view = LayoutInflater.from(getActivity()).inflate(R.layout.item_hot_list, maleList, false);
                            tvTitle = (TextView) view.findViewById(R.id.tvTitle);
                            tvTitle.setText(bean.getShortTitle());
                            tvTitle.setTag(bean.get_id());
                            tvTitle.setClickable(true);
                            tvTitle.setOnClickListener(itemClick);
                            maleList.addView(view);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public View.OnClickListener itemClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = RankActivity.getRankIntent(getActivity(), v.getTag().toString());
            getActivity().startActivity(intent);
        }
    };


}
