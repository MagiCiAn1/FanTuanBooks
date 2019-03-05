package com.anonymouser.book.module;

import com.anonymouser.book.utlis.JsoupUtil;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by YandZD on 2017/7/23.
 */

public class BookFactory_zbiquge implements IBookLoadFactory {

    @Override
    public ArrayList<Map<String, String>> getZhangjie(String link) {
        Document stream = JsoupUtil.getUrlStream(link);
        ArrayList<Map<String, String>> zhangjie = new ArrayList<>();
        HashMap<String, String> map;
        boolean nextFlag = true;
        Element element = stream.select("div#list").select("dt").get(1);
        while (nextFlag) {
            element = element.nextElementSibling();
            if (element != null && element.select("dd") != null && element.select("dd").size() > 0) {
                map = new HashMap<>();
                map.put("title", element.text());
                map.put("link", "http://www.zbiquge.com" + element.select("a").attr("href"));
                zhangjie.add(map);
            } else {
                nextFlag = false;
            }
        }

        return zhangjie;
    }

    @Override
    public String getBook(String link) {
        Document stream = JsoupUtil.getUrlStream(link);
        Elements elements = stream.select("div#content");
        elements.select("style").remove();
        elements.select("div").remove();

        String text = elements.html();

        text = text.replaceAll("<br> ", "");

        text = text.replaceAll("<br>", "");

        text = text.replaceAll("\n \n", "\n");

        text = text.replaceAll("\n\n", "\n");

        text = text.replaceAll("&nbsp;", "");

        return text;
    }

    public int getVersion() {
        return 1;
    }
}
