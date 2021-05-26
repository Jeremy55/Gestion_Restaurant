package fr.ul.miage;

import org.bson.types.ObjectId;

public class Table {
    private ObjectId _id;
    private int numero;
    private int etage;
    private String etat;
    //private Order listOrder;
    private int nbCouvert;

    public Table(ObjectId _id, int numero, int etage, String etat, int nbCouvert) {
        this._id = _id;
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

    public ObjectId get_id() {
        return _id;
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }
}