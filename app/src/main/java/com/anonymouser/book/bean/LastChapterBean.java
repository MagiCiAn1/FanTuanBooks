package com.anonymouser.book.bean;

/**
 * Created by YandZD on 2017/8/23.
 */

public class LastChapterBean {
    
    /**
     * _id : 531169b3173bfacb4904ca67
     * author : 耳根
     * referenceSource : sogou
     * updated : 2017-02-16T07:05:36.427Z
     * chaptersCount : 1609
     * lastChapter : 第十卷 我看沧海化桑田 第1614章 孤帆一片日边来！（终）
     */

    private String _id;
    private String author;
    private String referenceSource;
    private String updated;
    private int chaptersCount;
    private String lastChapter;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getReferenceSource() {
        return referenceSource;
    }

    public void setReferenceSource(String referenceSource) {
        this.referenceSource = referenceSource;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public int getChaptersCount() {
        return chaptersCount;
    }

    public void setChaptersCount(int chaptersCount) {
        this.chaptersCount = chaptersCount;
    }

    public String getLastChapter() {
        return lastChapter;
    }

    public void setLastChapter(String lastChapter) {
        this.lastChapter = lastChapter;
    }
}
