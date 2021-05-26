package fr.ul.miage;

import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class Order {
    private ObjectId _id;
    private String dateDebut;
    private String dateFin;
    private List<Preparation> Preparation;
    private Double montant;

    public Order(ObjectId _id, String dateDebut, String dateFin, Double montant) {
        this._id = _id;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.montant = montant;
        Preparation = new ArrayList<Preparation>();
    }

    public ObjectId get_id() {
        return _id;
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    public String getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(String dateDebut) {
        this.dateDebut = dateDebut;
    }

    public String getDateFin() {
        return dateFin;
    }

    public void setDateFin(String dateFin) {
        this.dateFin = dateFin;
    }

    public List<fr.ul.miage.Preparation> getPreparation() {
        return Preparation;
    }

    public void setPreparation(List<fr.ul.miage.Preparation> preparation) {
        Preparation = preparation;
    }

    public Double getMontant() {
        return montant;
    }

    public void setMontant(Double montant) {
        this.montant = montant;
    }

    @Override
    public String toString() {
        return  "dateDebut='" + dateDebut + '\'' +
                ", dateFin='" + dateFin + '\'' +
                ", Preparation=" + Preparation +
                ", montant=" + montant;
    }
}
