package com.niu.myapplication.RecyclerView;

/**
 * Created by A on 2016/12/14.
 */

public class Article {
    public String title;
    public String date;
    public String content;
    public String replaycontent;
    public String userID;

    public Article() {
    }

    public Article(String title, String date, String content,String userID, String replaycontent) {
        this.title = title;
        this.date = date;
        this.content = content;
        this.userID = userID;
        this.replaycontent = replaycontent;
    }

    public String getTitle() { return title; }

    public String getDate() { return date; }

    public String getContent() { return content; }

    public String getReplaycontent() { return replaycontent; }

    public String getUserID() {
        return userID;
    }
}
