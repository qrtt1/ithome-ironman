package org.qty.crawler;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class DataUpdater {


    public static void main(String[] args) throws IOException {
        Crawler crawler = new Crawler(new DefaultFetch());
        Storage storage = createStorage();

        List<Topic> topics = crawler.topics();
        List<Topic> savedTopics = storage.loadSavedTopics();

        if (savedTopics.isEmpty()) {
            savedTopics = topics;
        } else {
            appendNewTopics(savedTopics, topics);
        }

        savedTopics = new ArrayList<>(savedTopics.stream()
                .collect(Collectors.toMap(t -> t.url, t -> t, (existing, replacement) -> existing))
                .values());

        Collections.sort(savedTopics, (a, b) -> {
            if (a.lastUpdated == b.lastUpdated) {
                return 0;
            }
            return a.lastUpdated > b.lastUpdated ? 1 : -1;
        });

        System.out.println("size: " + savedTopics.size());
        savedTopics.stream().limit(300).forEach(topic -> {
            crawler.update(topic);
            System.out.println(topic);
        });

        storage.saveTopics(savedTopics);
    }

    private static Storage createStorage() {
        if ("production".equals(System.getenv("ithome_crawler_env"))) {
            System.out.println("Run with S3Storage");
            return new S3Storage();
        }
        System.out.println("Run with DefaultStorage");
        return new DefaultStorage();
    }

    private static void appendNewTopics(List<Topic> savedTopics, List<Topic> topics) {
        Set<String> existingUrls = savedTopics.stream().map(t -> t.url).collect(Collectors.toSet());
        topics.forEach(t -> {
            if (!existingUrls.contains(t.url)) {
                savedTopics.add(t);
                System.out.println("Add: " + t);
            }
        });

    }
}
