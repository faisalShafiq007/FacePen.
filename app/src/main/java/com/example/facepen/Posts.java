package com.example.facepen;

public class Posts {
    public  String uid ,time , profileimage , postimage , fullname , descryption , date;

    public Posts()
    {

    }
    public Posts(String uid, String time, String profileimage, String postimage, String fullname, String descryption, String date) {
        this.uid = uid;
        this.time = time;
        this.profileimage = profileimage;
        this.postimage = postimage;
        this.fullname = fullname;
        this.descryption = descryption;
        this.date = date;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }

    public String getPostimage() {
        return postimage;
    }

    public void setPostimage(String postimage) {
        this.postimage = postimage;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getDescryption() {
        return descryption;
    }

    public void setDescryption(String descryption) {
        this.descryption = descryption;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

