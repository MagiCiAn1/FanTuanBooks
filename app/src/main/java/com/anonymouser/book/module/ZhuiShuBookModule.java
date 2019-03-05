package com.anonymouser.book.module;

import android.text.TextUtils;

import com.anonymouser.book.bean.BookContent;
import com.anonymouser.book.bean.BookInfo;
import com.anonymouser.book.bean.ZhuiShuBookContent;
import com.anonymouser.book.bean.ZhuiShuChaptersBean;
import com.anonymouser.book.bean.ZhuiShuContentBean;
import com.anonymouser.book.bean.ZhuiShuSourceBean;
import com.anonymouser.book.utlis.http.ServiceApi;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.adapter.Call;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.anonymouser.book.module.BookModule.mDownloadScheduler;
import static com.anonymouser.book.module.BookModule.mScheduler;

/**
 * Created by YandZD on 2017/8/11.
 */

public class ZhuiShuBookModule {

    //获取源列表
    public static Observable<Response<ZhuiShuSourceBean[]>> getSourceListBean(String bookId) {
        return ServiceApi.zhuishuSource(bookId);
    }

    //获取章节
    public static Observable<Response<ZhuiShuChaptersBean>> getChapters(String sourceId) {
        return ServiceApi.zhuishuChapters(sourceId);
    }

    public static Observable<ZhuiShuBookContent> getBookContent(
            final String bookName
            , String link) {
        List<String> links = new ArrayList<>();
        links.add(link);
        return getBookContent(bookName, links);
    }

    //从追书神器api 、获取内容
    public static Observable<ZhuiShuBookContent> getBookContent(final String bookName
            , List<String> link) {
        if (mScheduler == null) {
            mScheduler = Schedulers.from(Executors.newFixedThreadPool(3));
        }

        return Observable.fromIterable(link)
                .flatMap(new Function<String, ObservableSource<ZhuiShuBookContent>>() {
                    @Override
                    public ObservableSource<ZhuiShuBookContent> apply(@NonNull String s) throws Exception {
                        return Observable
                                .just(s)
                                .subscribeOn(mScheduler)     //线程池
                                .map(new Function<String, ZhuiShuBookContent>() {
                                    @Override
                                    public ZhuiShuBookContent apply(@NonNull String s) throws Exception {
                                        try {
                                            boolean isNeedSave = false;
                                            //先查询数据库，如果有则不在网上爬
                                            String content = ZhuiShuBookDao.newInstance().loadContent(s);
                                            if (TextUtils.isEmpty(content)) {
                                                String link = String.format("http://chapterup.zhuishushenqi.com/chapter/%s?cv=%d", s, System.currentTimeMillis());
                                                Call<String> call = OkGo.<String>get(link)
                                                        .converter(new StringCallback() {
                                                            @Override
                                                            public void onSuccess(Response<String> response) {

                                                            }
                                                        })
                                                        .adapt();
                                                Response<String> response = call.execute();
                                                String body = response.body() == null ? "" : response.body();
                                                ZhuiShuContentBean bean = new Gson().fromJson(body, ZhuiShuContentBean.class);
                                                if (bean == null || !bean.isOk()) {
                                                    ZhuiShuBookContent errorContent = new ZhuiShuBookContent();
                                                    errorContent.setLink(s);
                                                    EventBus.getDefault().post(errorContent);
                                                    throw new Exception();
                                                }
                                                content = bean.getChapter().getBody();
                                                isNeedSave = true;
                                            }

                                            ZhuiShuBookContent bookContent = new ZhuiShuBookContent();
                                            bookContent.setBookName(bookName);
                                            bookContent.setLink(s);
                                            bookContent.setContent(content);
                                            bookContent.setTime(System.currentTimeMillis());

                                            if (TextUtils.isEmpty(content)) {
                                                throw new Exception();
                                            }

                                            if (isNeedSave)
                                                ZhuiShuBookDao.newInstance().saveContent(bookContent);
                                            return bookContent;
                                        } catch (Exception e) {
                                            System.out.println(e);
                                        }
                                        throw new Exception();
                                    }
                                })
                                .observeOn(Schedulers.io())
                                .onExceptionResumeNext(new Observable<ZhuiShuBookContent>() {
                                    @Override
                                    protected void subscribeActual(Observer<? super ZhuiShuBookContent> observer) {
                                        observer.onError(new Exception());
                                    }
                                });

                    }
                })
                .observeOn(Schedulers.io());
    }

    public static Observable<ZhuiShuBookContent> downloadBookContent(final String bookName
            , List<String> link) {
        if (mDownloadScheduler == null) {
            mDownloadScheduler = Schedulers.from(Executors.newFixedThreadPool(5));
        }

        return Observable.fromIterable(link)
                .flatMap(new Function<String, ObservableSource<ZhuiShuBookContent>>() {
                    @Override
                    public ObservableSource<ZhuiShuBookContent> apply(@NonNull String s) throws Exception {
                        return Observable
                                .just(s)
                                .subscribeOn(mDownloadScheduler)     //线程池
                                .map(new Function<String, ZhuiShuBookContent>() {
                                    @Override
                                    public ZhuiShuBookContent apply(@NonNull String s) throws Exception {
                                        try {
                                            boolean isNeedSave = false;
                                            //先查询数据库，如果有则不在网上爬
                                            String content = ZhuiShuBookDao.newInstance().loadContent(s);
                                            if (TextUtils.isEmpty(content)) {
                                                String link = String.format("http://chapterup.zhuishushenqi.com/chapter/%s?cv=%d", s, System.currentTimeMillis());
                                                Call<String> call = OkGo.<String>get(link)
                                                        .converter(new StringCallback() {
                                                            @Override
                                                            public void onSuccess(Response<String> response) {

                                                            }
                                                        })
                                                        .adapt();
                                                Response<String> response = call.execute();
                                                String body = response.body() == null ? "" : response.body();
                                                ZhuiShuContentBean bean = new Gson().fromJson(body, ZhuiShuContentBean.class);

                                                content = bean.getChapter().getBody();
                                                isNeedSave = true;
                                            }

                                            ZhuiShuBookContent bookContent = new ZhuiShuBookContent();
                                            bookContent.setBookName(bookName);
                                            bookContent.setLink(s);
                                            bookContent.setContent(content);
                                            bookContent.setTime(System.currentTimeMillis());

                                            if (TextUtils.isEmpty(content)) {
                                                throw new Exception();
                                            }

                                            if (isNeedSave)
                                                ZhuiShuBookDao.newInstance().saveContent(bookContent);
                                            return bookContent;
                                        } catch (Exception e) {
                                            System.out.println(e);
                                        }
                                        throw new Exception();
                                    }
                                })
                                .observeOn(Schedulers.io())
                                .onExceptionResumeNext(new Observable<ZhuiShuBookContent>() {
                                    @Override
                                    protected void subscribeActual(Observer<? super ZhuiShuBookContent> observer) {
                                        observer.onComplete();
                                    }
                                });

                    }
                })
                .observeOn(Schedulers.io());
    }

    public static String getBookContentDao(String link) {
        String context = ZhuiShuBookDao.newInstance().loadContent(link);
        return context;
    }

    public static List<String> getBookDownloadList(String bookName) {
        return ZhuiShuBookDao.newInstance().queryDownloadBookContent(bookName);
    }

}
