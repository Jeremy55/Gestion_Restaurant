package fr.ul.miage;

import org.bson.types.ObjectId;

public abstract class Staff {


    private ObjectId _id;
    private String login;
    private String mdp;
    private String nom;
    private String prenom;

    public Staff(ObjectId id, String login, String mdp, String nom, String prenom) {
        this._id = id;
        this.login = login;
        this.mdp = mdp;
        this.nom = nom;
        this.prenom = prenom;
    }

    public ObjectId getId() {
        return _id;
    }

    public void setId(ObjectId id) {
        this._id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getMdp() {
        return mdp;
    }

    public void setMdp(String mdp) {
        this.mdp = mdp;
    }


    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    abstract void Screen();


}
