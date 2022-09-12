package org.qty.crawler;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
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

    Set<Article> articles = new TreeSet<>();

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

    public Set<Article> getArticles() {
        return articles;
    }

    public void setArticles(Set<Article> articles) {
        this.articles = articles;
    }


    @Override
    public String toString() {
        return "Topic{" + "category='" + category + '\'' + ", title='" + title + '\'' + ", url='" + url + '\'' + ", author='" + author + '\'' + ", profileUrl='" + profileUrl + '\'' + ", anchor='" + anchor + '\'' + ", view=" + view + ", lastUpdated=" + lastUpdated + ", articles=" + articles + '}';
    }


    @Override
    public int compareTo(Topic o) {
        return o.getView() - view;
    }

    public void updateStatus() {
        LocalDate deadlineForStarting = LocalDate.of(2022, 9, 17);
        LocalDate deadlineForEnding = deadlineForStarting.plusDays(30);

        // status: not started
        // 1. there are no articles and before the date: 9/17
        // 2. if the date after 9/16 and no articles should be failed not be not started
        if (this.articles.isEmpty()) {
            if (LocalDate.now().isBefore(deadlineForStarting)) {
                status = Status.NOT_STARTED;
            } else {
                status = Status.FAILED;
            }
            return;
        }

        // status: failed
        // 1. have not published any articles before 9/17 (at least should get started at 9/16)
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
