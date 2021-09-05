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

class CrawlerTopicUpdaterTest {

    @Test
    public void testCrawler_viewCount() {
        Crawler crawler = new Crawler(createFakeFetch());

        Topic topic = new Topic();
        topic.setUrl("/topic_page.html");
        crawler.update(topic);
        // pre-calculated view count was 472
        assertEquals(472, topic.getView());
    }

    private Fetch createFakeFetch() {
        return new Fetch() {
            @Override
            public String get(String source) {
                try {
                    ByteArrayOutputStream output = new ByteArrayOutputStream();
                    IOUtils.copy(CrawlerTopicUpdaterTest.class.getResourceAsStream(source), output);
                    return new String(output.toByteArray(), "utf-8");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

}