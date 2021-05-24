package fr.ul.miage;

import org.bson.types.ObjectId;

public class Table {
    private int numero;
    private int etage;
    private String etat;
    //private Order listOrder;
    private int nbCouvert;

    public Table(int numero, int etage, String etat, int nbCouvert) {
        this.numero = numero;
        this.etage = etage;
        this.etat = etat;
        this.nbCouvert = nbCouvert;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public int getEtage() {
        return etage;
    }

    public void setEtage(int etage) {
        this.etage = etage;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public int getNbCouvert() {
        return nbCouvert;
    }

    public void setNbCouvert(int nbCouvert) {
        this.nbCouvert = nbCouvert;
    }
}