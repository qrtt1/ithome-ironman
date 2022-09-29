package org.qty.crawler;

import java.io.IOException;
import java.util.List;

import static org.qty.crawler.Storage.loadPreviousTopics;

public class DebugViewCrawler {

    public static void main(String[] args) throws IOException {
        Crawler crawler = new Crawler(new DefaultFetch());
        List<Topic> savedTopics = loadPreviousTopics();
        savedTopics.stream().filter(t -> t.url.equals("https://ithelp.ithome.com.tw/users/20140998/ironman/5461")).forEach(topic -> {
            crawler.update(topic);
            System.out.println(topic);
            topic.getArticles().forEach(a->{
                System.out.println(a);
            });
            System.out.println(topic.getView());
        });
    }

}
