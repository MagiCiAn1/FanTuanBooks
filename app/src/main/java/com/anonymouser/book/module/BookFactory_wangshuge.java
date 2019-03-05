package com.anonymouser.book.module;

import com.anonymouser.book.utlis.JsoupUtil;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by YandZD on 2017/7/27.
 */

public class BookFactory_wangshuge implements IBookLoadFactory {
    @Override
    public ArrayList<Map<String, String>> getZhangjie(String link) {
        Document stream = JsoupUtil.getUrlStream(link);
        Elements elements = stream.getElementsByTag("tbody").select("a");
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
        Document stream = JsoupUtil.getUrlStream(link);
        String text = stream.select("dd#contents").html();

        text = text.replaceAll("<br>", "");

        text = text.replaceAll("&nbsp;&nbsp;&nbsp;&nbsp;", "");

        text = text.replaceAll("\n \n", "\n");

        text = text.replaceAll("\n\n", "\n");

        text = text.replaceAll("&amp;", "");

        return text;
    }

    @Override
    public int getVersion() {
        return 2;
    }
}
