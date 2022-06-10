package com.example.myapplication.Model;

public class Note {

    String id;
    String user;
    String date;
    String content;

    public Note() {
    }

    public Note(String id,String user, String date, String content) {
        this.id = id;
        this.user = user;
        this.date = date;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
