package com.example.demoserver.Module;

public class User {

    private String id;
    private String username;
    private String email;
    private String imageURl;
    private String status;
    private String search;
    private String statesign;
    private String habbit;
    private String birthday;
    private int flower;

    public User()
    {

    }

    public User(String id, String username, String email, String imageURl, String status, String search, String statesign, String habbit, String birthday, int flower) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.imageURl = imageURl;
        this.status = status;
        this.search = search;
        this.statesign = statesign;
        this.habbit = habbit;
        this.birthday = birthday;
        this.flower = flower;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getImageURl()
    {
        return imageURl;
    }

    public void setImageURl(String imageURl)
    {
        this.imageURl = imageURl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatesign() {
        return statesign;
    }

    public void setStatesign(String statesign) {
        this.statesign = statesign;
    }

    public String getHabbit() {
        return habbit;
    }

    public void setHabbit(String habbit) {
        this.habbit = habbit;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public int getFlower() {
        return flower;
    }

    public void setFlower(int flower) {
        this.flower = flower;
    }

}
