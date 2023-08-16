package org.qty.crawler;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

enum Status {
    NOT_STARTED, ONGOING, FAILED, COMPLETED
}

public class Topic implements Comparable<Topic> {

    String category;
    String title;
    String url;
    String author;
    String profileUrl;
    String anchor;
    int view;
    long lastUpdated;

    List<Article> articles = new ArrayList<>();

    Status status = Status.NOT_STARTED;

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

    public List<Article> getArticles() {
        return articles;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Topic{");
        sb.append("category='").append(category).append('\'');
        sb.append(", title='").append(title).append('\'');
        sb.append(", url='").append(url).append('\'');
        sb.append(", author='").append(author).append('\'');
        sb.append(", profileUrl='").append(profileUrl).append('\'');
        sb.append(", anchor='").append(anchor).append('\'');
        sb.append(", view=").append(view);
        sb.append(", lastUpdated=").append(lastUpdated);
        sb.append(", articles=").append(articles);
        sb.append(", status=").append(status);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int compareTo(Topic o) {
        return o.getView() - view;
    }

    public void updateStatus() {
        LocalDate deadlineForStarting = LocalDate.of(Storage.YEAR, 9, 17);

        // status: not started
        // 1. there are no articles and before the date: 9/17
        // 2. if the date after 9/17 and no articles should be failed not be not started
        if (this.articles.isEmpty()) {
            if (LocalDate.now().isBefore(deadlineForStarting)) {
                status = Status.NOT_STARTED;
            } else {
                status = Status.FAILED;
            }
            return;
        }

        // status: failed
        // 1. have not published any articles before 9/17
        // 2. there are articles between [start date] and min(today, end date) but not matched the criteria one articles per day

        // status: ongoing
        // 1. the topic has been started and not failed

        // how? generate 30 date and matching the articles
        LocalDate firstDate = this.articles.stream().sorted().findFirst().get().getPublished().toLocalDate();
        Set<LocalDate> expectedDates = IntStream.range(0, 30).boxed().map(d -> firstDate.plusDays(d)).collect(Collectors.toSet());
        this.articles.stream().forEach(a -> {
            expectedDates.remove(a.getPublished().toLocalDate());
        });

        if (expectedDates.isEmpty()) {
            status = Status.COMPLETED;
            return;
        }

        Set<LocalDate> unfinishedDates = expectedDates.stream().filter((d) -> d.isBefore(LocalDate.now())).collect(Collectors.toSet());
        if (unfinishedDates.size() > 1) {
            status = Status.FAILED;
        } else {
            status = Status.ONGOING;
        }

        return;
    }
}
