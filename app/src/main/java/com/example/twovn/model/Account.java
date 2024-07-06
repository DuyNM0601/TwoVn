package com.example.twovn.model;

public class Account {
    private String email;
    private String userName;
    private String address;
    private String phoneNumber;

    public Account(String email, String userName, String address, String phoneNumber) {
        this.email = email;
        this.userName = userName;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    public Account() {

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
