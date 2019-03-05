package com.anonymouser.book.utlis.http;

import com.anonymouser.book.bean.LastChapterBean;
import com.google.gson.Gson;
import com.lzy.okgo.convert.Converter;

import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by YandZD on 2017/8/3.
 */

public class BeanConvert<T> implements Converter<T> {

    public Class<T> mType;

    public BeanConvert(Class<T> type) {
        mType = type;
    }

    @Override
    public T convertResponse(Response response) throws Throwable {
        ResponseBody body = response.body();
        if (body == null) return null;
        
        return new Gson().fromJson(body.string(), mType);
    }
}