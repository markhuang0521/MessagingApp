package com.ming.messagingapp.user;

public class User {
    private String name;
    private String phoneNumber;
    private String uid;

    public User(String name, String phoneNumber, String uid) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public User(String name, String phoneNumber) {

        this.name = name;
        this.phoneNumber = phoneNumber;
        this.uid = "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
