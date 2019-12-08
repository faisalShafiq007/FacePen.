package com.example.facepen;

public class comments_class {
   public String comment,date,time,username;

   public comments_class(){

   }

    public comments_class(String comment, String date, String timeuid, String username) {
        this.comment = comment;
        this.date = date;
        this.time = timeuid;
        this.username = username;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
