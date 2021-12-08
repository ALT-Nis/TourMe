package com.example.tourme.Model;

public class Oglas {
    String idOglasa;
    String grad;
    int brojOcena;
    double ocena;
    String userId;
    String opis;
    String imageurl;
    String username;

    public Oglas(){

    }

    public Oglas(String idOglasa, String grad, double ocena, int brojOcena, String userId, String opis, String imageurl, String username){
        this.idOglasa = idOglasa;
        this.grad = grad;
        this.ocena = ocena;
        this.brojOcena = brojOcena;
        this.userId = userId;
        this.opis = opis;
        this.imageurl=imageurl;
        this.username = username;
    }

    public String getIdOglasa() { return idOglasa; }

    public void setIdOglasa(String idOglasa) { this.idOglasa = idOglasa; }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOpis() {
        return opis;
    }

    public void setOpis(String opis) {
        this.opis = opis;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public void setGrad(String grad) { this.grad = grad; }

    public void setOcena(double ocena) { this.ocena = ocena; }

    public double getOcena() { return ocena; }

    public String getGrad() {  return grad; }

    public int getBrojOcena() { return brojOcena; }

    public void setBrojOcena(int brojOcena) { this.brojOcena = brojOcena; }
}
