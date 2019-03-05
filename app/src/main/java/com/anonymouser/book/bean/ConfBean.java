package com.anonymouser.book.bean;

import java.util.List;

/**
 * Created by YandZD on 2017/6/22.
 */
public class ConfBean {


    /**
     * app : {"version":"1","link":"http://yourbuffslonnol.com/BookService/updateApp"}
     * jar : [{"tag":"uctxt","link":"http://yourbuffslonnol.com/BookService/downloadJar?tag=uctxt","version":"1"},{"tag":"biquw","link":"http://yourbuffslonnol.com/BookService/downloadJar?tag=biquw","version":"1"},{"tag":"uukanshu","link":"http://yourbuffslonnol.com/BookService/downloadJar?tag=uukanshu","version":"1"},{"tag":"wangshuge","link":"http://yourbuffslonnol.com/BookService/downloadJar?tag=wangshuge","version":"1"}]
     */

    private AppBean app;
    private List<JarBean> jar;
    private boolean isShowAd;

    public boolean isShowAd() {
        return isShowAd;
    }

    public void setShowAd(boolean showAd) {
        isShowAd = showAd;
    }

    public AppBean getApp() {
        return app;
    }

    public void setApp(AppBean app) {
        this.app = app;
    }

    public List<JarBean> getJar() {
        return jar;
    }

    public void setJar(List<JarBean> jar) {
        this.jar = jar;
    }

    public static class AppBean {
        /**
         * version : 1
         * link : http://yourbuffslonnol.com/BookService/updateApp
         */

        private int version;
        private String link;

        public int getVersion() {
            return version;
        }

        public void setVersion(int version) {
            this.version = version;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }
    }

    public static class JarBean {
        /**
         * tag : uctxt
         * link : http://yourbuffslonnol.com/BookService/downloadJar?tag=uctxt
         * version : 1
         */

        private String tag;
        private String link;
        private int version;

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public int getVersion() {
            return version;
        }

        public void setVersion(int version) {
            this.version = version;
        }
    }
}
