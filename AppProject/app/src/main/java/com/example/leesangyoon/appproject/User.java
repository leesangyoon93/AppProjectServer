package com.example.leesangyoon.appproject;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by daddyslab on 2016. 10. 11..
 */
public class User {

    private String Id = null;
    private int auth;

    private User() {
    }

    private static class Singleton {
        private static final User user = new User();
    }

    public static User getInstance() {
        Log.e("development", "create singleton instance : User");
        return Singleton.user;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public int getAuth() {
        return auth;
    }

    public void setAuth(int auth) {
        this.auth = auth;
    }
}
