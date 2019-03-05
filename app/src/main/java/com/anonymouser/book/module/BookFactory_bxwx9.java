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

public class BookFactory_bxwx9 implements IBookLoadFactory {

    @Override
    public ArrayList<Map<String, String>> getZhangjie(String link) {
        Document stream = JsoupUtil.getUrlStream(link);
        ArrayList<Map<String, String>> zhangjie = new ArrayList<>();
        Map<String, String> map;

        //相当于一行，四个
        ArrayList<Map<String, String>> links = new ArrayList<>();
        links.add(null);
        links.add(null);
        links.add(null);
        links.add(null);

        Elements elements = stream.select("div#TabCss dd");
        for (Element element : elements) {
            map = new HashMap<>();
            if (element.select("a").text() != null && !element.select("a").text().equals("")) {
                map.put("title", element.select("a").text());
                map.put("link", link.replace("index.html", "") + element.select("a").attr("href"));
            }


            if (element.hasClass("col4")) {
                //有col4类的 插在右边
                for (int i = 3; i >= 0; i--) {
                    if (links.get(i) == null) {
                        links.remove(i);
                        links.add(i, map);
                        break;
                    }
                }
            } else {
                for (int i = 0; i < 4; i++) {
                    if (links.get(i) == null) {
                        links.remove(i);
                        links.add(i, map);
                        break;
                    }
                }
            }


            if (links.get(0) != null
                    && links.get(1) != null
                    && links.get(2) != null
                    && links.get(3) != null) {
                for (Map<String, String> item : links) {
                    if (item.get("title") != null) {
                        zhangjie.add(item);
                    }
                }

                links.clear();
                links.add(null);
                links.add(null);
                links.add(null);
                links.add(null);
            }

        }

        return zhangjie;
    }

    @Override
    public String getBook(String link) {
        Document stream = JsoupUtil.getUrlStream(link);
        Elements elements = stream.select("div#content");
        elements.select("div").remove();

        String text = elements.html();

        text = text.replaceAll("<br>", "");

        text = text.replaceAll("\n \n", "\n");

        text = text.replaceAll("\n\n", "\n");

        text = text.replaceAll("&nbsp;", "");

        text = text.replaceAll("\n<!--go-->", "")
                .replaceAll("<!--go-->", "")
                .replaceAll("\n天才壹秒記住『笔下文学』，為您提供精彩小說閱讀。", "")
                .replaceAll("\n天才壹秒記住『笔下文学qu】\n", "")
                .replaceAll("天才壹秒記住『笔下文学qu】", "");

        return text;
    }

    public int getVersion() {
        return 1;
    }
}
