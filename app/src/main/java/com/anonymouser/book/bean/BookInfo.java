package com.anonymouser.book.bean;

import android.text.TextUtils;

import com.anonymouser.book.utlis.DataBaseUtil;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by YandZD on 2017/7/14.
 */
@Entity
public class BookInfo {
    //书名，作者，简介，图片链接，map<目录、链接>
    private String bookName;
    private String tag;
    private String author;
    private String introduc;
    private String imgLink;
    private String list;
    private String updateTime;
    private boolean isZhuiShu;  //判断是否是追书的接口

    @Generated(hash = 1582084718)
    public BookInfo(String bookName, String tag, String author, String introduc,
                    String imgLink, String list, String updateTime, boolean isZhuiShu) {
        this.bookName = bookName;
        this.tag = tag;
        this.author = author;
        this.introduc = introduc;
        this.imgLink = imgLink;
        this.list = list;
        this.updateTime = updateTime;
        this.isZhuiShu = isZhuiShu;
    }

    @Generated(hash = 1952025412)
    public BookInfo() {
    }

    public String getBookName() {
        return this.bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getTag() {
        return this.tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIntroduc() {
        return this.introduc;
    }

    public void setIntroduc(String introduc) {
        this.introduc = introduc;
    }

    public String getImgLink() {
        return this.imgLink;
    }

    public void setImgLink(String imgLink) {
        this.imgLink = imgLink;
    }

    public String getList() {
        return this.list;
    }

    public void setList(String list) {
        this.list = list;
    }

    public String getUpdateTime() {
        return this.updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public boolean getIsZhuiShu() {
        return this.isZhuiShu;
    }

    public void setIsZhuiShu(boolean isZhuiShu) {
        this.isZhuiShu = isZhuiShu;
    }


}
