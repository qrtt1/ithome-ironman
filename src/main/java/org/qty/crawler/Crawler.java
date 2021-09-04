package org.qty.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Crawler {

    public final static String ALL_TOPICS_URL = "https://ithelp.ithome.com.tw/2021ironman/signup/list";

    private Fetch fetch;

    public Crawler(Fetch fetch) {
        this.fetch = fetch;
    }

    public List<Topic> topics() {
        String content = fetch.get(ALL_TOPICS_URL);
        Document document = Jsoup.parse(content);

        ArrayList<Topic> topics = new ArrayList<>();
        for (int page = 1; page <= getMaxPage(document); page++) {
            String singlePageUrl = ALL_TOPICS_URL + "?page=" + page;
            Document topicsDoc = Jsoup.parse(fetch.get(singlePageUrl));
            topics.addAll(parseTopics(topicsDoc));
        }

        return topics;
    }

    private List<Topic> parseTopics(Document document) {
        return document.select("div.border-frame.clearfix > div.contestants-wrapper > div.contestants-list").stream().map(
                (elem) -> {
                    Topic t = new Topic();
                    t.setTitle(elem.select("a.contestants-list__title").text());
                    return t;
                }
        ).collect(Collectors.toList());
    }

    public int getMaxPage(Document document) {
        String pageListSelector = "div.border-frame.clearfix > div.contestants-wrapper > div.text-center > ul a";
        int maxPage = 0;
        for (Element element : document.select(pageListSelector)) {
            try {
                int page = Integer.parseInt(element.text());
                if (maxPage < page) {
                    maxPage = page;
                }
            } catch (Exception e) {
            }

        }
        return maxPage;
    }
}
