package fr.ul.miage;

import org.bson.types.ObjectId;

public class Table {
    private Object _id;
    private int numero;
    private int etage;
    private String etat;
    //private Order listOrder;
    private int nbCouvert;

    public Table(int etage, String etat, int nbCouvert) {
        this.etage = etage;
        this.etat = etat;
        this.nbCouvert = nbCouvert;
    }

    public Object get_id() { return _id; }

    public void set_id(Object _id) { this._id = _id; }

    public int getNumero() { return numero; }

    public void setNumero(int number) { this.numero = numero; }

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

    public String getInfosTable(){
        return null;

    }

    @Override
    public String toString() {
        return "Table{" +
                "_id=" + _id +
                ", numero=" + numero +
                ", etage=" + etage +
                ", etat='" + etat + '\'' +
                ", nbCouvert=" + nbCouvert +
                '}';
    }
}
