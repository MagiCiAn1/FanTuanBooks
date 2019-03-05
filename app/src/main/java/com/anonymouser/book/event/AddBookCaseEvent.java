package com.anonymouser.book.event;

import com.anonymouser.book.bean.BookCaseBean;
import com.anonymouser.book.bean.BookInfo;
import com.anonymouser.book.bean.SearchBookInfoBean;
import com.google.gson.Gson;

/**
 * 添加书本到书架事件
 * Created by YandZD on 2017/7/19.
 */

public class AddBookCaseEvent {
    public BookCaseBean mBookCaseBean = new BookCaseBean();

    public void setBeanFromSearchBookInfoBean(SearchBookInfoBean bean) {
        mBookCaseBean.setBookName(bean.getBookName());
        mBookCaseBean.setAuther(bean.getAuthor());
        mBookCaseBean.setBaseLink(bean.getBaseLink());
        mBookCaseBean.setImg(bean.getImg());
        mBookCaseBean.setIsTheCache(false);
        mBookCaseBean.setReadProgress(0);
        mBookCaseBean.setReadChapterTitle("");
        mBookCaseBean.setReadPageIndex(0);
        mBookCaseBean.setUseSource(bean.getTag());
        mBookCaseBean.setIsZhuiShu(bean.isZhuiShu());
        mBookCaseBean.setZhuiShuId(bean.getId());
    }

    /**
     * @param bean
     * @param readProgress     第几章节
     * @param readChapterTitle 章节名
     * @param readPageIndex    章节的第几页
     */
    public void setBeanFromSearchBookInfoBean(SearchBookInfoBean bean
            , int readProgress, String readChapterTitle, int readPageIndex, String bookLink) {
        mBookCaseBean.setBookName(bean.getBookName());
        mBookCaseBean.setAuther(bean.getAuthor());
        mBookCaseBean.setBaseLink(bean.getBaseLink());
        mBookCaseBean.setImg(bean.getImg());
        mBookCaseBean.setIsTheCache(false);
        mBookCaseBean.setReadProgress(readProgress);
        mBookCaseBean.setReadChapterTitle(readChapterTitle);
        mBookCaseBean.setReadPageIndex(readPageIndex);

        SearchBookInfoBean.BaseLink[] baseLinks = new Gson().fromJson(bean.getBaseLink(), SearchBookInfoBean.BaseLink[].class);
        if (baseLinks != null && baseLinks.length > 0) {
            for (SearchBookInfoBean.BaseLink baseLink : baseLinks) {
                if (baseLink.getLink().equals(bookLink)) {
                    mBookCaseBean.setUseSource(baseLink.getTag());
                    break;
                }
            }
        }

        mBookCaseBean.setIsZhuiShu(false);
        mBookCaseBean.setZhuiShuId(bean.getId());
    }

    /**
     * 追书
     */
    public void setZhuiShuBeanFromSearchBookInfoBean(SearchBookInfoBean bean
            , int readProgress, String readChapterTitle, int readPageIndex, String tag) {
        mBookCaseBean.setBookName(bean.getBookName());
        mBookCaseBean.setAuther(bean.getAuthor());
        mBookCaseBean.setBaseLink(bean.getBaseLink());
        mBookCaseBean.setImg(bean.getImg());
        mBookCaseBean.setIsTheCache(false);
        mBookCaseBean.setReadProgress(readProgress);
        mBookCaseBean.setReadChapterTitle(readChapterTitle);
        mBookCaseBean.setReadPageIndex(readPageIndex);
        mBookCaseBean.setUseSource(tag);

        mBookCaseBean.setIsZhuiShu(bean.isZhuiShu());
        mBookCaseBean.setZhuiShuId(bean.getId());
    }

}
