package com.anonymouser.book.service;

import android.text.TextUtils;

import com.anonymouser.book.bean.BookCaseBean;
import com.anonymouser.book.bean.BookContent;
import com.anonymouser.book.bean.BookInfo;
import com.anonymouser.book.bean.DownloadBookEvent;
import com.anonymouser.book.bean.SearchBookInfoBean;
import com.anonymouser.book.bean.ZhuiShuBookContent;
import com.anonymouser.book.bean.ZhuiShuChaptersBean;
import com.anonymouser.book.bean.ZhuiShuSourceBean;
import com.anonymouser.book.event.CacheProgressEvent;
import com.anonymouser.book.module.BookModule;
import com.anonymouser.book.module.ZhuiShuBookModule;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.lzy.okgo.model.Response;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 这里不使用 Service 了。 因为没有做多任务管理
 * Created by YandZD on 2017/7/21.
 */

public class DownloadService {
    private List<DownloadBookEvent> mEvents = new ArrayList<>();
    private int mDownloadIndex = -1;
    private int mFinishIndex = 0;
    private boolean lock = true;
    private List<Disposable> mDisposables = new ArrayList<>();
    private int oldProgress = -1;
    private long sendEventTime = 0;   //防止发送事件太频繁
    private CacheProgressEvent event = new CacheProgressEvent();
    private int totalCacheCount = 1;      //总需要缓存的章节数


    public void onDownloadEvent(DownloadBookEvent downloadBookEvent) {

        mEvents.add(downloadBookEvent);
        onDown();
    }

    public void onDown() {
        if (lock) {
            if (mDownloadIndex + 1 >= mEvents.size()) return;
            mFinishIndex = 0;
            oldProgress = 0;
            lock = false;
            mDownloadIndex++;
            if (mEvents.get(mDownloadIndex).bean.getIsZhuiShu()) {
                downloadZhuiShuBook();
            } else {
                downloadLocalBook();
            }

        }
    }

    public void downloadLocalBook() {
        Observable.just(mEvents.get(mDownloadIndex).bean)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap(new Function<BookCaseBean, Observable<BookInfo>>() {
                    @Override
                    public Observable<BookInfo> apply(@NonNull BookCaseBean bean) throws Exception {
                        SearchBookInfoBean.BaseLink[] links = new Gson().fromJson(bean.getBaseLink(),
                                SearchBookInfoBean.BaseLink[].class);

                        String link = "";           //入口
//                            bean.getUseSource();
                        for (SearchBookInfoBean.BaseLink baseLink : links) {
                            if (baseLink.getTag().equals(bean.getUseSource())) {
                                link = baseLink.getLink();
                            }
                        }

                        return BookModule.getBookInfo(mEvents.get(mDownloadIndex).bean.getUseSource(), link, mEvents.get(mDownloadIndex).bean.getBookName());
                    }
                })
                .map(new Function<BookInfo, ArrayList<String>>() {
                    @Override
                    public ArrayList<String> apply(@NonNull BookInfo info) throws Exception {
                        JsonArray jsonArray = (JsonArray) new JsonParser().parse(info.getList());
                        ArrayList<String> strings = new ArrayList<>();

                        //计算缓存的范围
                        int startIndex = 0, endIndex = 0;
                        int module = mEvents.get(mDownloadIndex).downloadModel;
                        if (module == 0) {
                            startIndex = mEvents.get(mDownloadIndex).bean.getReadProgress();
                            endIndex = startIndex + 50;
                        } else if (module == 1) {
                            startIndex = mEvents.get(mDownloadIndex).bean.getReadProgress();
                            endIndex = jsonArray.size();
                        } else if (module == 2) {
                            startIndex = 0;
                            endIndex = jsonArray.size();
                        }
                        totalCacheCount = endIndex - startIndex;

                        for (int s = startIndex; s < endIndex; s++) {
                            if (jsonArray.size() > s) {
                                strings.add(jsonArray.get(s).getAsJsonObject().get("link").getAsString());
                            } else {
                                break;
                            }
                        }

                        return strings;
                    }
                })
                .flatMap(new Function<ArrayList<String>, Observable<BookContent>>() {
                    @Override
                    public Observable<BookContent> apply(@NonNull ArrayList<String> strings) throws Exception {
                        return BookModule.downloadBookContent(mEvents.get(mDownloadIndex).bean.getUseSource(),
                                mEvents.get(mDownloadIndex).bean.getBookName(), strings);
                    }
                })
                .subscribe(new Observer<BookContent>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mDisposables.add(d);

                        event.isFinish = true;
                        event.msg = mEvents.get(mDownloadIndex).bean.getBookName() + " 0%";
                        EventBus.getDefault().post(event);

                    }

                    @Override
                    public void onNext(@NonNull BookContent bookContent) {
                        if (bookContent == null) return;

                        mFinishIndex++;

                        int progress = ((int) ((mFinishIndex / (float) totalCacheCount) * 100));
                        if (progress > oldProgress && System.currentTimeMillis() - sendEventTime > 1000) {
                            oldProgress = progress;
                            sendEventTime = System.currentTimeMillis();

                            event.isFinish = false;
                            event.msg = mEvents.get(mDownloadIndex).bean.getBookName() + " " + progress + "%";
                            EventBus.getDefault().post(event);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        lock = true;
                    }

                    @Override
                    public void onComplete() {
                        lock = true;

                        event.isFinish = true;
                        event.msg = "";
                        EventBus.getDefault().post(event);

                        onDown();

                    }
                });
    }

    public void downloadZhuiShuBook() {
        ZhuiShuBookModule
                .getSourceListBean(mEvents.get(mDownloadIndex).bean.getZhuiShuId())
                .flatMap(new Function<Response<ZhuiShuSourceBean[]>, Observable<Response<ZhuiShuChaptersBean>>>() {
                    @Override
                    public Observable<Response<ZhuiShuChaptersBean>> apply(@NonNull Response<ZhuiShuSourceBean[]> response) throws Exception {
                        String sourceId = "";
                        if (!TextUtils.isEmpty(mEvents.get(mDownloadIndex).bean.getUseSource())) {
                            //如果指定使用源，判断这个源是否是可以使用的
                            for (ZhuiShuSourceBean item : response.body()) {
                                if (item.getSource().equals(mEvents.get(mDownloadIndex).bean.getUseSource())) {
                                    sourceId = item.get_id();
                                    break;
                                }
                            }
                        }
                        return ZhuiShuBookModule.getChapters(sourceId);
                    }
                })
                .map(new Function<Response<ZhuiShuChaptersBean>, ArrayList<String>>() {
                    @Override
                    public ArrayList<String> apply(@NonNull Response<ZhuiShuChaptersBean> zhuiShuChaptersBeanResponse) throws Exception {
                        List<ZhuiShuChaptersBean.ChaptersBean> chapters = zhuiShuChaptersBeanResponse.body().getChapters();
                        ArrayList<String> strings = new ArrayList<>();

                        //计算缓存的范围
                        int startIndex = 0, endIndex = 0;
                        int module = mEvents.get(mDownloadIndex).downloadModel;
                        if (module == 0) {
                            startIndex = mEvents.get(mDownloadIndex).bean.getReadProgress();
                            endIndex = startIndex + 50;
                        } else if (module == 1) {
                            startIndex = mEvents.get(mDownloadIndex).bean.getReadProgress();
                            endIndex = chapters.size();
                        } else if (module == 2) {
                            startIndex = 0;
                            endIndex = chapters.size();
                        }
                        totalCacheCount = endIndex - startIndex;

                        for (int s = startIndex; s < endIndex; s++) {
                            if (chapters.size() > s) {
                                strings.add(chapters.get(s).getLink());
                            } else {
                                break;
                            }
                        }

                        return strings;
                    }
                })
                .flatMap(new Function<ArrayList<String>, Observable<ZhuiShuBookContent>>() {
                    @Override
                    public Observable<ZhuiShuBookContent> apply(@NonNull ArrayList<String> strings) throws Exception {
                        return ZhuiShuBookModule.downloadBookContent(
                                mEvents.get(mDownloadIndex).bean.getBookName(), strings);
                    }
                })
                .subscribe(new Observer<ZhuiShuBookContent>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mDisposables.add(d);

                        event.isFinish = true;
                        event.msg = mEvents.get(mDownloadIndex).bean.getBookName() + " 0%";
                        EventBus.getDefault().post(event);

                    }

                    @Override
                    public void onNext(@NonNull ZhuiShuBookContent bookContent) {
                        if (bookContent == null) return;

                        mFinishIndex++;

                        int progress = ((int) ((mFinishIndex / (float) totalCacheCount) * 100));
                        if (progress > oldProgress && System.currentTimeMillis() - sendEventTime > 1000) {
                            oldProgress = progress;
                            sendEventTime = System.currentTimeMillis();

                            event.isFinish = false;
                            event.msg = mEvents.get(mDownloadIndex).bean.getBookName() + " " + progress + "%";
                            EventBus.getDefault().post(event);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        lock = true;
                    }

                    @Override
                    public void onComplete() {
                        lock = true;

                        event.isFinish = true;
                        event.msg = "";
                        EventBus.getDefault().post(event);

                        onDown();
                    }
                });
    }

}
