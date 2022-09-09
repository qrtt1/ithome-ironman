package org.qty.crawler;

import java.time.LocalDateTime;

public class Article implements Comparable<Article> {
    String url;
    String title;
    LocalDateTime published;
    String iso8601Published;

    public String getIso8601Published() {
        return iso8601Published;
    }

    public void setIso8601Published(String iso8601Published) {
        this.iso8601Published = iso8601Published;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getPublished() {
        return published;
    }

    public void setPublished(LocalDateTime published) {
        this.published = published;
    }

    @Override
    public String toString() {
        return "Article{" +
                "url='" + url + '\'' +
                ", title='" + title + '\'' +
                ", published=" + published +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Article article = (Article) o;
        return url.equals(article.url);
    }

    @Override
    public int hashCode() {
        return url.hashCode();
    }

    @Override
    public int compareTo(Article o) {
        if (o == null || o.published == null) {
            throw new RuntimeException("Cannot compare with null");
        }
        return o.published.compareTo(this.published);
    }
}
