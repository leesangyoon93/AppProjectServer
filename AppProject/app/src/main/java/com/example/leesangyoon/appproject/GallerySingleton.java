package com.example.leesangyoon.appproject;

import android.util.Log;

/**
 * Created by daddyslab on 2016. 10. 31..
 */
public class GallerySingleton {

    private String id = "00a00a0aaa00aa000000a0a0";
    private String author = "";
    private String content = "";
    private String title = "";

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    private String image = "";
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

    private GallerySingleton() {}

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
        private static final GallerySingleton gallery = new GallerySingleton();
    }

    public static GallerySingleton getInstance () {
        Log.e("development","create singleton instance : Article");
        return Singleton.gallery;
    }

    public String getId(){
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void initGallery(){
        this.id="00a00a0aaa00aa000000a0a0";
        this.content="";
        this.title="";
        this.author="";
        this.commentCount=0;
        this.image="";
        this.date="";
    }
}
