package com.anonymouser.book.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 排行榜
 * Created by YandZD on 2017/7/24.
 */

public class RankBean {

    /**
     * ranking : {"_id":"54d4312d5f3c22ae137255a1","updated":"2017-07-23T21:20:15.518Z","title":"和阅读原创榜","tag":"cmreadYuanchuangbang","cover":"/ranking-cover/142319217152210","icon":"/cover/148945807649134","__v":468,"shortTitle":"和阅读榜","created":"2017-07-24T14:28:54.219Z","isSub":false,"collapse":true,"new":true,"gender":"male","priority":2250,"books":[{"_id":"50865988d7a545903b000009","author":"天蚕土豆","cover":"fdf","shortIntro":"dfdf","title":"斗破苍穹","site":"zhuishuvip","banned":0,"latelyFollower":61115,"retentionRatio":"54.45"}],"id":"54d4312d5f3c22ae137255a1","total":71}
     * ok : true
     */

    private RankingBean ranking;
    private boolean ok;

    public RankingBean getRanking() {
        return ranking;
    }

    public void setRanking(RankingBean ranking) {
        this.ranking = ranking;
    }

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public static class RankingBean {
        /**
         * _id : 54d4312d5f3c22ae137255a1
         * updated : 2017-07-23T21:20:15.518Z
         * title : 和阅读原创榜
         * tag : cmreadYuanchuangbang
         * cover : /ranking-cover/142319217152210
         * icon : /cover/148945807649134
         * __v : 468
         * shortTitle : 和阅读榜
         * created : 2017-07-24T14:28:54.219Z
         * isSub : false
         * collapse : true
         * new : true
         * gender : male
         * priority : 2250
         * books : [{"_id":"50865988d7a545903b000009","author":"天蚕土豆","cover":"fdf","shortIntro":"dfdf","title":"斗破苍穹","site":"zhuishuvip","banned":0,"latelyFollower":61115,"retentionRatio":"54.45"}]
         * id : 54d4312d5f3c22ae137255a1
         * total : 71
         */

        private String _id;
        private String updated;
        private String title;
        private String tag;
        private String cover;
        private String icon;
        private int __v;
        private String shortTitle;
        private String created;
        private boolean isSub;
        private boolean collapse;
        @SerializedName("new")
        private boolean newX;
        private String gender;
        private int priority;
        private String id;
        private int total;
        private List<BooksBean> books;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getUpdated() {
            return updated;
        }

        public void setUpdated(String updated) {
            this.updated = updated;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public String getCover() {
            return cover;
        }

        public void setCover(String cover) {
            this.cover = cover;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public int get__v() {
            return __v;
        }

        public void set__v(int __v) {
            this.__v = __v;
        }

        public String getShortTitle() {
            return shortTitle;
        }

        public void setShortTitle(String shortTitle) {
            this.shortTitle = shortTitle;
        }

        public String getCreated() {
            return created;
        }

        public void setCreated(String created) {
            this.created = created;
        }

        public boolean isIsSub() {
            return isSub;
        }

        public void setIsSub(boolean isSub) {
            this.isSub = isSub;
        }

        public boolean isCollapse() {
            return collapse;
        }

        public void setCollapse(boolean collapse) {
            this.collapse = collapse;
        }

        public boolean isNewX() {
            return newX;
        }

        public void setNewX(boolean newX) {
            this.newX = newX;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public int getPriority() {
            return priority;
        }

        public void setPriority(int priority) {
            this.priority = priority;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public List<BooksBean> getBooks() {
            return books;
        }

        public void setBooks(List<BooksBean> books) {
            this.books = books;
        }

        public static class BooksBean {
            /**
             * _id : 50865988d7a545903b000009
             * author : 天蚕土豆
             * cover : fdf
             * shortIntro : dfdf
             * title : 斗破苍穹
             * site : zhuishuvip
             * banned : 0
             * latelyFollower : 61115
             * retentionRatio : 54.45
             */

            private String _id;
            private String author;
            private String cover;
            private String shortIntro;
            private String title;
            private String site;
            private int banned;
            private int latelyFollower;
            private String retentionRatio;

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

            public String getCover() {
                return cover;
            }

            public void setCover(String cover) {
                this.cover = cover;
            }

            public String getShortIntro() {
                return shortIntro;
            }

            public void setShortIntro(String shortIntro) {
                this.shortIntro = shortIntro;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getSite() {
                return site;
            }

            public void setSite(String site) {
                this.site = site;
            }

            public int getBanned() {
                return banned;
            }

            public void setBanned(int banned) {
                this.banned = banned;
            }

            public int getLatelyFollower() {
                return latelyFollower;
            }

            public void setLatelyFollower(int latelyFollower) {
                this.latelyFollower = latelyFollower;
            }

            public String getRetentionRatio() {
                return retentionRatio;
            }

            public void setRetentionRatio(String retentionRatio) {
                this.retentionRatio = retentionRatio;
            }
        }
    }
}
