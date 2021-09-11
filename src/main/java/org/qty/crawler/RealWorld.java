package org.qty.crawler;

import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class RealWorld {

    public static void main(String[] args) throws IOException {
        Crawler crawler = new Crawler(new DefaultFetch()) {
            @Override
            public int getMaxPage(Document document) {
                return 3;
            }
        };

        List<Topic> topics = crawler.topics();
        System.out.println(topics.size());
        for (Topic topic : topics) {
            crawler.update(topic);
            System.out.println(topic);
        }

        FileUtils.write(new File("output.json"), new Gson().toJson(topics), "utf-8");
    }
}
