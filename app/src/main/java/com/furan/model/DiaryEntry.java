package com.furan.model;

import java.util.Date;

public class DiaryEntry {
    private long id;
    private String title;
    private String content;
    private String userName;  // 新增
    private Date date;

    public DiaryEntry() {}

    public DiaryEntry(String title, String content, Date date,String userName) {
        this.title = title;
        this.content = content;
        this.date = date;
        this.userName = userName;
    }

    public DiaryEntry(long id, String title, String content, Date date,String userName) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.date = date;
        this.userName = userName;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
}