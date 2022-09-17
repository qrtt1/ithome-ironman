package org.qty.crawler;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class IOUtils {

    public static List<Topic> loadPreviousTopics() throws IOException {
        File dataFile = new File("data.json");
        if (!dataFile.exists()) {
            return new ArrayList<>();
        }
        String data = FileUtils.readFileToString(dataFile, "utf-8");
        List<Topic> previousTopics = new Gson().fromJson(data, new TypeToken<List<Topic>>() {
        }.getType());

        return previousTopics;
    }
}
