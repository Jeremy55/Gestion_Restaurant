package fr.ul.miage;

import org.bson.types.ObjectId;

import java.util.Date;

public class Preparation implements Comparable<Preparation>{
    ObjectId _id;
    String heureCommande;
    Boolean debut;
    String fin;
    ObjectId Plat;
    Boolean menuEnfant;
    Date trueDate;

    public Preparation(String heureCommande, Boolean debut, ObjectId plat, Boolean menuEnfant) {
        this.heureCommande = heureCommande;
        this.debut = debut;
        Plat = plat;
        this.menuEnfant = menuEnfant;
    }

    public Preparation(ObjectId _id, String heureCommande, Boolean debut, ObjectId plat, Boolean menuEnfant) {
        this._id = _id;
        this.heureCommande = heureCommande;
        this.debut = debut;
        Plat = plat;
        this.menuEnfant = menuEnfant;
    }

    public Preparation(ObjectId _id, String heureCommande, Boolean debut, String fin, ObjectId plat, Boolean menuEnfant) {
        this._id = _id;
        this.heureCommande = heureCommande;
        this.debut = debut;
        this.fin = fin;
        Plat = plat;
        this.menuEnfant = menuEnfant;
    }

    @Override
    public String toString() {
        return "Preparation{" +
                "_id=" + _id +
                ", heureCommande=" + heureCommande +
                ", debut=" + debut +
                ", fin=" + fin +
                ", Plat=" + Plat +
                ", menuEnfant=" + menuEnfant +
                '}';
    }

    @Override
    public int compareTo(Preparation o) {
        return trueDate.compareTo(o.trueDate);
    }
}
