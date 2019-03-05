package com.anonymouser.book.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * 临时保存的书本content
 * Created by YandZD on 2017/7/14.
 */
@Entity
public class ZhuiShuBookContent {
    private String bookName;
    @Id
    private String link;
    private String content;
    private long time;
    @Generated(hash = 571793474)
    public ZhuiShuBookContent(String bookName, String link, String content,
            long time) {
        this.bookName = bookName;
        this.link = link;
        this.content = content;
        this.time = time;
    }
    @Generated(hash = 2105938338)
    public ZhuiShuBookContent() {
    }
    public String getBookName() {
        return this.bookName;
    }
    public void setBookName(String bookName) {
        this.bookName = bookName;
    }
    public String getLink() {
        return this.link;
    }
    public void setLink(String link) {
        this.link = link;
    }
    public String getContent() {
        return this.content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public long getTime() {
        return this.time;
    }
    public void setTime(long time) {
        this.time = time;
    }
}
