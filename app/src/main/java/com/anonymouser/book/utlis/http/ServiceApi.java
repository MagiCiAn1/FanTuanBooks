package com.anonymouser.book.utlis.http;

import com.anonymouser.book.bean.CategoryBean;
import com.anonymouser.book.bean.CategoryBookItemBean;
import com.anonymouser.book.bean.CategoryItemBean;
import com.anonymouser.book.bean.LastChapterBean;
import com.anonymouser.book.bean.SearchBookInfoBean;
import com.anonymouser.book.bean.ZhuiShuContentBean;
import com.anonymouser.book.bean.ZhuiShuSearcheBean;
import com.anonymouser.book.bean.ZhuiShuSourceBean;
import com.anonymouser.book.bean.ZhuiShuChaptersBean;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.convert.StringConvert;
import com.lzy.okgo.model.Response;
import com.lzy.okrx2.adapter.ObservableResponse;

import java.util.HashSet;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;

/**
 *
 * Created by YandZD on 2017/7/7.
 */

public class ServiceApi {

    //    public final static String HEADAPI = "http://192.168.50.152:8080/";           //用户
//    public final static String HEADAPI = "http://192.168.6.57:8080/";           //用户
//    public final static String HEADAPI = "http://yourbuffslonnol.com/BookService/";           //用户
    public final static String HEADAPI = "http://47.101.182.185:8080/BookService/";
    public static Observable<Response<SearchBookInfoBean[]>> searchBookInfo(String word) {
        return OkGo.<SearchBookInfoBean[]>get(HEADAPI + "bookinfo/")
                .params("word", word)
                .converter(new BeanConvert<>(SearchBookInfoBean[].class))
                .adapt(new ObservableResponse<SearchBookInfoBean[]>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }


    public static Observable<Response<String>> feedback(@NonNull String msg) {
        return OkGo.<String>post(HEADAPI + "feedback")
                .params("msg", msg)
                .converter(new StringConvert())
                .adapt(new ObservableResponse<String>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    //追书模糊搜索
    public static Observable<Response<ZhuiShuSearcheBean>> zhuishuSearch(@NonNull String word) {
        return OkGo.<ZhuiShuSearcheBean>get("http://api.zhuishushenqi.com/book/fuzzy-search")
                .params("query", word)
                .params("limit", "5")
                .converter(new BeanConvert<>(ZhuiShuSearcheBean.class))
                .adapt(new ObservableResponse<ZhuiShuSearcheBean>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    //追书源
    public static Observable<Response<ZhuiShuSourceBean[]>> zhuishuSource(@NonNull String bookId) {
        return OkGo.<ZhuiShuSourceBean[]>get("http://api.zhuishushenqi.com/atoc")
                .params("view", "summary")
                .params("book", bookId)
                .converter(new BeanConvert<>(ZhuiShuSourceBean[].class))
                .adapt(new ObservableResponse<ZhuiShuSourceBean[]>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 追书章节
     * http://api.zhuishushenqi.com/mix-atoc/50bff3ec209793513100001c?view=chapters
     * <p>
     * sourceId
     * 源的id
     */
    public static Observable<Response<ZhuiShuChaptersBean>> zhuishuChapters(@NonNull String sourceId) {
        return OkGo.<ZhuiShuChaptersBean>get(String.format("http://api.zhuishushenqi.com/atoc/%s?view=chapters", sourceId))
                .converter(new BeanConvert<>(ZhuiShuChaptersBean.class))
                .adapt(new ObservableResponse<ZhuiShuChaptersBean>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 追书 内容
     * http://chapterup.zhuishushenqi.com/chapter/http://vip.zhuishushenqi.com/chapter/5817f1161bb2ca566b0a5973?cv=1481275033588
     */
    public static Observable<Response<ZhuiShuContentBean>> zhuishuContent(String link) {
        return OkGo.<ZhuiShuContentBean>get(String.format("http://chapterup.zhuishushenqi.com/chapter/%s?cv=%d", link, System.currentTimeMillis()))
                .converter(new BeanConvert<>(ZhuiShuContentBean.class))
                .adapt(new ObservableResponse<ZhuiShuContentBean>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 追书 最新章节
     * http://api05iye5.zhuishushenqi.com/book?view=updated&id=531169b3173bfacb4904ca67,531169b3173bfacb4904ca67
     */
    public static Observable<Response<LastChapterBean[]>> zhuishuLastChapt(String bookId) {
        return OkGo.<LastChapterBean[]>get("http://api05iye5.zhuishushenqi.com/book?view=updated&id=" + bookId)
                .converter(new BeanConvert<>(LastChapterBean[].class))
                .adapt(new ObservableResponse<LastChapterBean[]>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    public static Observable<Response<LastChapterBean[]>> bookInfoLastChapt(String bookId){
        return OkGo.<LastChapterBean[]>get(HEADAPI + "lastchapter/?bookid=" + bookId)
                .converter(new BeanConvert<>(LastChapterBean[].class))
                .adapt(new ObservableResponse<LastChapterBean[]>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 分类右边条目
     *
     * @return
     */
    public static Observable<Response<CategoryBean>> categoryItem() {
        return OkGo.<CategoryBean>get("http://api.zhuishushenqi.com/cats/lv2")
                .converter(new BeanConvert<>(CategoryBean.class))
                .adapt(new ObservableResponse<CategoryBean>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    //分类右边
    public static Observable<Response<CategoryBookItemBean>> categoryBookItem(CategoryItemBean itemBean) {
        return OkGo.<CategoryBookItemBean>get("http://api.zhuishushenqi.com/book/by-categories")
                .params("gender", itemBean.gender)
                .params("type", "hot")
                .params("major", itemBean.major)
                .params("minor", itemBean.minor)
                .params("start", itemBean.start + "")
                .params("limit", 20 + "")
                .converter(new BeanConvert<>(CategoryBookItemBean.class))
                .adapt(new ObservableResponse<CategoryBookItemBean>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


}
