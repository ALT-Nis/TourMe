package com.example.tourme.Model;

public class User {

    private String id;
    private String email;
    private String username;
    private String imageurl;
    private String brojOglasa;
    private String status;

    public User(){

    }

    public User(String id, String email, String username, String imageurl, String brojOglasa, String status){
        this.id = id;
        this.email = email;
        this.username = username;
        this.imageurl = imageurl;
        this.brojOglasa = brojOglasa;
        this.status = status;
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

    public String getImageurl() { return imageurl; }

    public void setImageurl(String imageurl) { this.imageurl = imageurl; }

    public void setBrojOglasa(String brojOglasa) { this.brojOglasa = brojOglasa; }

    public String getBrojOglasa() { return brojOglasa; }

    public String getStatus() { return status; }

    public void setStatus(String status) {
        this.status = status;
    }
}
