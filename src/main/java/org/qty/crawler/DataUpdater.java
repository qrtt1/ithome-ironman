package org.qty.crawler;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.FileUtils;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataUpdater {

    public static void main(String[] args) throws IOException {
        Crawler crawler = new Crawler(new DefaultFetch()) {
            @Override
            public int getMaxPage(Document document) {
                return super.getMaxPage(document);
            }
        };

        Map<String, Long> lastUpdates = loadPreviousTopics();
        List<Topic> topics = crawler.topics();
        sortByLastUpdated(lastUpdates, topics);

        System.out.println("total size: " + topics.size());
        topics.stream().limit(50).forEach(topic -> {
            crawler.update(topic);
            System.out.println(topic);
        });

        FileUtils.write(new File("data.json"), new Gson().toJson(topics), "utf-8");
    }

    private static void sortByLastUpdated(Map<String, Long> lastUpdates, List<Topic> topics) {
        topics.forEach(t -> {
            if (lastUpdates.containsKey(t.getUrl())) {
                t.setLastUpdated(lastUpdates.get(t.getUrl()));
            }
        });

        Collections.sort(topics, (a, b) -> {
            if (a.lastUpdated == b.lastUpdated) {
                return 0;
            }
            return a.lastUpdated > b.lastUpdated ? 1 : -1;
        });
    }

    private static Map<String, Long> loadPreviousTopics() throws IOException {
        String data = FileUtils.readFileToString(new File("data.json"), "utf-8");
        List<Topic> previousTopics = new Gson().fromJson(data, new TypeToken<List<Topic>>() {
        }.getType());

        Map<String, Long> lastUpdates = new HashMap<>();
        previousTopics.forEach(t -> {
            lastUpdates.put(t.url, t.lastUpdated);
        });
        return lastUpdates;
    }
}
