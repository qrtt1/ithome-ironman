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
        assertEquals(28, crawler.getMaxPage(Jsoup.parse(createFakeFetch().get(Crawler.CONTENT_LIST))));
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
                "在open source環境，建置container環境及k8s(Minikube)的API佈署環境",
                "從 Effective Java 到 Effective Kotlin 的異與同",
                "一天姐一題python",
                "Windows Exploitation 101",
                "Flutter 進階課",
                "Hello SQL 初次見面你好",
                "Make web3 data accessible",
                "基於Kubernetes的微服務部署和管理策略",
                "用python學習資料結構與演算法 學習筆記",
                "從 Airflow 走到 dbt 的 30 天");


        List<String> titles = topics.stream().map(Topic::getTitle).collect(Collectors.toList());
        assertEquals(expectedTitles, titles);

        List<String> expectedUrls = Arrays.asList(
                "https://ithelp.ithome.com.tw/users/20161653/ironman/6212",
                "https://ithelp.ithome.com.tw/users/20135701/ironman/6211",
                "https://ithelp.ithome.com.tw/users/20162203/ironman/6210",
                "https://ithelp.ithome.com.tw/users/20120098/ironman/6209",
                "https://ithelp.ithome.com.tw/users/20117363/ironman/6208",
                "https://ithelp.ithome.com.tw/users/20152148/ironman/6207",
                "https://ithelp.ithome.com.tw/users/20162188/ironman/6206",
                "https://ithelp.ithome.com.tw/users/20145329/ironman/6205",
                "https://ithelp.ithome.com.tw/users/20162172/ironman/6204",
                "https://ithelp.ithome.com.tw/users/20162184/ironman/6203"
        );
        List<String> urls = topics.stream().map(Topic::getUrl).collect(Collectors.toList());
        assertEquals(expectedUrls, urls);

        List<String> expectedCategories = Arrays.asList(
                "DevOps",
                "Kotlin",
                "影片教學",
                "Security",
                "Mobile Development",
                "自我挑戰組",
                "Web 3",
                "Cloud Native",
                "自我挑戰組",
                "AI & Data"
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