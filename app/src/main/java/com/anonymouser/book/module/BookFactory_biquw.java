package com.anonymouser.book.module;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by YandZD on 2017/7/23.
 */

public class BookFactory_biquw implements IBookLoadFactory {

    private Document getUrlStream(String url) {
        try {
            Connection connect = Jsoup.connect(url);
            connect.header("User-Agent", "  Mozilla/5.0 (Windows NT 6.1; WOW64; rv:5.0) Gecko/20100101 Firefox/5.0");
            connect.timeout(20 * 1000);

            Connection data = connect.data();

            Document document = data.get();

            return document;
        } catch (Exception e) {

        }
        return null;
    }

    @Override
    public ArrayList<Map<String, String>> getZhangjie(String link) {
        Document stream = getUrlStream(link);

        Elements bookList = stream.getElementsByClass("book_list").select("li");
        ArrayList<Map<String, String>> zhangjie = new ArrayList<>();
        HashMap<String, String> map;
        for (Element x : bookList) {
            map = new HashMap<>();
            map.put("title", x.select("a").text());
            map.put("link", String.format("%s%s", stream.location(), x.select("a").attr("href")));
            zhangjie.add(map);
        }
        return zhangjie;
    }

    @Override
    public String getBook(String link) {
        Document stream = getUrlStream(link);
        String text = stream.getElementById("htmlContent").html();

        text = text.replace("<br> \n<br> &nbsp;&nbsp;&nbsp;&nbsp;", "");

        text = text.replace("&nbsp;&nbsp;&nbsp;&nbsp;", "");

        text = text.replace("&lt; 更新更快 就在笔趣网", "");

        text = text.replace("<a href=\"http://www.biquw.com\" target=\"_blank\">www.biquw.com</a> &gt;", "");

        return text;
    }

    public int getVersion() {
        return 1;
    }
}
