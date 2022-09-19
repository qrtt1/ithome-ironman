package org.qty.crawler.uidata;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.qty.crawler.Article;
import org.qty.crawler.Topic;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


import static org.qty.crawler.Storage.loadPreviousTopics;

public class UIDataModel {

    List<String> categories = new ArrayList<>();
    Map<String, List<Topic>> topics = new HashMap<>();


    public static void main(String[] args) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
//        System.out.println(gson.toJson(convertForUI(loadPreviousTopics())));
        gson.toJson(convertForUI(loadPreviousTopics()));
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
            model.topics.get(k).stream().forEach(t -> {
                // only keep the last one for saving file size
                if (t.getArticles().size() > 1) {
                    List<Article> lastOne = t.getArticles().stream().skip(t.getArticles().size() - 1).collect(Collectors.toList());
                    lastOne.iterator().next().setPublished(null);
                    t.setArticles(lastOne);
                }
            });
        });

        return model;

    }
}
