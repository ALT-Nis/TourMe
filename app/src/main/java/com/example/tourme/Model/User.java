package com.example.tourme.Model;

public class User {

    private String id;
    private String email;
    private String username;
    private String imageurl;
    private String brojOglasa;

    public User(String id, String email, String username, String imageurl, String brojOglasa){
        this.id = id;
        this.email = email;
        this.username = username;
        this.imageurl = imageurl;
        this.brojOglasa = brojOglasa;
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

    public void setBrojOglasa(String brojOglasa) { this.brojOglasa = brojOglasa; }

    public String getBrojOglasa() { return brojOglasa; }
}
