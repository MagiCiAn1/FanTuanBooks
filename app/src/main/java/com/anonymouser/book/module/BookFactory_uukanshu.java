package com.anonymouser.book.module;

import com.anonymouser.book.utlis.JsoupUtil;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 停用
 * Created by YandZD on 2017/7/27.
 */

public class BookFactory_uukanshu implements IBookLoadFactory {
    @Override
    public ArrayList<Map<String, String>> getZhangjie(String link) {
        Document stream = JsoupUtil.getUrlStream(link);
        ArrayList<Map<String, String>> zhangjie = new ArrayList<>();
        HashMap<String, String> map;

        Elements elements = stream.select("ul#chapterList a");
        for (Element element : elements) {
            map = new HashMap<>();
            map.put("title", element.text());
            map.put("link", "http://www.uukanshu.net" + element.attr("href"));
            zhangjie.add(map);
        }
        Collections.reverse(zhangjie);
        return zhangjie;
    }

    @Override
    public String getBook(String link) {
        Document stream = JsoupUtil.getUrlStream(link);
        String text = stream.select("div#contentbox").text().trim();
        text = text.replaceAll(" 　　", "\n");
        text = text.replaceAll("     ", "\n");
        text = text.replaceAll("　　", "");
        return text;
    }

    @Override
    public int getVersion() {
        return 1;
    }
}
