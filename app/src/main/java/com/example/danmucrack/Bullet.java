package com.example.danmucrack;

public class Bullet {

    private String uid;
    private String comment;

    public Bullet(String uid, String comment) {
        this.uid = uid;
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public String getUid() {
        return uid;
    }
}
