package fr.ul.miage;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import fr.ul.miage.MainTerminal;
import fr.ul.miage.Staff;
import org.bson.types.ObjectId;

import java.io.IOException;
import java.util.ArrayList;

public class Cook extends Staff {

    public Cook(String login, String mdp, String nom, String prenom) {
        super(login, mdp, nom, prenom);
    }
        public class Plat {
            ObjectId _id;
            String nom;
            ArrayList<String> Ingredient;
            ArrayList<String> Categorie;
            Double prix;
            boolean platDuJour;

            public Plat(ObjectId _id, String nom, ArrayList<String> ingredient, ArrayList<String> categorie, Double prix, boolean platDuJour) {
                this._id = _id;
                this.nom = nom;
                Ingredient = ingredient;
                Categorie = categorie;
                this.prix = prix;
                this.platDuJour = platDuJour;
            }
        }
    @Override
    public void Screen(){
        Panel panel = super.deconnection();
        panel.setLayoutManager(new GridLayout(2));
        panel.addComponent(new EmptySpace(new TerminalSize(0, 0)));
        panel.addComponent(new Label("Menu cuisinier"));
        BasicWindow window = new BasicWindow();
        window.setComponent(panel);
        try {
            MainTerminal.getConsole().switchWindow(window);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
