package com.anonymouser.book.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;

/**
 * 书架bean
 * 书名、作者、阅读第几章、阅读章的第几页、阅读到的章节名字、是否正在缓存、baselink、使用源、图片链接
 * Created by YandZD on 2017/7/19.
 */
@Entity
public class BookCaseBean implements Serializable {
    @Transient
    private static final long serialVersionUID = 7244266643513254L;

    @Id
    private String bookName;
    private String auther;
    private int readProgress = 0;
    private int readPageIndex = 0;
    private String readChapterTitle = "";
    private boolean isTheCache = false; //是否正在缓存中，场景：缓存书本过程中途退出
    private String baseLink;
    private String useSource;
    private String img;
    private String cacheStartEnd;       //缓存的范围如  9,100

    private String zhuiShuId;
    private boolean isZhuiShu = false;          //是否是追书

    @Generated(hash = 77364277)
    public BookCaseBean(String bookName, String auther, int readProgress,
            int readPageIndex, String readChapterTitle, boolean isTheCache,
            String baseLink, String useSource, String img, String cacheStartEnd,
            String zhuiShuId, boolean isZhuiShu) {
        this.bookName = bookName;
        this.auther = auther;
        this.readProgress = readProgress;
        this.readPageIndex = readPageIndex;
        this.readChapterTitle = readChapterTitle;
        this.isTheCache = isTheCache;
        this.baseLink = baseLink;
        this.useSource = useSource;
        this.img = img;
        this.cacheStartEnd = cacheStartEnd;
        this.zhuiShuId = zhuiShuId;
        this.isZhuiShu = isZhuiShu;
    }

    @Generated(hash = 1012353176)
    public BookCaseBean() {
    }

    public String getBookName() {
        return this.bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getAuther() {
        return this.auther;
    }

    public void setAuther(String auther) {
        this.auther = auther;
    }

    public int getReadProgress() {
        return this.readProgress;
    }

    public void setReadProgress(int readProgress) {
        this.readProgress = readProgress;
    }

    public int getReadPageIndex() {
        return this.readPageIndex;
    }

    public void setReadPageIndex(int readPageIndex) {
        this.readPageIndex = readPageIndex;
    }

    public String getReadChapterTitle() {
        return this.readChapterTitle;
    }

    public void setReadChapterTitle(String readChapterTitle) {
        this.readChapterTitle = readChapterTitle;
    }

    public boolean getIsTheCache() {
        return this.isTheCache;
    }

    public void setIsTheCache(boolean isTheCache) {
        this.isTheCache = isTheCache;
    }

    public String getBaseLink() {
        return this.baseLink;
    }

    public void setBaseLink(String baseLink) {
        this.baseLink = baseLink;
    }

    public String getUseSource() {
        return this.useSource;
    }

    public void setUseSource(String useSource) {
        this.useSource = useSource;
    }

    public String getImg() {
        return this.img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getCacheStartEnd() {
        return this.cacheStartEnd;
    }

    public void setCacheStartEnd(String cacheStartEnd) {
        this.cacheStartEnd = cacheStartEnd;
    }

    public boolean getIsZhuiShu() {
        return this.isZhuiShu;
    }

    public void setIsZhuiShu(boolean isZhuiShu) {
        this.isZhuiShu = isZhuiShu;
    }

    public String getZhuiShuId() {
        return this.zhuiShuId;
    }

    public void setZhuiShuId(String zhuiShuId) {
        this.zhuiShuId = zhuiShuId;
    }

}
