package com.example.tourme.Model;

public class Oglas {
    String grad;
    double ocena;

    public Oglas(){

    }
    public Oglas(String grad, double ocena){
        this.grad = grad;
        this.ocena = ocena;
    }

    public void setGrad(String grad) { this.grad = grad; }

    public void setOcena(double ocena) { this.ocena = ocena; }

    public double getOcena() { return ocena; }

    public String getGrad() {  return grad; }
}
