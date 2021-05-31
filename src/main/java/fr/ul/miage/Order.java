package fr.ul.miage;

import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private ObjectId _id;
    private String dateDebut;
    private String dateFin;
    private List<ObjectId> Preparation;
    private Double montant;

    public Order(ObjectId _id, Double montant) {
        this._id = _id;
        this.dateDebut = DateTimeFormatter.ofPattern("dd-MM-uuuu HH:mm:ss").format(LocalDateTime.now());
        this.montant = montant;
        Preparation = new ArrayList<ObjectId>();
    }
/*
    public Order(ObjectId _id, String dateDebut, String dateFin, Double montant) {
        this._id = _id;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.montant = montant;
        Preparation = new ArrayList<ObjectId>();
    }
*/
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

    public List<ObjectId> getPreparation() {
        return Preparation;
    }

    public void setPreparation(List<ObjectId> preparation) {
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
        return  "_id='" + _id + '\'' +
                ",dateDebut='" + dateDebut + '\'' +
                ", dateFin='" + dateFin + '\'' +
                ", Preparation=" + Preparation +
                ", montant=" + montant;
    }
}
