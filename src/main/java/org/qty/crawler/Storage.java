package org.qty.crawler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.FileUtils;
import org.qty.crawler.uidata.UIDataModel;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.qty.crawler.IOUtils.loadPreviousTopics;

public interface Storage {
    List<Topic> loadSavedTopics() throws IOException;

    void saveTopics(List<Topic> savedTopics) throws IOException;
}


class DefaultStorage implements Storage {
    @Override
    public List<Topic> loadSavedTopics() throws IOException {
        return loadPreviousTopics();
    }

    @Override
    public void saveTopics(List<Topic> savedTopics) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Collections.sort(savedTopics, Comparator.comparing(Topic::getUrl));
        FileUtils.write(new File("data.json"), gson.toJson(savedTopics), "utf-8");
        FileUtils.write(new File("ui-data.json"), gson.toJson(UIDataModel.convertForUI(savedTopics)), "utf-8");
    }
}