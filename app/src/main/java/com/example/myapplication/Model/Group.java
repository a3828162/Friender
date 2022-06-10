package com.example.myapplication.Model;

public class Group {

    private String id;
    private String name;
    private String imageURl;

    public Group(String id, String name, String imageURl) {
        this.id = id;
        this.name = name;
        this.imageURl = imageURl;
    }

    public Group() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageURl() {
        return imageURl;
    }

    public void setImageURl(String imageURl) {
        this.imageURl = imageURl;
    }
}
