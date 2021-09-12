package org.qty.crawler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.FileUtils;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class DataUpdater {

    public static void main(String[] args) throws IOException {
        Crawler crawler = new Crawler(new DefaultFetch()) {
            @Override
            public int getMaxPage(Document document) {
                return super.getMaxPage(document);
            }
        };

        List<Topic> topics = crawler.topics();
        List<Topic> savedTopics = loadPreviousTopics();
        if (savedTopics.isEmpty()) {
            savedTopics = topics;
        } else {
            appendNewTopics(savedTopics, topics);
        }

        Collections.sort(savedTopics, (a, b) -> {
            if (a.lastUpdated == b.lastUpdated) {
                return 0;
            }
            return a.lastUpdated > b.lastUpdated ? 1 : -1;
        });

        System.out.println("size: " + savedTopics.size());
        savedTopics.stream().limit(50).forEach(topic -> {
            crawler.update(topic);
            System.out.println(topic);
        });

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Collections.sort(savedTopics, Comparator.comparing(Topic::getUrl));
        FileUtils.write(new File("data.json"), gson.toJson(savedTopics), "utf-8");
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

    private static List<Topic> loadPreviousTopics() throws IOException {
        if (!new File("data.json").exists()) {
            return new ArrayList<>();
        }
        String data = FileUtils.readFileToString(new File("data.json"), "utf-8");
        List<Topic> previousTopics = new Gson().fromJson(data, new TypeToken<List<Topic>>() {
        }.getType());

        return previousTopics;
    }
}
