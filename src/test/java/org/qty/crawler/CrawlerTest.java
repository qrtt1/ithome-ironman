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
        assertEquals(13, crawler.getMaxPage(Jsoup.parse(createFakeFetch().get(Crawler.CONTENT_LIST))));
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
                "Leetcode 解題之旅：逐日攻克",
                "關於寫react 那二三事",
                "運用生成式 AI 服務 所提供的API 實做應用開發（以Gemini及ChatGPT為例）",
                "30天整頓職場",
                "Flutter 開發實戰 - 30 天逃離新手村",
                "Python入門基礎語法與應用",
                "繁體中文的第一本CC書─Certified in Cybersecurity",
                "使用 Spring AI 打造企業 RAG 知識庫",
                "從 SwiftUI 到 Apple Vision Pro - SwiftUI 從零開始",
                "用React Native打造找餐店APP");


        List<String> titles = topics.stream().map(Topic::getTitle).collect(Collectors.toList());
        assertEquals(expectedTitles, titles);

        List<String> expectedUrls = Arrays.asList(
                "https://ithelp.ithome.com.tw/users/20162696/ironman/7080",
                "https://ithelp.ithome.com.tw/users/20168266/ironman/7079",
                "https://ithelp.ithome.com.tw/users/20046160/ironman/7100",
                "https://ithelp.ithome.com.tw/users/20168339/ironman/7097",
                "https://ithelp.ithome.com.tw/users/20059915/ironman/7066",
                "https://ithelp.ithome.com.tw/users/20168211/ironman/7068",
                "https://ithelp.ithome.com.tw/users/20021644/ironman/7069",
                "https://ithelp.ithome.com.tw/users/20161290/ironman/7070",
                "https://ithelp.ithome.com.tw/users/20162607/ironman/7073",
                "https://ithelp.ithome.com.tw/users/20132295/ironman/7083"
        );
        List<String> urls = topics.stream().map(Topic::getUrl).collect(Collectors.toList());
        assertEquals(expectedUrls, urls);

        List<String> expectedCategories = Arrays.asList(
                "自我挑戰組",
                "Modern Web",
                "生成式 AI",
                "佛心分享-IT 人的工作軟技能",
                "Mobile Development",
                "Python",
                "Security",
                "生成式 AI",
                "Mobile Development",
                "佛心分享-SideProject30"
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