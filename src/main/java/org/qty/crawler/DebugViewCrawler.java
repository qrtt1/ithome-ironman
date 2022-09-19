package org.qty.crawler;

import java.io.IOException;
import java.util.List;

import static org.qty.crawler.Storage.loadPreviousTopics;

public class DebugViewCrawler {

    public static void main(String[] args) throws IOException {
        Crawler crawler = new Crawler(new DefaultFetch());
        List<Topic> savedTopics = loadPreviousTopics();
        savedTopics.stream().filter(t -> t.url.equals("https://ithelp.ithome.com.tw/users/20141784/ironman/5375")).forEach(topic -> {
            crawler.update(topic);
            System.out.println(topic);
            System.out.println(topic.getView());
        });
    }

}
