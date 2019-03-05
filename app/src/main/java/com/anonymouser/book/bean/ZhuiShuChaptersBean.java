package com.anonymouser.book.bean;

import java.util.List;

/**
 * 追书章节
 * Created by YandZD on 2017/8/11.
 */

public class ZhuiShuChaptersBean {


    /**
     * _id : 568fef99adb27bfb4b3a58dc
     * name : 优质书源
     * link : http://vip.zhuishushenqi.com/toc/568fef99adb27bfb4b3a58dc
     * book : 548d9c17eb0337ee6df738f5
     * chapters : [{"title":"正文 第2315章 为什么他还不走？","id":"598cec10ef5ab59644baa14e","link":"http://vip.zhuishushenqi.com/chapter/598cec10ef5ab59644baa14e?cv=1502407696921","totalpage":0,"partsize":0,"currency":15,"unreadble":false,"isVip":true}]
     * updated : 2017-08-10T23:28:16.938Z
     * host : vip.zhuishushenqi.com
     */

    private String _id;
    private String name;
    private String link;
    private String book;
    private String updated;
    private String host;
    private List<ChaptersBean> chapters;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getBook() {
        return book;
    }

    public void setBook(String book) {
        this.book = book;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public List<ChaptersBean> getChapters() {
        return chapters;
    }

    public void setChapters(List<ChaptersBean> chapters) {
        this.chapters = chapters;
    }

    public static class ChaptersBean {
        /**
         * title : 正文 第2315章 为什么他还不走？
         * id : 598cec10ef5ab59644baa14e
         * link : http://vip.zhuishushenqi.com/chapter/598cec10ef5ab59644baa14e?cv=1502407696921
         * totalpage : 0
         * partsize : 0
         * currency : 15
         * unreadble : false
         * isVip : true
         */

        private String title;
        private String id;
        private String link;
        private int totalpage;
        private int partsize;
        private int currency;
        private boolean unreadble;
        private boolean isVip;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public int getTotalpage() {
            return totalpage;
        }

        public void setTotalpage(int totalpage) {
            this.totalpage = totalpage;
        }

        public int getPartsize() {
            return partsize;
        }

        public void setPartsize(int partsize) {
            this.partsize = partsize;
        }

        public int getCurrency() {
            return currency;
        }

        public void setCurrency(int currency) {
            this.currency = currency;
        }

        public boolean isUnreadble() {
            return unreadble;
        }

        public void setUnreadble(boolean unreadble) {
            this.unreadble = unreadble;
        }

        public boolean isIsVip() {
            return isVip;
        }

        public void setIsVip(boolean isVip) {
            this.isVip = isVip;
        }
    }
}
