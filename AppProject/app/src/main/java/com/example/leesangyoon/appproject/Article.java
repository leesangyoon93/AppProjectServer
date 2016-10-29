package com.example.leesangyoon.appproject;

import android.util.Log;

/**
 * Created by daddyslab on 2016. 10. 29..
 */
public class Article {
    private String id = "00a00a0aaa00aa000000a0a0";
    private String author = "";
    private String content = "";
    private String title = "";
    private String date = "";

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    private int commentCount = 0;

    public void setDate(String date) {
        this.date = date;
    }

    private Article() {}

    public String getDate() {
        return date;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    private static class Singleton {
        private static final Article article = new Article();
    }

    public static Article getInstance () {
        Log.e("development","create singleton instance : Article");
        return Singleton.article;
    }

    public String getId(){
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void initArticle(){
        this.id="00a00a0aaa00aa000000a0a0";
        this.content="";
        this.title="";
        this.author="";
        this.commentCount=0;
        this.date="";
    }
}
