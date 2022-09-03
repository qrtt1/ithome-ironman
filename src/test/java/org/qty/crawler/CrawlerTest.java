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
        assertEquals(63, crawler.getMaxPage(Jsoup.parse(createFakeFetch().get(Crawler.CONTENT_LIST))));
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

        List<String> expectedTitles = Arrays.asList("[Dot Net Core](圖解系列與常用套件)",
                "Moleculer 家家酒",
                "從 Node.js 開發者到量化交易者：打造屬於自己的投資系統",
                "網頁開發（html.css）",
                "上班到一半突然被通知被炒了的我只好來寫個賓果",
                "工程師轉職新手爸爸 三十天讓你不當豬隊友",
                "寫寫歷年職場經歷過的大小事或近期所學習的知識啟發",
                "Quest for Hyperskewb",
                "了解AI多一點點",
                "亞洲雲端黑馬－阿里雲的七大必學應用");

        List<String> titles = topics.stream().map(Topic::getTitle).collect(Collectors.toList());
        assertEquals(expectedTitles, titles);

        List<String> expectedUrls = Arrays.asList("https://ithelp.ithome.com.tw/users/20144614/ironman/5120",
                "https://ithelp.ithome.com.tw/users/20107175/ironman/5388",
                "https://ithelp.ithome.com.tw/users/20150150/ironman/5145",
                "https://ithelp.ithome.com.tw/users/20120843/ironman/5158",
                "https://ithelp.ithome.com.tw/users/20140063/ironman/5417",
                "https://ithelp.ithome.com.tw/users/20129292/ironman/5178",
                "https://ithelp.ithome.com.tw/users/20151917/ironman/5437",
                "https://ithelp.ithome.com.tw/users/20103524/ironman/5183",
                "https://ithelp.ithome.com.tw/users/20150784/ironman/5200",
                "https://ithelp.ithome.com.tw/users/20150173/ironman/4947");
        List<String> urls = topics.stream().map(Topic::getUrl).collect(Collectors.toList());
        assertEquals(expectedUrls, urls);

        List<String> expectedCategories = Arrays.asList("自我挑戰組",
                "Software Development",
                "Software Development",
                "自我挑戰組",
                "Mobile Development",
                "自我挑戰組",
                "自我挑戰組",
                "自我挑戰組",
                "AI & Data",
                "自我挑戰組");
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