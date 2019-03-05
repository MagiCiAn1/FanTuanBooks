package com.anonymouser.book.module;

import com.anonymouser.book.bean.BookContent;
import com.anonymouser.book.bean.BookContentDao;
import com.anonymouser.book.bean.BookInfo;
import com.anonymouser.book.bean.BookInfoDao;
import com.anonymouser.book.bean.DaoSession;
import com.anonymouser.book.bean.UserInfo;
import com.anonymouser.book.bean.ZhuiShuBookContent;
import com.anonymouser.book.bean.ZhuiShuBookContentDao;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by YandZD on 2017/8/11.
 */

public class ZhuiShuBookDao {

    public static DaoSession mDaoSession;
    private static ZhuiShuBookDao mZhuiShuBookDao;

    private ZhuiShuBookDao() {

    }

    public static ZhuiShuBookDao newInstance() {
        if (mZhuiShuBookDao == null) {
            mDaoSession = BookDao.newInstance().mDaoSession;
            mZhuiShuBookDao = new ZhuiShuBookDao();
        }
        return mZhuiShuBookDao;
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



    public String loadContent(String link) {
        ZhuiShuBookContent content = mDaoSession.queryBuilder(ZhuiShuBookContent.class)
                .where(ZhuiShuBookContentDao.Properties.Link.eq(link))
                .unique();
        if (content == null)
            return null;

        return content.getContent();
    }

    public void saveContent(ZhuiShuBookContent bookContent) {
        synchronized (this) {
            mDaoSession.queryBuilder(ZhuiShuBookContent.class)
                    .where(ZhuiShuBookContentDao.Properties.Link.eq(bookContent.getLink())).buildDelete()
                    .executeDeleteWithoutDetachingEntities();

            mDaoSession.insert(bookContent);
        }
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


    //查询一本书下载的链接
    public List<String> queryDownloadBookContent(String bookName) {
        List<ZhuiShuBookContent> contents = mDaoSession.queryBuilder(ZhuiShuBookContent.class)
                .where(ZhuiShuBookContentDao.Properties.BookName.eq(bookName))
                .list();
        List<String> list = new ArrayList<>();
        for (ZhuiShuBookContent content : contents) {
            list.add(content.getLink());
        }
        return list;
    }


}

