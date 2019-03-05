package com.anonymouser.book.utlis;

/**
 * Created by YandZD on 2017/7/16.
 */

public class DataBaseUtil {

    public static long createId(String bookName, String author) {
        bookName.getBytes();

        return Long.parseLong((bookName.hashCode() + "").replace("-", "10")) + Long.parseLong((author.hashCode() + "").replace("-", "10"));
    }

}
