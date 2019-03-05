package com.anonymouser.book.module;

import android.os.Environment;

import com.anonymouser.book.BookApp;
import com.google.gson.Gson;

import java.io.File;

import dalvik.system.BaseDexClassLoader;

/**
 * Created by YandZD on 2017/7/23.
 */

public class BookLoadFactory {
    private boolean isDebug = false;

    private IBookLoadFactory mFactory;
    private String mLink;

    public BookLoadFactory(String tag, String link) throws Exception {
//        try {
        File optimizedDexOutputPath = new File(BookApp.mContext.getFilesDir(), "jar/" + tag);
        mLink = link;

        BaseDexClassLoader cl = new BaseDexClassLoader(Environment.getExternalStorageDirectory().toString(),
                optimizedDexOutputPath, optimizedDexOutputPath.getAbsolutePath(), BookApp.mContext.getClassLoader());
        Class libProviderClazz = null;


        // 载入JarLoader类， 并且通过反射构建JarLoader对象
        libProviderClazz = cl.loadClass("com.anonymouser.book.module.BookFactory_" + tag);
        mFactory = (IBookLoadFactory) libProviderClazz.newInstance();
//        } catch (Exception exception) {
//            exception.printStackTrace();
//        }
    }

    public String getBook() {
        if (isDebug)
            return new BookFactory_quanben().getBook(mLink);
//        try {
            return mFactory.getBook(mLink);
//        } catch (Exception e) {
//
//        }

//        return "";
    }


    public String getZhangjie() {
        if (isDebug)
            return new Gson().toJson(new BookFactory_quanben().getZhangjie(mLink));
//        try {
            return new Gson().toJson(mFactory.getZhangjie(mLink));
//        } catch (Exception e) {
//
//        }
//
//        return "";
    }

    public int getVersion() {
        try {
            return mFactory.getVersion();
        } catch (Exception e) {

        }
        return 0;
    }


    public IBookLoadFactory getFactory() {
        return mFactory;
    }

}
