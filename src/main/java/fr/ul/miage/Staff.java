package fr.ul.miage;

import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.Panel;
import org.bson.types.ObjectId;

import java.io.IOException;

public abstract class Staff {

    private ObjectId _id;
    private String login;
    private String mdp;
    private String nom;
    private String prenom;
    private DBQueries dbQueries;

    public Staff(String login, String mdp, String nom, String prenom) {
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

    public  void setDbQueries(DBQueries dbQueries) {
        this.dbQueries = dbQueries;
    }

    protected DBQueries getDbQueries(){
        return dbQueries;
    }

    abstract void screen() throws IOException;

    protected Panel deconnection(){
        Panel panel = new Panel();
        new Button("Déconnexion", new Runnable() {
            @Override
            public void run() {
                try {
                    LoginScreen loginScreen = new LoginScreen();
                    MainTerminal.getConsole().switchWindow(loginScreen);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).addTo(panel);
        return panel;
    }


}
