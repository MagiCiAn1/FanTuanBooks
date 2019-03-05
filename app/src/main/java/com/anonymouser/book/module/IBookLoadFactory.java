package com.anonymouser.book.module;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by YandZD on 2017/7/23.
 */

public interface IBookLoadFactory {
    //得到章节
    ArrayList<Map<String, String>> getZhangjie(String link);

    //得到内容
    String getBook(String link);

    //得到jar包版本
    int getVersion();
}
