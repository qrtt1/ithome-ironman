package org.qty.crawler;

import org.jsoup.Jsoup;

import java.io.IOException;

public class DefaultFetch implements Fetch {
    @Override
    public String get(String source) {
        try {
            return Jsoup.connect(source).get().toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
