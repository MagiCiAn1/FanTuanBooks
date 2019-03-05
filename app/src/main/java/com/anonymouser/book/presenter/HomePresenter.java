package com.anonymouser.book.presenter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.anonymouser.book.BookApp;
import com.anonymouser.book.bean.BookCaseBean;
import com.anonymouser.book.bean.BookInfo;
import com.anonymouser.book.bean.ConfBean;
import com.anonymouser.book.bean.LastChapterBean;
import com.anonymouser.book.bean.SearchBookInfoBean;
import com.anonymouser.book.bean.UserInfo;
import com.anonymouser.book.bean.ZhuiShuSearcheBean;
import com.anonymouser.book.event.CheckUpdateEvent;
import com.anonymouser.book.event.NotifyBookCaseLastChaptersEvent;
import com.anonymouser.book.event.SaveIsShowAdInfo;
import com.anonymouser.book.module.BookDao;
import com.anonymouser.book.module.BookLoadFactory;
import com.anonymouser.book.module.BookModule;
import com.anonymouser.book.utlis.http.ServiceApi;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.adapter.Call;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import org.greenrobot.eventbus.EventBus;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by YandZD on 2017/7/17.
 */

public class HomePresenter {

    public static LastChapterBean[] mLastChapterBeans;

    public UserInfo loadUserInfo() {
        return BookModule.loadUserInfo();
    }

    public void searchBook(String word) {

        Observable
                .zip(ServiceApi.searchBookInfo(word), ServiceApi.zhuishuSearch(word)
                        , new BiFunction<Response<SearchBookInfoBean[]>, Response<ZhuiShuSearcheBean>, ArrayList<SearchBookInfoBean>>() {
                            @Override
                            public ArrayList<SearchBookInfoBean> apply(@NonNull Response<SearchBookInfoBean[]> response
                                    , @NonNull Response<ZhuiShuSearcheBean> zhuiShuSearcheBeanResponse) throws Exception {
                                ArrayList<SearchBookInfoBean> searchBookInfoBeans = new ArrayList<>();
                                //把追书的结果转换成 SearchBookInfoBean
                                SearchBookInfoBean zhuishuBookInfoBean;
                                for (ZhuiShuSearcheBean.BooksBean bean : zhuiShuSearcheBeanResponse.body().getBooks()) {
                                    zhuishuBookInfoBean = new SearchBookInfoBean();
                                    zhuishuBookInfoBean.setBookName(bean.getTitle());
                                    zhuishuBookInfoBean.setImg(URLDecoder.decode(bean.getCover()).replace("/agent/", ""));
                                    zhuishuBookInfoBean.setAuthor(bean.getAuthor());
                                    zhuishuBookInfoBean.setIntro(bean.getShortIntro());
                                    zhuishuBookInfoBean.setId(bean.get_id());
                                    zhuishuBookInfoBean.setType(bean.getCat());
                                    zhuishuBookInfoBean.setTag("ZS");
                                    zhuishuBookInfoBean.setZhuiShu(true);
                                    searchBookInfoBeans.add(zhuishuBookInfoBean);
                                }
                                searchBookInfoBeans.addAll(Arrays.asList(response.body()));

                                //把可能没有源的追书结果移动到后面
                                int size = zhuiShuSearcheBeanResponse.body().getBooks().size();

                                for (int i = 0; i < size; i++) {
                                    if (searchBookInfoBeans.get(i).getImg().contains("qidian")) {
                                        searchBookInfoBeans.add(searchBookInfoBeans.get(i));
                                        searchBookInfoBeans.remove(i);
                                        size--;
                                    }
                                }

                                return searchBookInfoBeans;
                            }
                        })
                .subscribe(new Observer<ArrayList<SearchBookInfoBean>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull ArrayList<SearchBookInfoBean> searchBookInfoBeen) {
                        EventBus.getDefault().post(searchBookInfoBeen);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        ArrayList<SearchBookInfoBean> beans = new ArrayList<>();
                        EventBus.getDefault().post(beans);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void addBookCase(BookCaseBean bean) {
        BookModule.saveBookCaseBean(bean);
    }

    public void removeBookCase(BookCaseBean bean) {
        BookModule.deleteBookCaseBean(bean);
    }

    public void notfiyBookCase() {
        BookModule.notfiyBookCase();
    }

    public void notfiyDataBase(List<BookCaseBean> beans) {
        BookModule.notifyDataBase(beans);
    }

    //删除临时数据
    public void removeTempDataBase() {
        BookModule.removeTempDataBase();
    }

    //jar 包初始化
    public void initJar() {
        Observable.just(BookApp.mContext.getSharedPreferences("init", Context.MODE_PRIVATE).getBoolean("isCopyJar", false))
                .subscribeOn(Schedulers.io())
                .map(new Function<Boolean, Object>() {
                    @Override
                    public Object apply(@NonNull Boolean isInit) throws Exception {
                        if (!isInit) {
                            File file = new File(BookApp.mContext.getFilesDir(), "/jar");
                            file.mkdir();

                            String[] paths = BookApp.mContext.getAssets().list("jar");
                            InputStream input;
                            BufferedOutputStream output;
                            byte[] fileByte = new byte[2048 * 2];
                            for (String filePath : paths) {
                                input = BookApp.mContext.getAssets().open("jar/" + filePath);

                                output = new BufferedOutputStream(new FileOutputStream(new File(BookApp.mContext.getFilesDir(), "/jar/" + filePath)));

                                while (input.read(fileByte) > 0) {
                                    output.write(fileByte);
                                }
                            }
                            BookApp.mContext.getSharedPreferences("init", Context.MODE_PRIVATE).edit()
                                    .putBoolean("isCopyJar", true).commit();
                        }
                        return "";
                    }
                })
                .map(new Function<Object, String>() {
                    @Override
                    public String apply(@NonNull Object s) throws Exception {
                        Call<String> call = OkGo.<String>get(ServiceApi.HEADAPI + "info")
                                .converter(new StringCallback() {
                                    @Override
                                    public void onSuccess(Response<String> response) {

                                    }
                                })
                                .adapt();
                        Response<String> response = call.execute();
                        return response.body() == null ? "" : response.body();
                    }
                })
                .map(new Function<String, String>() {
                    @Override
                    public String apply(@NonNull String s) throws Exception {
                        if (TextUtils.isEmpty(s)) return "";

                        ArrayList<ConfBean.JarBean> needDownloadJar = new ArrayList<>();
                        try {
                            ConfBean beans = new Gson().fromJson(s, ConfBean.class);
                            //是否显示广告
                            SaveIsShowAdInfo isShowAdInfo = new SaveIsShowAdInfo();
                            isShowAdInfo.isShowAd = beans.isShowAd();
                            EventBus.getDefault().post(isShowAdInfo);

                            //检查app版本 升级
                            CheckUpdateEvent event = new CheckUpdateEvent();
                            event.mBean = beans.getApp();
                            EventBus.getDefault().post(event);

                            //检查jar包升级
                            for (ConfBean.JarBean x : beans.getJar()) {
                                try {
                                    if (new BookLoadFactory(x.getTag(), null).getVersion() < x.getVersion()) {
                                        needDownloadJar.add(x);
                                    }
                                } catch (Exception e) {
                                    needDownloadJar.add(x);
                                }
                            }
                            for (ConfBean.JarBean bean : needDownloadJar) {
                                OkGo.<File>get(bean.getLink())
                                        .execute(new FileCallback(BookApp.mContext.getFilesDir().getAbsolutePath() + "/jar", bean.getTag()) {
                                            @Override
                                            public void onSuccess(Response<File> response) {

                                            }
                                        });
                            }
                        } catch (Exception e) {

                        }

                        return "";
                    }
                })
                .subscribe();
    }

    //获取书架上的最新章节
    public void getLastChapters(List<BookCaseBean> beans) {
        if (mLastChapterBeans != null && mLastChapterBeans.length > 0) return;
        String ids = "";
        String bookInfoIds = "";
        for (final BookCaseBean bean : beans) {
            if (bean.getIsZhuiShu()) {
                ids += "," + bean.getZhuiShuId();
            }else{
                bookInfoIds += "," + bean.getZhuiShuId();
            }
        }

        if (TextUtils.isEmpty(ids)&&TextUtils.isEmpty(bookInfoIds)) {
            return;
        } else {
            if (!TextUtils.isEmpty(ids))
                ids = ids.subSequence(1, ids.length()).toString();
            else{
                ids = "59ba0dbb017336e411085a4e";
            }
            if (!TextUtils.isEmpty(bookInfoIds))
                bookInfoIds = bookInfoIds.subSequence(1, bookInfoIds.length()).toString();
            Observable.zip(ServiceApi.zhuishuLastChapt(ids), ServiceApi.bookInfoLastChapt(bookInfoIds),
                    new BiFunction<Response<LastChapterBean[]>, Response<LastChapterBean[]>, Response<LastChapterBean[]>>() {
                        @Override
                        public Response<LastChapterBean[]> apply(@NonNull Response<LastChapterBean[]> zhuishuResponse, @NonNull Response<LastChapterBean[]> bookResponse) throws Exception {
                            Response<LastChapterBean[]> res = new Response<LastChapterBean[]>();
                            LastChapterBean[] zhuiShuiLastChapter = zhuishuResponse.body();
                            LastChapterBean[] bookInfoLastChapter = bookResponse.body();
                            LastChapterBean[] responseChapter= new LastChapterBean[zhuiShuiLastChapter.length+bookInfoLastChapter.length];
                            for (int i=0;i< zhuiShuiLastChapter.length;i++){
                                responseChapter[i] = zhuiShuiLastChapter[i];
                            }
                            for (int i=0;i< bookInfoLastChapter.length;i++){
                                responseChapter[zhuiShuiLastChapter.length+i] = bookInfoLastChapter[i];
                            }
                            res.setBody(responseChapter);
                            return res;
                        }
                    }
                    )
                    .subscribe(new Observer<Response<LastChapterBean[]>>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NonNull Response<LastChapterBean[]> response) {
                            mLastChapterBeans = response.body();
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {

                        }

                        @Override
                        public void onComplete() {
                            EventBus.getDefault().post(new NotifyBookCaseLastChaptersEvent());
                        }
                    });

        }
    }
}
