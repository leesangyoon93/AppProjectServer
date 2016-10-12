package com.example.leesangyoon.appproject;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by daddyslab on 2016. 10. 11..
 */
public class User {

    private String userId = null;
    private String userName = null;
    private String phoneNumber = null;
    private String nursingHomeId = null;
    private String nursingHomeName = null;
    private String nursingHomeAddress = null;
    private String nursingHomePhoneNumber = null;
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

    public String getNursingHomeName() {
        return nursingHomeName;
    }

    public void setNursingHomeName(String nursingHomeName) {
        this.nursingHomeName = nursingHomeName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNursingHomeId() {
        return nursingHomeId;
    }

    public void setNursingHomeId(String nursingHomeId) {
        this.nursingHomeId = nursingHomeId;
    }

    public int getAuth() {
        return auth;
    }

    public void setAuth(int auth) {
        this.auth = auth;
    }

    public String getNursingHomeAddress() {
        return nursingHomeAddress;
    }

    public void setNursingHomeAddress(String nursingHomeAddress) {
        this.nursingHomeAddress = nursingHomeAddress;
    }

    public String getNursingHomePhoneNumber() {
        return nursingHomePhoneNumber;
    }

    public void setNursingHomePhoneNumber(String nursingHomePhoneNumber) {
        this.nursingHomePhoneNumber = nursingHomePhoneNumber;
    }

}
