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

        // pre-calculated view count was 421
        assertEquals(0, topic.getView());

        // body > div.container.index-top > div > div > div:nth-child(1) > div.profile-header.clearfix > div.profile-header__content > div.profile-header__name
        assertEquals("ludwig (ludwig)", topic.getAuthor());
        assertEquals("https://ithelp.ithome.com.tw/users/20151244/profile", topic.getProfileUrl());

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