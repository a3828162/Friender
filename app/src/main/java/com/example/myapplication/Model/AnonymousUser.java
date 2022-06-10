package com.example.myapplication.Model;

public class AnonymousUser {

    String id;

    public AnonymousUser(String id) {
        this.id = id;
    }

    public AnonymousUser() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
