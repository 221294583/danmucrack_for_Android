package com.example.dmc;

public class UID {

    private String uid;
    private String link;

    public UID(String s){
        uid=s;
        link="https://space.bilibili.com/"+uid;
    }

    public UID(String name,String s){
        uid=name;
        link=s;
    }

    public String getUID() {
        return uid;
    }

    public String getLINK() {
        return link;
    }
}
