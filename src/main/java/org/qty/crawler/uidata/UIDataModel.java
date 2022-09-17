package org.qty.crawler.uidata;

import org.qty.crawler.Article;
import org.qty.crawler.Topic;

import java.io.IOException;
import java.util.*;


import static org.qty.crawler.IOUtils.loadPreviousTopics;

public class UIDataModel {

    List<String> categories = new ArrayList<>();
    Map<String, List<Topic>> topics = new HashMap<>();


    public static void main(String[] args) throws IOException {
        convertForUI(loadPreviousTopics());
    }

    public static UIDataModel convertForUI(List<Topic> allTopics) {
        UIDataModel model = new UIDataModel();

        allTopics.stream().forEach(t -> {
            if (!model.topics.containsKey(t.getCategory())) {
                model.topics.put(t.getCategory(), new ArrayList<>());
            }
            model.topics.get(t.getCategory()).add(t);
        });

        // sort
        model.categories.addAll(model.topics.keySet());
        Collections.sort(model.categories);
        model.topics.keySet().forEach(k -> {
            Collections.sort(model.topics.get(k));
        });

        return model;

    }
}
