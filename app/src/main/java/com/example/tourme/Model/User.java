package com.example.tourme.Model;

public class User {

    private String id;
    private String username;
    private String imageurl;

    public User(String id, String username, String imageurl){
        this.id = id;
        this.username = username;
        this.imageurl = imageurl;
    }

    public User(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }
}
