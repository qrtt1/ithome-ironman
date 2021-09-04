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

class CrawlerMultipageTopcsTest {

    @Test
    public void testCrawler_parseMaxPage() {
        Crawler crawler = new Crawler(null);
        assertEquals(80, crawler.getMaxPage(Jsoup.parse(createFakeFetch().get(Crawler.ALL_TOPICS_URL))));
    }

    @Test
    public void testCrawler_2page_topics() {
        Crawler crawler = new Crawler(multiPageFetch()) {
            @Override
            public int getMaxPage(Document document) {
                return 2;
            }
        };
        List<Topic> topics = crawler.topics();

        List<String> expected = Arrays.asList(
                "從零開始使用python打造簡易投資工具",
                "來學習寫安卓手機的程式吧-Kotlin語言",
                "學資料科學的小孩不會變壞- 從入門到實戰全攻略",
                "大學生必學的 30個Python技巧",
                "我好想轉生成史萊姆",
                "Google Assistant 開發與語音使用者介面設計",
                "機智接案生活 - WooCommerce 金流串接實戰",
                "[ 重構倒數30天，你的網站不Vue白不Vue ]",
                "JUCE 入門 @ 跨平台應用程式開發使用 C++",
                "C# 入门",
                "不專業的 Youtuber 之路：「跟著舒安吃到飽～」",
                "AI ninja project",
                "我不太懂 AI，可是我會一點 Python 和 Azure",
                "DevOps 好想學!新手也能打造雲端 Study Lab",
                "學姊，要不要來我家看電腦後空翻阿",
                "我要成為全端王!",
                "觀賞魚辨識的YOLO全餐",
                "奇怪的知識增加了!原來程式還可以這樣用?!",
                "30 天 Java 從陌生到更陌生",
                "一起來探索Python語言"
        );

        List<String> titles = topics.stream().map(Topic::getTitle).collect(Collectors.toList());
        assertEquals(expected, titles);

    }

    private Fetch multiPageFetch() {
        return new Fetch() {
            @Override
            public String get(String source) {
                if (source.endsWith("page=1")) {
                    return readFromResource("/p1.html");
                }
                if (source.endsWith("page=2")) {
                    return readFromResource("/p2.html");
                }
                return readFromResource("/topics.html");
            }
        };
    }

    private Fetch createFakeFetch() {
        return new Fetch() {
            @Override
            public String get(String source) {
                return readFromResource("/topics.html");
            }
        };
    }

    private String readFromResource(String resource) {
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            IOUtils.copy(CrawlerMultipageTopcsTest.class.getResourceAsStream(resource), output);
            return new String(output.toByteArray(), "utf-8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}