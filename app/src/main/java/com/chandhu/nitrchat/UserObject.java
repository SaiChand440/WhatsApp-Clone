package com.chandhu.nitrchat;

public class UserObject {
    private String name,phoneNumber;

    public UserObject(String name,String phoneNumber){
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
