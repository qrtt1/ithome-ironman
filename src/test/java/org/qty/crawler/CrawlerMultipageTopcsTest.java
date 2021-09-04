package org.qty.crawler;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
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
    
    private Fetch createFakeFetch() {
        return new Fetch() {
            @Override
            public String get(String source) {
                try {
                    ByteArrayOutputStream output = new ByteArrayOutputStream();
                    IOUtils.copy(CrawlerMultipageTopcsTest.class.getResourceAsStream("/topics.html"), output);
                    return new String(output.toByteArray(), "utf-8");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

}