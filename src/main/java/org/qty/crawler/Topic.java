package org.qty.crawler;

public class Topic {

    String category;
    String title;
    String url;
    String author;
    String profileUrl;
    String anchor;
    int view;
    long lastUpdated;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public int getView() {
        return view;
    }

    public void setView(int view) {
        this.view = view;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getAnchor() {
        return anchor;
    }

    public void setAnchor(String anchor) {
        this.anchor = anchor;
    }

    @Override
    public String toString() {
        return "Topic{" +
                "category='" + category + '\'' +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", author='" + author + '\'' +
                ", profileUrl='" + profileUrl + '\'' +
                ", view=" + view +
                ", lastUpdated=" + lastUpdated +
                '}';
    }
}
