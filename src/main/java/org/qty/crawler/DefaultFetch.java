package org.qty.crawler;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;

public class DefaultFetch implements Fetch {
    @Override
    public String get(String source) {
        try {
            Connection connect = Jsoup.connect(source);
            return connect.execute().body();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
