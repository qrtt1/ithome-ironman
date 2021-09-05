package org.qty.crawler;

import org.jsoup.nodes.Document;

import java.util.List;

public class RealWorld {

    public static void main(String[] args) {
        Crawler crawler = new Crawler(new DefaultFetch()) {
            @Override
            public int getMaxPage(Document document) {
                return 1;
            }
        };

        List<Topic> topics = crawler.topics();
        for (Topic topic : topics) {
            crawler.update(topic);
            System.out.println(topic);
        }
    }
}
