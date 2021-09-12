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
                return super.getMaxPage(document);
            }
        };

        List<Topic> topics = crawler.topics();

        for (Topic topic : topics) {
            crawler.update(topic);
        }

        FileUtils.write(new File("data.json"), new Gson().toJson(topics), "utf-8");
    }
}
