package com.niu.myapplication.RecyclerView;

/**
 * Created by A on 2016/12/13.
 */

public class MainSubject {
    private String name;
    private int thumbnail;

    public MainSubject() {
    }

    public MainSubject(String name, int thumbnail) {
        this.name = name;
        this.thumbnail = thumbnail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(int thumbnail) {
        this.thumbnail = thumbnail;
    }
}
