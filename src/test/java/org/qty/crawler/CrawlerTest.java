package org.qty.crawler;

import org.apache.commons.io.IOUtils;
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
    public void testCrawler_fetch_topics() {
        Crawler crawler = new Crawler(createFakeFetch()) {
            @Override
            public int getMaxPage(Document document) {
                return 1;
            }
        };
        List<Topic> topics = crawler.topics();

        List<String> expectedTitles = Arrays.asList("來學習寫安卓手機的程式吧-Kotlin語言",
                "大學生必學的 30個Python技巧",
                "Google Assistant 開發與語音使用者介面設計",
                "機智接案生活 - WooCommerce 金流串接實戰",
                "[ 重構倒數30天，你的網站不Vue白不Vue ]",
                "C# 入门",
                "AI ninja project",
                "我不太懂 AI，可是我會一點 Python 和 Azure",
                "觀賞魚辨識的YOLO全餐",
                "30 天 Java 從陌生到更陌生");

        List<String> titles = topics.stream().map(Topic::getTitle).collect(Collectors.toList());
        assertEquals(expectedTitles, titles);

        List<String> expectedUrls = Arrays.asList("https://ithelp.ithome.com.tw/users/20140419/ironman/4096",
                "https://ithelp.ithome.com.tw/users/20140998/ironman/4362",
                "https://ithelp.ithome.com.tw/users/20141015/ironman/4365",
                "https://ithelp.ithome.com.tw/users/20133765/ironman/4367",
                "https://ithelp.ithome.com.tw/users/20125854/ironman/4112",
                "https://ithelp.ithome.com.tw/users/20099494/ironman/4373",
                "https://ithelp.ithome.com.tw/users/20122678/ironman/3864",
                "https://ithelp.ithome.com.tw/users/20139923/ironman/3866",
                "https://ithelp.ithome.com.tw/users/20129510/ironman/4385",
                "https://ithelp.ithome.com.tw/users/20140066/ironman/3878");
        List<String> urls = topics.stream().map(Topic::getUrl).collect(Collectors.toList());
        assertEquals(expectedUrls, urls);

        List<String> expectedCategories = Arrays.asList("Mobile Development",
                "影片教學",
                "AI & Data",
                "Modern Web",
                "Modern Web",
                "Software Development",
                "AI & Data",
                "AI & Data",
                "AI & Data",
                "Software Development");
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