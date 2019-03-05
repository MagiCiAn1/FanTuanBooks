package com.anonymouser.book.bean;

import java.io.Serializable;

/**
 * Created by YandZD on 2017/7/19.
 */

public class SearchBookInfoBean implements Serializable {
    private static final long serialVersionUID = 7429142654654L;
    /**
     * bookName : 圣墟
     * author : 辰东
     * intro : 在破败中崛起，在寂灭中复苏。    沧海成尘，雷电枯竭，那一缕幽雾又一次临近大地，世间的枷锁被打开了，一个全新的世界就此揭开神秘的一角……    &
     * img : http://www.biquw.com/files/article/image//0/979/979s.jpg
     * baseLink : [{"tag":"biquw","url":"http://www.biquw.com","link":"http://www.biquw.com/book/979/"}]
     */

    private String bookName;
    private String author;
    private String intro;
    private String img;
    private String type;
    private String baseLink;
    private String tag = new String("Spider");
    private int mBookIndex = 0;

    private String id = "";      //追书搜出来的才有Id
    private boolean isZhuiShu = false;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getmBookIndex() {
        return mBookIndex;
    }

    public void setmBookIndex(int mBookIndex) {
        this.mBookIndex = mBookIndex;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getBaseLink() {
        return baseLink;
    }

    public void setBaseLink(String baseLink) {
        this.baseLink = baseLink;
    }

    public String getType() {
        return type;
    }

    public void setType(String bookType) {
        this.type = bookType;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public boolean isZhuiShu() {
        return isZhuiShu;
    }

    public void setZhuiShu(boolean zhuiShu) {
        isZhuiShu = zhuiShu;
    }

    public class BaseLink {
        private String tag;
        private String url;
        private String link;

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }
    }
}
