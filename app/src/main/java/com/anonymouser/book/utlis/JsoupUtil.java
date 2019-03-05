package com.anonymouser.book.utlis;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Created by YandZD on 2017/7/27.
 */

public class JsoupUtil {
    public static Document getUrlStream(String url) {
        try {
            Connection connect = Jsoup.connect(url);
//            connect.header("User-Agent", "  Mozilla/5.0 (Windows NT 6.1; WOW64; rv:5.0) Gecko/20100101 Firefox/5.0");
            connect.timeout(20 * 1000);

            Connection data = connect.data();

            Document document = data.get();

            return document;
        } catch (Exception e) {

        }
        return null;
    }
}
