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

public class BookFactory_uctxt implements IBookLoadFactory {

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
        Elements elements = stream.getElementsByClass("chapter-list").select("a");
        ArrayList<Map<String, String>> zhangjie = new ArrayList<>();
        HashMap<String, String> map;
        for (Element item : elements) {
            map = new HashMap<>();
            map.put("title", item.text());
            map.put("link", link + item.attr("href"));

            zhangjie.add(map);
        }
        return zhangjie;
    }

    @Override
    public String getBook(String link) {
        Document stream = getUrlStream(link);
        String text = stream.getElementById("content").html();

        text = text.replaceAll("<br>", "");

        text = text.replaceAll("&nbsp;&nbsp;&nbsp;&nbsp;", "");

        text = text.replaceAll("\n\n", "\n");

        return text;
    }

    public int getVersion() {
        return 1;
    }
}
