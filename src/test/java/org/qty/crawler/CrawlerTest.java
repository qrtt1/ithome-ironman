package org.qty.crawler;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class CrawlerTest {

    @Test
    public void testCrawler_fetch_topics() {
        Crawler crawler = new Crawler(createFakeFetch());
        List<Topic> topics = crawler.topics();

        List<String> expected = Arrays.asList("來學習寫安卓手機的程式吧-Kotlin語言",
                "大學生必學的 30個Python技巧",
                "Google Assistant 開發與語音使用者介面設計",
                "機智接案生活 - WooCommerce 金流串接實戰",
                "[ 重構倒數30天，你的網站不Vue白不Vue ] ",
                "C# 入门",
                "AI ninja project",
                "我不太懂 AI，可是我會一點 Python 和 Azure",
                "觀賞魚辨識的YOLO全餐",
                "30 天 Java 從陌生到更陌生");

        List<String> titles = topics.stream().map(Topic::getTitle).collect(Collectors.toList());
        assertEquals(expected, titles);
    }

    private Fetch createFakeFetch() {
        return new Fetch() {
            @Override
            public String get(String source) {
                return null;
            }
        };
    }

}