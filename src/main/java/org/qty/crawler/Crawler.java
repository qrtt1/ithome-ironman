package org.qty.crawler;

import org.apache.commons.codec.digest.DigestUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Crawler {

    public final static String CONTENT_LIST = "https://ithelp.ithome.com.tw/2022ironman/signup/list";

    private Fetch fetch;

    public Crawler(Fetch fetch) {
        this.fetch = fetch;

        // enable it for get sample page
        PageSampler.CREATE_SAMPLE = false;
    }

    public List<Topic> topics() {
        String content = fetch.get(CONTENT_LIST);
        Document document = Jsoup.parse(content);
        PageSampler.save(document, "topics.html");

        ArrayList<Topic> topics = new ArrayList<>();
        for (int page = 1; page <= getMaxPage(document); page++) {
            String singlePageUrl = CONTENT_LIST + "?page=" + page;
            Document topicsDoc = Jsoup.parse(fetch.get(singlePageUrl));
            topics.addAll(parseTopics(topicsDoc));
        }

        return topics;
    }

    private List<Topic> parseTopics(Document document) {
        return document.select("body > section.sec-contestants > div > div  > div > div.col-md-10").stream().map((elem) -> {
            Topic t = new Topic();
            t.setTitle(elem.select("a.contestants-list__title").text());
            t.setUrl(elem.select("a.contestants-list__title").attr("href"));
            t.setCategory(elem.select(".tag span").text());
            return t;
        }).collect(Collectors.toList());
    }

    public int getMaxPage(Document document) {
        String pageListSelector = ".pagination-inner a";
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
            System.err.println("ERR: " + topic);
            e.printStackTrace();
        } finally {
            topic.setLastUpdated(System.currentTimeMillis());
        }
    }

    private void executeUpdate(Topic topic) {
        Document document = Jsoup.parse(fetch.get(topic.getUrl()));
        PageSampler.save(document, "topic_page.html");

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
        topic.setAnchor(DigestUtils.md5Hex(topic.getUrl()));
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
