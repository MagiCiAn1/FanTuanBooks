package com.anonymouser.book.presenter;

import android.text.TextUtils;

import com.anonymouser.book.bean.BookCaseBean;
import com.anonymouser.book.bean.BookInfo;
import com.anonymouser.book.bean.ChapterBean;
import com.anonymouser.book.bean.UserInfo;
import com.anonymouser.book.bean.ZhuiShuBookContent;
import com.anonymouser.book.bean.ZhuiShuChaptersBean;
import com.anonymouser.book.bean.ZhuiShuSourceBean;
import com.anonymouser.book.module.BookDao;
import com.anonymouser.book.module.BookModule;
import com.anonymouser.book.module.ZhuiShuBookModule;
import com.anonymouser.book.view.ReadZhuiShuActivity;
import com.google.gson.Gson;
import com.lzy.okgo.model.Response;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by YandZD on 2017/8/11.
 */

public class ReadZhuiShuPresenter {
    private List<ZhuiShuChaptersBean.ChaptersBean> mChapters;
    public ReadZhuiShuActivity mView;
    private String mSource;
    private ArrayList<Disposable> mDisposables = new ArrayList<>();
    private String mBookName;
    public ArrayList<ZhuiShuSourceBean> mSourceList = new ArrayList<>();  //能使用源的列表
    public List<String> mDownloadLinks;
    private Disposable mCacheDisposable;

    public ReadZhuiShuPresenter(ReadZhuiShuActivity activity) {
        mView = activity;
    }

    public void baseReading(final String tag, final int index, String id, final String bookName) {
        mBookName = bookName;

        ZhuiShuBookModule
                .getSourceListBean(id)
                .map(new Function<Response<ZhuiShuSourceBean[]>, ArrayList<ZhuiShuSourceBean>>() {
                    @Override
                    public ArrayList<ZhuiShuSourceBean> apply(@NonNull Response<ZhuiShuSourceBean[]> response) throws Exception {
                        ArrayList<String> nonSource = new ArrayList<>();        //不能使用的源名
                        nonSource.add("zhuishuvip");
                        nonSource.add("my176");

                        //提取能使用的源
                        for (int i = 0; i < response.body().length; i++) {
                            if (!nonSource.contains(response.body()[i].getSource())) {
                                mSourceList.add(response.body()[i]);
                            }
                        }

                        if (mSourceList.size() == 0) {
                            throw new Exception();
                        }

                        return mSourceList;
                    }
                })
                .flatMap(new Function<ArrayList<ZhuiShuSourceBean>, Observable<Response<ZhuiShuChaptersBean>>>() {
                    @Override
                    public Observable<Response<ZhuiShuChaptersBean>> apply(@NonNull ArrayList<ZhuiShuSourceBean> o) throws Exception {
                        String sourceId = "";
                        mSource = tag;
                        if (!TextUtils.isEmpty(mSource)) {
                            //如果指定使用源，判断这个源是否是可以使用的
                            for (ZhuiShuSourceBean item : o) {
                                if (item.getSource().equals(mSource)) {
                                    sourceId = item.get_id();
                                    break;
                                }
                            }
                        }

                        if (TextUtils.isEmpty(sourceId)) {
                            mSource = o.get(0).getSource();
                            sourceId = o.get(0).get_id();
                        }

                        mView.setMTag(mSource);
                        return ZhuiShuBookModule.getChapters(sourceId);
                    }
                })
                .flatMap(new Function<Response<ZhuiShuChaptersBean>, Observable<ZhuiShuBookContent>>() {
                    @Override
                    public Observable<ZhuiShuBookContent> apply(@NonNull Response<ZhuiShuChaptersBean> response) throws Exception {
                        BookInfo info = new BookInfo();
                        info.setBookName(bookName);
                        info.setTag(mSource);
                        info.setList(new Gson().toJson(response.body().getChapters()));
                        info.setUpdateTime(System.currentTimeMillis() + "");
                        BookModule.saveBookInfo(info);

                        mChapters = response.body().getChapters();
                        if (mView.getMBookIndex() < 0) {
                            mView.setMBookIndex(0);
                        } else if (mView.getMBookIndex() >= mChapters.size()) {
                            mView.setMBookIndex(mChapters.size() - 1);
                        }

                        int chapterIndex = index;
                        if (chapterIndex >= response.body().getChapters().size()) {
                            mView.setMBookIndex(response.body().getChapters().size() - 1);
                            chapterIndex = response.body().getChapters().size() - 1;
                        }


                        return ZhuiShuBookModule.getBookContent(mBookName, response.body().getChapters().get(chapterIndex).getLink());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<ZhuiShuBookContent>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mDisposables.add(d);
                    }

                    @Override
                    public void onNext(@NonNull ZhuiShuBookContent zhuiShuContentBean) {
                        actionCache(index);

                        mView.setPager();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        List<BookInfo> infos = BookDao.newInstance().loadBookInfo(tag, bookName);
                        if (infos == null || infos.size() == 0) {
                            mView.setPromptChageSource();
                            return;
                        }
                        ZhuiShuChaptersBean.ChaptersBean[] beans = new Gson().fromJson(infos.get(0).getList(), ZhuiShuChaptersBean.ChaptersBean[].class);
                        mChapters = Arrays.asList(beans);

                        int chapterIndex = index;
                        if (chapterIndex >= beans.length) {
                            mView.setMBookIndex(beans.length - 1);
                            chapterIndex = beans.length - 1;
                        }

                        ZhuiShuBookModule.getBookContent(mBookName, beans[chapterIndex].getLink())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<ZhuiShuBookContent>() {
                                    @Override
                                    public void onSubscribe(@NonNull Disposable d) {

                                    }

                                    @Override
                                    public void onNext(@NonNull ZhuiShuBookContent zhuiShuBookContent) {
                                        actionCache(index);

                                        mView.setPager();
                                    }

                                    @Override
                                    public void onError(@NonNull Throwable e) {
                                        mView.setPromptChageSource();
                                    }

                                    @Override
                                    public void onComplete() {
                                        mView.setPager();
                                    }
                                });
                    }

                    @Override
                    public void onComplete() {
                        mView.setPager();
                    }
                });
        mDownloadLinks = ZhuiShuBookModule.getBookDownloadList(bookName);
    }

    private void actionCache(int index) {
        ArrayList<String> links = new ArrayList<>();
        for (int x = index - 1; x <= index + 5; x++) {
            if (x >= mChapters.size() || x < 0) {
                continue;
            }

            links.add(mChapters.get(x).getLink());
        }

        if (mCacheDisposable != null && mCacheDisposable.isDisposed()) {
            mCacheDisposable.dispose();
        }

        ZhuiShuBookModule.getBookContent(mBookName, links)
                .subscribe(new Observer<ZhuiShuBookContent>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mCacheDisposable = d;
                    }

                    @Override
                    public void onNext(@NonNull ZhuiShuBookContent zhuiShuBookContent) {
                        EventBus.getDefault().post(zhuiShuBookContent);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        System.out.println(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    public UserInfo loadUserInfo() {
        return BookModule.loadUserInfo();
    }

    public void saveUserInfo(UserInfo info) {
        BookModule.saveUserInfo(info);
    }

    public List<ZhuiShuChaptersBean.ChaptersBean> getBookDirectory() {
        return mChapters;
    }

    public boolean isDownload(@NotNull String link) {

        return mDownloadLinks.contains(link);
    }

    @NotNull
    public String getChapterLink(int mBookIndex) {
        if (mBookIndex >= mChapters.size() || mBookIndex < 0) {
            return "";
        }

        return mChapters.get(mBookIndex).getLink();
    }

    @Nullable
    public ChapterBean getBookContent(int bookIndex) {
        if (bookIndex >= mChapters.size() || bookIndex < 0) {
            return null;
        }

        actionCache(bookIndex);

        ChapterBean bean = new ChapterBean();
        bean.setTitle(mChapters.get(bookIndex).getTitle());
        String content = ZhuiShuBookModule.getBookContentDao(mChapters.get(bookIndex).getLink());
        if (TextUtils.isEmpty(content)) {
            content = "";
        }
        bean.setContent(content);

        if (TextUtils.isEmpty(bean.getContent())) {
            cacheClickChapter(bookIndex);
        }

        return bean;
    }

    //缓存点击的目录（点击目录里的章节时，本机还没有缓存）
    private void cacheClickChapter(final int index) {
        String link = mChapters.get(index).getLink();
        ZhuiShuBookModule.getBookContent(mBookName, link)
                .subscribe(new Observer<ZhuiShuBookContent>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mDisposables.add(d);
                    }

                    @Override
                    public void onNext(@NonNull ZhuiShuBookContent zhuiShuBookContent) {
                        EventBus.getDefault().post(zhuiShuBookContent);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        actionCache(index);
                    }
                });
    }

    public void notifyPageIndex(int bitmapIndex, int chapterIndex, @NotNull String chapterTitle, @NotNull BookCaseBean bookCaseBean) {
        bookCaseBean.setReadPageIndex(bitmapIndex);
        bookCaseBean.setReadProgress(chapterIndex);
        bookCaseBean.setReadChapterTitle(chapterTitle);

        BookModule.updateBookCaseBook(bookCaseBean);
    }

    public void updateBookCaseBook(BookCaseBean book) {
        BookModule.updateBookCaseBook(book);
    }

    public void dispose() {
        for (Disposable x : mDisposables) {
            x.dispose();
        }
    }

}
