package com.furan.model;

import java.util.Date;

public class DiaryEntry {
    private long id;
    private String title;
    private String content;
    private Date date;

    public DiaryEntry() {}

    public DiaryEntry(String title, String content, Date date) {
        this.title = title;
        this.content = content;
        this.date = date;
    }

    public DiaryEntry(long id, String title, String content, Date date) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.date = date;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }
}