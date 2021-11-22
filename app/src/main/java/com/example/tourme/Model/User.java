package com.example.tourme.Model;

public class User {

    private String id;
    private String email;
    private String username;
    private String imageurl;

    public User(String id, String email, String username, String imageurl){
        this.id = id;
        this.email = email;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
