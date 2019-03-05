package com.anonymouser.book.module;

import com.anonymouser.book.utlis.JsoupUtil;

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

public class BookFactory_e8zw implements IBookLoadFactory {

    @Override
    public ArrayList<Map<String, String>> getZhangjie(String link) {
        Document stream = JsoupUtil.getUrlStream(link);
        ArrayList<Map<String, String>> zhangjie = new ArrayList<>();
        HashMap<String, String> map;

        Elements elements = stream.select("div#chapterlist.directoryArea").select("a");
        elements.remove(0);
        for (Element element : elements) {
            map = new HashMap<>();
            map.put("title", element.text());
            map.put("link", link.replace("all.html", "") + element.attr("href"));
            zhangjie.add(map);
        }
        return zhangjie;
    }

    @Override
    public String getBook(String link) {
        Document stream = JsoupUtil.getUrlStream(link);
        Elements elements = stream.select("div#chaptercontent");
        elements.select("p").remove();
        String text = elements.html();

        text = text.replaceAll("<br>　　", "");

        text = text.replaceAll("<br>", "");

        text = text.replace("公告：笔趣阁免费app上线了，支持安卓，苹果。请关注微信公众号进入下载安装 wanbenheji (按住三秒复制)!! ", "");

        text = text.replace("&nbsp;&nbsp;&nbsp;&nbsp;", "");

        return text;
    }

    public int getVersion() {
        return 1;
    }
}
