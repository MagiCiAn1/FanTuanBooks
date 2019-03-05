package com.anonymouser.book.view;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ListView;

import com.anonymouser.book.R;
import com.anonymouser.book.adapter.CategoryBookAdapter;
import com.anonymouser.book.adapter.CategoryLeftAdapter;
import com.anonymouser.book.bean.CategoryBean;
import com.anonymouser.book.bean.CategoryBookItemBean;
import com.anonymouser.book.bean.CategoryItemBean;
import com.anonymouser.book.utlis.http.ServiceApi;
import com.anonymouser.book.view.lazyFragment.LazyFragment;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lzy.okgo.model.Response;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * 分类页面
 * 使用 追书神器 api
 * Created by YandZD on 2017/8/30.
 */

public class CategoryFragment extends LazyFragment {

    @BindView(R.id.category_item)
    ListView categoryLeftItem;
    @BindView(R.id.category_book)
    RecyclerView categoryRightItem;
    @BindView(R.id.refreshLayout)
    TwinklingRefreshLayout mRefreshLayout;

    private Disposable mDisposable;

    private CategoryBookAdapter mBookAdapter;

    private CategoryItemBean mNowCategoryItemBean;  //现在使用的左边的分类

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.fragment_category);

        ButterKnife.bind(this, getContentView());
        EventBus.getDefault().register(this);

        mBookAdapter = new CategoryBookAdapter();
        categoryRightItem.setLayoutManager(new LinearLayoutManager(getActivity()));
        categoryRightItem.setAdapter(mBookAdapter);
        //不需要下拉刷新
        mRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(final TwinklingRefreshLayout refreshLayout) {
            }

            @Override
            public void onLoadMore(final TwinklingRefreshLayout refreshLayout) {
                if (mNowCategoryItemBean == null) return;
                mNowCategoryItemBean.start = mBookAdapter.getItemCount();
                onClickLeftCategory(mNowCategoryItemBean);
            }
        });

        getCategory();
    }

    @Override
    protected void onDestroyViewLazy() {
        super.onDestroyViewLazy();
        EventBus.getDefault().unregister(this);
    }

    public void getCategory() {
        ServiceApi.categoryItem()
                .subscribe(new Observer<Response<CategoryBean>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Response<CategoryBean> categoryBeanResponse) {
                        CategoryLeftAdapter adapter = new CategoryLeftAdapter();
                        categoryLeftItem.setAdapter(adapter);


                        CategoryBean beans = categoryBeanResponse.body();
                        ArrayList<CategoryItemBean> itemBeans = new ArrayList<>();
                        CategoryItemBean bean;

                        for (CategoryBean.MaleBean item : beans.getMale()) {
                            if (item.getMins().isEmpty()) {
                                bean = new CategoryItemBean();
                                bean.gender = "male";
                                bean.major = item.getMajor();
                                bean.minor = "";
                                bean.str = bean.major;
                                itemBeans.add(bean);
                            } else {
                                for (String mins : item.getMins()) {
                                    bean = new CategoryItemBean();
                                    bean.gender = "male";
                                    bean.major = item.getMajor();
                                    bean.minor = mins;
                                    bean.str = mins;
                                    itemBeans.add(bean);
                                }
                            }
                        }

                        for (CategoryBean.FemaleBean item : beans.getFemale()) {
                            if (item.getMins().isEmpty()) {
                                bean = new CategoryItemBean();
                                bean.gender = "female";
                                bean.major = item.getMajor();
                                bean.minor = "";
                                bean.str = bean.major;
                                itemBeans.add(bean);
                            } else {
                                for (String mins : item.getMins()) {
                                    bean = new CategoryItemBean();
                                    bean.gender = "female";
                                    bean.major = item.getMajor();
                                    bean.minor = mins;
                                    bean.str = mins;
                                    itemBeans.add(bean);
                                }
                            }
                        }

                        for (CategoryBean.PictureBean item : beans.getPicture()) {
                            if (item.getMins().isEmpty()) {
                                bean = new CategoryItemBean();
                                bean.gender = "picture";
                                bean.major = item.getMajor();
                                bean.minor = "";
                                bean.str = bean.major;
                                itemBeans.add(bean);
                            } else {
                                for (Object mins : item.getMins()) {
                                    bean = new CategoryItemBean();
                                    bean.gender = "picture";
                                    bean.major = item.getMajor();
                                    bean.minor = (String) mins;
                                    bean.str = (String) mins;
                                    itemBeans.add(bean);
                                }
                            }
                        }

                        for (CategoryBean.PressBean item : beans.getPress()) {
                            if (item.getMins().isEmpty()) {
                                bean = new CategoryItemBean();
                                bean.gender = "press";
                                bean.major = item.getMajor();
                                bean.minor = "";
                                bean.str = bean.major;
                                itemBeans.add(bean);
                            } else {
                                for (Object mins : item.getMins()) {
                                    bean = new CategoryItemBean();
                                    bean.gender = "press";
                                    bean.major = item.getMajor();
                                    bean.minor = (String) mins;
                                    bean.str = (String) mins;
                                    itemBeans.add(bean);
                                }
                            }
                        }

                        adapter.setData(itemBeans);


                        if (!itemBeans.isEmpty()) {
                            onClickLeftCategory(itemBeans.get(0));
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

    @Subscribe
    public void onClickLeftCategory(CategoryItemBean bean) {
        //如果开始是0，就是新的分类，把以前的清空。
        if (bean.start == 0) {
            mBookAdapter.setData(new ArrayList<CategoryBookItemBean.BooksBean>());
        }

        if (mDisposable != null)
            mDisposable.dispose();

        mNowCategoryItemBean = bean;

        ServiceApi.categoryBookItem(bean)
                .subscribe(new Observer<Response<CategoryBookItemBean>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mDisposable = d;
                    }

                    @Override
                    public void onNext(@NonNull Response<CategoryBookItemBean> categoryBookItemBeanResponse) {
                        mBookAdapter.addData(categoryBookItemBeanResponse.body().getBooks());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        mRefreshLayout.finishLoadmore();
                    }

                    @Override
                    public void onComplete() {
                        mRefreshLayout.finishLoadmore();
                    }
                });
    }
}
