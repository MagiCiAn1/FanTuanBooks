package com.anonymouser.book.presenter;

import com.anonymouser.book.bean.BookCaseBean;
import com.anonymouser.book.bean.ChapterBean;
import com.anonymouser.book.bean.UserInfo;
import com.anonymouser.book.bean.ZhuiShuChaptersBean;
import com.google.gson.JsonArray;

import java.util.List;

/**
 * Created by YandZD on 2017/9/18.
 */

interface BaseReadPresenter {
    UserInfo loadUserInfo();

    void saveUserInfo(UserInfo info);

    void baseReading(String tag, int index, String baseLink, String bookName);

    ChapterBean getBookContent(String tag, int bookIndex);

    void actionCache(String tag, int index);

    void cacheClickChapter(String tag, int index);

    String getChapterLink(int index);

    String getBookName();

    JsonArray getBookDirectory();

    List<ZhuiShuChaptersBean.ChaptersBean> getBookDirectory(boolean isZhuishu);

    void notifyPageIndex(int bitmapIndex, int chapterIndex, String chapterTitle, BookCaseBean bookCaseBean);

    void updateBookCaseBook(BookCaseBean book);

    void dispose();

    boolean isDownload(String link);
}
