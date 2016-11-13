package com.example.leesangyoon.appproject;

import android.util.Log;

/**
 * Created by daddyslab on 2016. 11. 13..
 */
public class Patient {
    private String workerId = null;
    private String protectorId = null;
    private String patientName = null;
    private String birthday = null;
    private String relation = null;
    private String gender = null;
    private String roomNumber = null;

    public String getWorkerId() {
        return workerId;
    }

    public void setWorkerId(String workerId) {
        this.workerId = workerId;
    }

    public String getProtectorId() {
        return protectorId;
    }

    public void setProtectorId(String protectorId) {
        this.protectorId = protectorId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    private Patient() {
    }

    private static class Singleton {
        private static final Patient patient = new Patient();
    }

    public static Patient getInstance() {
        Log.e("development", "create singleton instance : User");
        return Singleton.patient;
    }
}
