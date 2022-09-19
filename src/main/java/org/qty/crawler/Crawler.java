package org.qty.crawler;

import org.apache.commons.codec.digest.DigestUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
        return getMaxPageBySelector(document, pageListSelector);
    }

    private static int getMaxPageBySelector(Document document, String pageListSelector) {
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
        return getMaxPageBySelector(document, ".pagination a");
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
        PageSampler.save(document, "topic_page_1.html");

        topic.setAuthor(document.select("div.profile-header__name").first().text());
        topic.setProfileUrl(document.select("a.profile-nav__link").first().attr("href"));
        List<Article> articles = extractArticles(document);
        topic.articles.addAll(articles);

        int maxPageInTopic = getMaxPageInTopic(document);
        if (maxPageInTopic > 1) {
            for (int i = 2; i <= maxPageInTopic; i++) {
                PageSampler.save(document, String.format("topic_page_%d.html", i));
                Document doc = Jsoup.parse(fetch.get(topic.getUrl() + "?page=" + i));
                topic.articles.addAll(extractArticles(doc));
            }
        }
        topic.updateStatus();
        topic.setAnchor(DigestUtils.md5Hex(topic.getUrl()));
        topic.setView(topic.getArticles().stream().mapToInt(Article::getViewCount).sum());
    }

    private List<Article> extractArticles(Document document) {
        Elements titleAndLinks = document.select("a.qa-list__title-link");
        Elements publishTimes = document.select("a.qa-list__info-time");
        Elements viewAllCounts = document.select("span.qa-condition__count");
        List<Integer> viewInts = IntStream.range(1, viewAllCounts.size() + 1)
                .filter(x -> x % 3 == 0).boxed()
                .map(x -> Integer.parseInt(viewAllCounts.get(x - 1).text())).collect(Collectors.toList());

        return IntStream.range(0, titleAndLinks.size()).boxed().map((idx -> {
            Article article = new Article();
            article.setTitle(titleAndLinks.get(idx).text());
            article.setUrl(titleAndLinks.get(idx).attr("href").strip());
            article.setViewCount(viewInts.get(idx));

            // 2022-09-02 09:16:19
            article.setPublished(
                    LocalDateTime.parse(publishTimes.get(idx).attr("title"),
                            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            article.setIso8601Published(article.getPublished().format(DateTimeFormatter.ISO_DATE_TIME));
            System.out.println(article);
            return article;
        })).collect(Collectors.toList());
    }


}
