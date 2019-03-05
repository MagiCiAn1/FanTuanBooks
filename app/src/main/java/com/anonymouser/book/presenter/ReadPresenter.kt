package com.anonymouser.book.presenter

import android.text.TextUtils
import com.anonymouser.book.bean.*
import com.anonymouser.book.module.BookDao
import com.anonymouser.book.module.BookModule
import com.anonymouser.book.view.ReadActivity
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus

/**
 *
 * Created by YandZD on 2017/7/14.
 */
class ReadPresenter {
    var mBaseIndex = 0
    var mCacheDisposable: Disposable? = null    //缓存的回调（Rx） 用于结束
    var mView: ReadActivity? = null
    var mBookInfo: BookInfo? = null
    var mBookList: JsonArray? = null
    var mDownloadLinks: List<String>? = null
    var mDisposables = ArrayList<Disposable>()

    constructor(view: ReadActivity) {
        mView = view
    }

    fun loadUserInfo(): UserInfo {
        return BookModule.loadUserInfo()
    }

    fun saveUserInfo(info: UserInfo) {
        BookModule.saveUserInfo(info)
    }

    /**
     * 获取开始阅读的必要条件
     * 1、书的基本信息
     * 2、阅读进度
     * 3、阅读的首章
     */
    fun baseReading(tag: String, index: Int, baseLink: String, bookName: String) {
        mBaseIndex = index

        BookModule.getBookInfo(tag, baseLink, bookName)
                .subscribeOn(Schedulers.io())
                .flatMap { t: BookInfo ->
                    BookModule.saveBookInfo(t)

                    mDownloadLinks = BookModule.getBookDownloadList(t.bookName)
                    mBookInfo = t
                    mBookList = JsonParser().parse(mBookInfo?.list) as JsonArray

                    mBookList ?: throw Exception()

                    var tempIndex = index
                    if (tempIndex >= mBookList!!.size()) {
                        tempIndex = mBookList!!.size() - 1
                        mView?.mBookIndex = tempIndex
                    }

                    if (tempIndex < 0) {
                        tempIndex = 0
                    }

                    var link = (mBookList?.get(tempIndex) as JsonObject)["link"].toString()

                    link = link.replace("\"", "")
//                    println(link)

                    //得到显示的首章
                    BookModule.getBookContent(tag, bookName, link)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<BookContent> {
                    override fun onSubscribe(d: Disposable) {
                        mDisposables.add(d)
                    }

                    override fun onNext(t: BookContent) {
                        BookDao.newInstance().saveContent(t)
                        mView?.setPager()

                        actionCache(tag, index)
                    }

                    override fun onError(e: Throwable) {
//                        mView?.setPager()
                        //需要换源
                        mView?.setPromptChageSource()
                    }

                    override fun onComplete() {
                    }

                })
    }


    fun getBookContent(tag: String, bookIndex: Int): ChapterBean? {
        if (bookIndex >= mBookList?.size()!! || bookIndex < 0) {
            return null
        }
        var link = (mBookList?.get(bookIndex) as JsonObject)["link"].toString().replace("\"", "")

        actionCache(tag, bookIndex)

        var bean = ChapterBean()
        bean.title = (mBookList?.get(bookIndex) as JsonObject)["title"].toString().replace("\"", "")
        bean.content = BookModule.getBookContentDao(link) ?: ""

        if (TextUtils.isEmpty(bean.content)) {
            cacheClickChapter(tag, bookIndex)
        }
        return bean
    }

    fun actionCache(tag: String, index: Int) {
        var links = ArrayList<String>()
        for (x in index - 1..index + 5) {
            if (x >= mBookList?.size()!! || x < 0) {5
                continue
            }

            links.add((mBookList?.get(x) as JsonObject)["link"].toString().replace("\"", ""))
        }
        mCacheDisposable?.dispose()
        BookModule.getBookContent(tag, mBookInfo?.bookName, links).subscribe(object : Observer<BookContent> {
            override fun onComplete() {
            }

            override fun onError(e: Throwable) {
            }

            override fun onNext(t: BookContent) {
                EventBus.getDefault().post(t)
            }

            override fun onSubscribe(d: Disposable) {
                mCacheDisposable = d
                mDisposables.add(d)
            }
        })
    }

    //缓存点击的目录（点击目录里的章节时，本机还没有缓存）
    fun cacheClickChapter(tag: String, index: Int) {
        var link = (mBookList?.get(index) as JsonObject)["link"].toString()
        link = link.replace("\"", "")
        BookModule.getBookContent(tag, mBookInfo?.bookName, link).subscribe(object : Observer<BookContent> {
            override fun onNext(t: BookContent) {
//                mView?.loadThisPage()
                EventBus.getDefault().post(t)
            }

            override fun onSubscribe(d: Disposable) {
                mDisposables.add(d)
            }

            override fun onError(e: Throwable) {
            }

            override fun onComplete() {
                actionCache(tag, index)
            }
        })
    }

    //获取章节link
    fun getChapterLink(index: Int): String {
        if (index >= mBookList?.size()!! || index < 0) {
            return ""
        }
        var link = (mBookList?.get(index) as JsonObject)["link"].toString().replace("\"", "")
        return link
    }

    fun getBookName(): String {
        return mBookInfo?.bookName ?: ""
    }


    fun getBookDirectory(): JsonArray? {
        return mBookList
    }


    fun notifyPageIndex(bitmapIndex: Int, chapterIndex: Int, chapterTitle: String, bookCaseBean: BookCaseBean) {
        bookCaseBean.readPageIndex = bitmapIndex
        bookCaseBean.readProgress = chapterIndex
        bookCaseBean.readChapterTitle = chapterTitle

        BookModule.updateBookCaseBook(bookCaseBean)
    }

    fun updateBookCaseBook(book: BookCaseBean) {
        BookModule.updateBookCaseBook(book)
    }

    fun dispose() {
        for (x in mDisposables) {
            x.dispose()
        }
    }

    fun isDownload(link: String): Boolean {
        return mDownloadLinks?.contains(link) ?: false
    }
}


