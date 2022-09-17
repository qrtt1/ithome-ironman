package org.qty.crawler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.FileUtils;
import org.jsoup.nodes.Document;
import org.qty.crawler.uidata.UIDataModel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.qty.crawler.IOUtils.loadPreviousTopics;

public class DebugViewCrawler {

    public static void main(String[] args) throws IOException {
        Crawler crawler = new Crawler(new DefaultFetch());
        List<Topic> savedTopics = loadPreviousTopics();
        savedTopics.stream().filter(t -> t.url.equals("https://ithelp.ithome.com.tw/users/20138542/ironman/4954")).forEach(topic -> {
            crawler.update(topic);
            System.out.println(topic);
        });
    }

}
