package com.anonymouser.book.module;

import android.text.TextUtils;

import com.anonymouser.book.bean.BookCaseBean;
import com.anonymouser.book.bean.BookContent;
import com.anonymouser.book.bean.BookInfo;
import com.anonymouser.book.bean.UserInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by YandZD on 2017/7/14.
 */
public class BookModule {

    private static Disposable notifyBookCaseDisposable;
    private static Disposable updateBookCaseBook;

    public static Scheduler mScheduler = null;
    public static Scheduler mDownloadScheduler = null;

    //"http://www.biquw.com/book/9/"
    public static Observable<BookInfo> getBookInfo(final String tag, final String baseLink, final String bookName) {
        return Observable.just(bookName)
                .map(new Function<String, BookInfo>() {
                    @Override
                    public BookInfo apply(@NonNull String s) throws Exception {
                        boolean flag = true;  //失败重试
                        int errorCount = 2;     //重试次数
                        int count = -1;
                        BookInfo bookInfo = null;
                        while (flag) {
                            try {
                                List<BookInfo> infos = BookDao.newInstance().loadBookInfo(tag, bookName);
                                if (infos != null && infos.size() > 0) {
//                                    return infos.get(0);
                                    bookInfo = infos.get(0);
                                    if (System.currentTimeMillis() - Long.parseLong(bookInfo.getUpdateTime()) <
                                            1000 * 60 * 60 * 3 && !TextUtils.isEmpty(bookInfo.getList())) {//* 60 * 60 * 3
                                        return bookInfo;
                                    }
                                }
                                //根据 baseLink 调用jar包 来获取章节->从服务器获取
                                BookLoadFactory bf = new BookLoadFactory(tag, baseLink);
                                BookInfo info = new BookInfo();
                                info.setBookName(bookName);
                                info.setList(bf.getZhangjie());
                                info.setUpdateTime(System.currentTimeMillis() + "");
                                info.setTag(tag);


                                //不为空的时候才抛出
                                if (info != null && !TextUtils.isEmpty(info.getList())) {
                                    BookDao.newInstance().saveBookInfo(info);
                                    return info;
                                } else {
                                    count++;
                                }
                            } catch (Exception e) {
                                count++;
                                System.out.println("");
                            }
                            if (count >= errorCount) {
                                flag = false;
                            }
                        }

                        if (bookInfo != null) {
                            return bookInfo;
                        } else {
                            throw new Exception();
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    //获取某一章节内容
    public static Observable<BookContent> getBookContent(String tag, String bookName, String link) {
        List<String> strings = new ArrayList<>();
        strings.add(link);
        return getBookContent(tag, bookName, strings);
    }

    //临时的章节
    public static Observable<BookContent> getBookContent(final String tag, final String bookName
            , List<String> link) {
        if (mScheduler == null) {
            mScheduler = Schedulers.from(Executors.newFixedThreadPool(3));
        }

        return Observable.fromIterable(link)
                .flatMap(new Function<String, ObservableSource<BookContent>>() {
                    @Override
                    public ObservableSource<BookContent> apply(@NonNull String s) throws Exception {
                        return Observable
                                .just(s)
                                .subscribeOn(mScheduler)     //线程池
                                .map(new Function<String, BookContent>() {
                                    @Override
                                    public BookContent apply(@NonNull String s) throws Exception {
                                        boolean flag = true;  //失败重试
                                        int errorCount = 1;     //重试次数
                                        int count = -1;
                                        while (flag) {
                                            try {
                                                count++;
                                                boolean isNeedSave = false;
                                                //先查询数据库，如果有则不在网上爬
                                                String content = BookDao.newInstance().loadContext(s);
                                                if (TextUtils.isEmpty(content)) {
                                                    ///根据章节link 去调用jar包爬取章节 --> 访问服务器获取章节内容
                                                    BookLoadFactory bf = new BookLoadFactory(tag, s);
                                                    content = bf.getBook();
                                                    isNeedSave = true;
                                                }

                                                BookContent bookContent = new BookContent();
                                                bookContent.setBookName(bookName);
                                                bookContent.setLink(s);
                                                bookContent.setContent(content);
                                                bookContent.setTime(System.currentTimeMillis());

                                                if (TextUtils.isEmpty(content)) {
                                                    System.out.println(content + "      ---getBookContent");
                                                    throw new Exception();
                                                }

                                                if (isNeedSave)
                                                    BookDao.newInstance().saveContent(bookContent);
                                                return bookContent;
                                            } catch (Exception e) {

                                            }
                                            if (count >= errorCount) {
                                                flag = false;
                                            }
                                        }

                                        throw new Exception();

                                    }
                                })
                                .observeOn(Schedulers.io())
                                .onExceptionResumeNext(new Observable<BookContent>() {
                                    @Override
                                    protected void subscribeActual(Observer<? super BookContent> observer) {
                                        System.out.println("Error-----缓存有错误");
                                        observer.onComplete();
                                    }
                                });

                    }
                })
                .observeOn(Schedulers.io());

    }

    public static Observable<BookContent> downloadBookContent(final String tag, final String bookName
            , List<String> link) {

        if (mDownloadScheduler == null) {
            mDownloadScheduler = Schedulers.from(Executors.newFixedThreadPool(6));
        }

        return Observable.fromIterable(link)
                .flatMap(new Function<String, ObservableSource<BookContent>>() {
                    @Override
                    public ObservableSource<BookContent> apply(@NonNull String s) throws Exception {
                        return Observable
                                .just(s)
                                .subscribeOn(mDownloadScheduler)     //线程池
                                .map(new Function<String, BookContent>() {
                                    @Override
                                    public BookContent apply(@NonNull String s) throws Exception {
                                        boolean flag = true;  //失败重试
                                        int errorCount = 1;     //重试次数
                                        int count = -1;
                                        while (flag) {
                                            try {
                                                count++;
                                                boolean isNeedSave = false;
                                                //先查询数据库，如果有则不在网上爬
                                                String content = BookDao.newInstance().loadContext(s);
                                                if (TextUtils.isEmpty(content)) {
                                                    BookLoadFactory bf = new BookLoadFactory(tag, s);
                                                    content = bf.getBook();
                                                    isNeedSave = true;
                                                }

                                                BookContent bookContent = new BookContent();
                                                bookContent.setBookName(bookName);
                                                bookContent.setLink(s);
                                                bookContent.setContent(content);
                                                bookContent.setTime(System.currentTimeMillis());

                                                if (TextUtils.isEmpty(content)) {
                                                    System.out.println(content + "      ---getBookContent");
                                                    throw new Exception();
                                                }

                                                if (isNeedSave)
                                                    BookDao.newInstance().saveContent(bookContent);
                                                return bookContent;
                                            } catch (Exception e) {

                                            }
                                            if (count >= errorCount) {
                                                flag = false;
                                            }
                                        }

                                        throw new Exception();

                                    }
                                })
                                .observeOn(Schedulers.io())
                                .onExceptionResumeNext(new Observable<BookContent>() {
                                    @Override
                                    protected void subscribeActual(Observer<? super BookContent> observer) {
                                        System.out.println("Error-----缓存有错误");
                                        observer.onComplete();
                                    }
                                });

                    }
                })
                .observeOn(Schedulers.io());

    }


    public static String getBookContentDao(String link) {
        String context = BookDao.newInstance().loadContext(link);
        return context;
    }

    public static void saveBookInfo(BookInfo info) {
        BookDao.newInstance().saveBookInfo(info);
    }

    public static void saveUserInfo(UserInfo info) {
        BookDao.newInstance().saveUserInfo(info);
    }

    public static UserInfo loadUserInfo() {
        return BookDao.newInstance().loadUserInfo();
    }

    public static void saveBookCaseBean(BookCaseBean bean) {
        BookDao.newInstance().addBookCaseBean(bean);
    }

    public static void updateBookCaseBook(final BookCaseBean bean) {
        if (updateBookCaseBook != null) updateBookCaseBook.dispose();
        Observable.just("")
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        updateBookCaseBook = d;
                    }

                    @Override
                    public void onNext(@NonNull String o) {
                        BookDao.newInstance().updateBookCaseBean(bean);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public static void deleteBookCaseBean(BookCaseBean bean) {
        BookDao.newInstance().deleteBookCaseBean(bean);
    }

    public static void notfiyBookCase() {
        BookDao.newInstance().notfiyBookCase();
    }


    public static BookCaseBean getBookCaseBean(String bookName) {
        return BookDao.newInstance().queryBookCaseBean(bookName);
    }


    /**
     * 判断是否已经缓存章节
     **/
    public static boolean hasCacheContent(String link) {
        if (TextUtils.isEmpty(BookDao.newInstance().loadContext(link))) {
            return false;
        }
        return true;
    }

    public static void notifyDataBase(List<BookCaseBean> beans) {
        if (notifyBookCaseDisposable != null) notifyBookCaseDisposable.dispose();
        Observable.fromIterable(beans)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Observer<BookCaseBean>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        notifyBookCaseDisposable = d;
                        BookDao.newInstance().deleteClass(BookCaseBean.class);
                    }

                    @Override
                    public void onNext(@NonNull BookCaseBean bean) {
                        BookDao.newInstance().addBookCaseBean(bean);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //获取一本书下载列表
    public static List<String> getBookDownloadList(String bookName) {
        return BookDao.newInstance().queryDownloadBookContent(bookName);
    }

    /**
     * 移除临时数据
     */
    public static void removeTempDataBase() {
        BookDao.newInstance().removeTempData();
    }

}
