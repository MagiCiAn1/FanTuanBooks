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

public class BookFactory_quanben implements IBookLoadFactory {

    @Override
    public ArrayList<Map<String, String>> getZhangjie(String link) {
        Document stream = JsoupUtil.getUrlStream(link);
        ArrayList<Map<String, String>> zhangjie = new ArrayList<>();
        HashMap<String, String> map;

        Elements elements = stream.select("div.book_list").select("li a");
        for (Element element : elements) {
            map = new HashMap<>();
            map.put("title", element.text());
            map.put("link", link + element.attr("href"));
            zhangjie.add(map);
        }
        return zhangjie;
    }

    @Override
    public String getBook(String link) {
        Document stream = JsoupUtil.getUrlStream(link);
        Elements elements = stream.select("div#htmlContent");

        String text = elements.html();

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
