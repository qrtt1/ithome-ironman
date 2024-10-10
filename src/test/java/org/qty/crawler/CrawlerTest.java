package org.qty.crawler;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CrawlerTest {


    @Test
    public void testCrawler_parseMaxPage() {
        Crawler crawler = new Crawler(null);
        assertEquals(106, crawler.getMaxPage(Jsoup.parse(createFakeFetch().get(Crawler.CONTENT_LIST))));
    }

    @Test
    public void testCrawler_fetch_topics() {
        Crawler crawler = new Crawler(createFakeFetch()) {
            @Override
            public int getMaxPage(Document document) {
                return 1;
            }
        };
        List<Topic> topics = crawler.topics();

        List<String> expectedTitles = Arrays.asList(
                "30 Days of AI Research",
                "從 SwiftUI 到 Apple Vision Pro - SwiftUI 從零開始",
                "asp.net可以變出那些功能",
                "時空序列分析-關鍵籌碼分析",
                "Flutter 開發實戰 - 30 天逃離新手村",
                "Web仔常見的面試問題",
                "Python入門基礎語法與應用",
                "繁體中文的第一本CC書─Certified in Cybersecurity",
                "使用 Spring AI 打造企業 RAG 知識庫",
                "關於新手會想知道Kubernetes的幾件事情");


        List<String> titles = topics.stream().map(Topic::getTitle).collect(Collectors.toList());
        assertEquals(expectedTitles, titles);

        List<String> expectedUrls = Arrays.asList(
                "https://ithelp.ithome.com.tw/users/20152821/ironman/7074",
                "https://ithelp.ithome.com.tw/users/20162607/ironman/7073",
                "https://ithelp.ithome.com.tw/users/20119035/ironman/7064",
                "https://ithelp.ithome.com.tw/users/20168322/ironman/7065",
                "https://ithelp.ithome.com.tw/users/20059915/ironman/7066",
                "https://ithelp.ithome.com.tw/users/20161704/ironman/7067",
                "https://ithelp.ithome.com.tw/users/20168211/ironman/7068",
                "https://ithelp.ithome.com.tw/users/20021644/ironman/7069",
                "https://ithelp.ithome.com.tw/users/20161290/ironman/7070",
                "https://ithelp.ithome.com.tw/users/20152821/ironman/7072"
        );
        List<String> urls = topics.stream().map(Topic::getUrl).collect(Collectors.toList());
        assertEquals(expectedUrls, urls);

        List<String> expectedCategories = Arrays.asList(
                "AI/ ML & Data",
                "Mobile Development",
                "自我挑戰組",
                "Python",
                "Mobile Development",
                "JavaScript",
                "Python",
                "Security",
                "生成式 AI",
                "Kubernetes"
        );
        List<String> categories = topics.stream().map(Topic::getCategory).collect(Collectors.toList());
        assertEquals(expectedCategories, categories);

    }

    private Fetch createFakeFetch() {
        return new Fetch() {
            @Override
            public String get(String source) {
                try {
                    ByteArrayOutputStream output = new ByteArrayOutputStream();
                    IOUtils.copy(CrawlerTest.class.getResourceAsStream("/topics.html"), output);
                    return new String(output.toByteArray(), "utf-8");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

}