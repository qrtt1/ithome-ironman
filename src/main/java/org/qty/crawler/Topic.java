package org.qty.crawler;

public class Topic {

    String title;
    String url;
    int view;

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

    public int getView() {
        return view;
    }

    public void setView(int view) {
        this.view = view;
    }

    @Override
    public String toString() {
        return "Topic{" +
                "title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", view=" + view +
                '}';
    }
}
