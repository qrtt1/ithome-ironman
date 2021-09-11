package org.qty.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
                    t.setUrl(elem.select("a.contestants-list__title").attr("href"));
                    t.setCategory(elem.select("div.contestants-group").text());
                    return t;
                }
        ).collect(Collectors.toList());
    }

    public int getMaxPage(Document document) {
        String pageListSelector = ".pagination li";
        int maxPage = 1;
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

    public int getMaxPageInTopic(Document document) {
        return getMaxPage(document);
    }

    public void update(Topic topic) {
        try {
            executeUpdate(topic);
        } catch (Exception e) {
            topic.setView(0);
        }
    }

    private void executeUpdate(Topic topic) {
        Document document = Jsoup.parse(fetch.get(topic.getUrl()));
        topic.setAuthor(document.select("div.profile-header__name").first().text());
        topic.setProfileUrl(document.select("a.profile-nav__link").first().attr("href"));
        topic.setView(getViewInTopicPage(document));

        int maxPageInTopic = getMaxPageInTopic(document);
        if (maxPageInTopic > 1) {
            for (int i = 2; i <= maxPageInTopic; i++) {
                Document doc = Jsoup.parse(fetch.get(topic.getUrl() + "?page=" + i));
                topic.setView(topic.getView() + getViewInTopicPage(doc));
            }
        }
    }

    private int getViewInTopicPage(Document document) {
        Elements elements = document.select(".qa-condition__count");

        int sum = 0;
        for (int i = 2; i < elements.size(); i += 3) {
            sum += Integer.parseInt(elements.get(i).text());
        }

        return sum;
    }
}
