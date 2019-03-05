package com.anonymouser.book.module;

import android.database.sqlite.SQLiteDatabase;

import com.anonymouser.book.BookApp;
import com.anonymouser.book.bean.BookCaseBean;
import com.anonymouser.book.bean.BookCaseBeanDao;
import com.anonymouser.book.bean.BookContent;
import com.anonymouser.book.bean.BookContentDao;
import com.anonymouser.book.bean.BookInfo;
import com.anonymouser.book.bean.BookInfoDao;
import com.anonymouser.book.bean.DaoMaster;
import com.anonymouser.book.bean.DaoSession;
import com.anonymouser.book.bean.NotifyBookcaseDataEvent;
import com.anonymouser.book.bean.UserInfo;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 保存书本信息到数据库
 * Created by YandZD on 2017/7/14.
 */

//
public class BookDao {

    public static DaoSession mDaoSession;
    private static BookDao mBookDao;

    private BookDao() {

    }

    public static BookDao newInstance() {
        if (mBookDao == null) {
            mBookDao = new BookDao();
            DaoMaster.DevOpenHelper openHelper = new DaoMaster.DevOpenHelper(BookApp.mContext, "db", null);
            SQLiteDatabase db = openHelper.getWritableDatabase();
            DaoMaster mDaoMaster = new DaoMaster(db);
            mDaoSession = mDaoMaster.newSession();
        }
        return mBookDao;
    }

    public void deleteClass(Class cls) {
        mDaoSession.deleteAll(cls);
    }

    public <T> List<T> loadAll(Class cls) {
        return mDaoSession.loadAll(cls);
    }


    /**
     * 基本信息：书id，书名，作者，简介，图片链接，map<目录、链接>
     */
    public void saveBookInfo(BookInfo info) {
        mDaoSession.queryBuilder(BookInfo.class)
                .where(BookInfoDao.Properties.BookName.eq(info.getBookName()))
                .buildDelete()
                .executeDeleteWithoutDetachingEntities();

        mDaoSession.insert(info);
    }

    public List<BookInfo> loadBookInfo(String tag, String bookName) {
        List<BookInfo> infos = mDaoSession.queryBuilder(BookInfo.class)
                .where(BookInfoDao.Properties.Tag.eq(tag), BookInfoDao.Properties.BookName.eq(bookName))
                .list();

        if (infos != null && infos.size() > 0) {
            return infos;
        }
        return null;
    }

    public void saveContent(BookContent bookContent) {
        synchronized (this) {
            mDaoSession.queryBuilder(BookContent.class)
                    .where(BookContentDao.Properties.Link.eq(bookContent.getLink())).buildDelete()
                    .executeDeleteWithoutDetachingEntities();

            mDaoSession.insert(bookContent);
        }
    }

    public String loadContext(String link) {
        //        link = new String(Base64.encode(link.getBytes(), Base64.DEFAULT));
        link = link.replace("\"", "");

        BookContent content = mDaoSession.queryBuilder(BookContent.class)
                .where(BookContentDao.Properties.Link.eq(link))
                .unique();
        if (content == null)
            return null;

        return content.getContent();
    }


    //保存用户信息，字体颜色，背景
    public void saveUserInfo(UserInfo info) {
        mDaoSession.deleteAll(UserInfo.class);
        mDaoSession.insert(info);

    }

    public UserInfo loadUserInfo() {
        List<UserInfo> infos = mDaoSession.loadAll(UserInfo.class);
        UserInfo info = new UserInfo();
        if (infos != null && infos.size() > 0) {
            info = infos.get(0);
        }
        return info;
    }

    //书架操作 添加
    public void addBookCaseBean(BookCaseBean bean) {
        List<BookCaseBean> contents = mDaoSession.queryBuilder(BookCaseBean.class)
                .where(BookCaseBeanDao.Properties.BookName.eq(bean.getBookName()))
                .list();
        if (contents != null && contents.size() > 0) return;

        mDaoSession.insert(bean);


        //是否需要发送刷新事件
//        if (isNeedNotify)
//            notfiyBookCase();
    }

    //书架操作 删除
    public void deleteBookCaseBean(BookCaseBean bean) {
        mDaoSession.delete(bean);

//        notfiyBookCase();
    }

    //书架操作 更新
    public void updateBookCaseBean(BookCaseBean bean) {
        mDaoSession.update(bean);

//        notfiyBookCase();
    }

    //书架操作 查单个
    public BookCaseBean queryBookCaseBean(String bookName) {
        return mDaoSession.load(BookCaseBean.class, bookName);
    }

    //查询一本书下载的链接
    public List<String> queryDownloadBookContent(String bookName) {
        List<BookContent> contents = mDaoSession.queryBuilder(BookContent.class)
                .where(BookContentDao.Properties.BookName.eq(bookName))
                .list();
        List<String> list = new ArrayList<>();
        for (BookContent content : contents) {
            list.add(content.getLink());
        }
        return list;
    }

    public void notfiyBookCase() {
        NotifyBookcaseDataEvent event = new NotifyBookcaseDataEvent();
        event.beans = loadAll(BookCaseBean.class);
        EventBus.getDefault().post(event);
    }

    //删除临时
    public void removeTempData() {
        new Thread() {
            public void run() {
//                long bookInfoOverTime = 1000 * 60 * 60 * 3;   //缓存3小时，书本信息
                //删除不在书架上的临时书
                String deleteNotInBookCase = "DELETE FROM BOOK_CONTENT WHERE BOOK_CONTENT.BOOK_NAME IN (SELECT BOOK_NAME FROM BOOK_CONTENT WHERE BOOK_CONTENT.BOOK_NAME NOT IN (select BOOK_CASE_BEAN.BOOK_NAME from BOOK_CASE_BEAN))";
                mDaoSession.getDatabase().execSQL(deleteNotInBookCase);
                //删除超时保存的书本信息
//                String deleteOverTimeBookInfo = String.format("DELETE FROM BOOK_INFO WHERE %d - BOOK_INFO.UPDATE_TIME > %d", System.currentTimeMillis(), bookInfoOverTime);
                String deleteOverTimeBookInfo = "DELETE FROM BOOK_INFO WHERE BOOK_INFO.BOOK_NAME IN (SELECT BOOK_NAME FROM BOOK_INFO WHERE BOOK_INFO.BOOK_NAME NOT IN (select BOOK_CASE_BEAN.BOOK_NAME from BOOK_CASE_BEAN))";
                mDaoSession.getDatabase().execSQL(deleteOverTimeBookInfo);
                String deleteNotInZhuiShuBookCase = "DELETE FROM ZHUI_SHU_BOOK_CONTENT WHERE ZHUI_SHU_BOOK_CONTENT.BOOK_NAME IN (SELECT BOOK_NAME FROM ZHUI_SHU_BOOK_CONTENT WHERE ZHUI_SHU_BOOK_CONTENT.BOOK_NAME NOT IN (select BOOK_CASE_BEAN.BOOK_NAME from BOOK_CASE_BEAN))";
                mDaoSession.getDatabase().execSQL(deleteNotInZhuiShuBookCase);
            }
        }.start();
    }
}
