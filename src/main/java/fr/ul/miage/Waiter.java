package fr.ul.miage;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.SimpleTheme;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.Panel;
import org.bson.types.ObjectId;
import org.w3c.dom.Text;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Waiter extends Staff {
    private List<ObjectId> Table;

    public Waiter(String login, String mdp, String nom, String prenom) {
        super(login, mdp, nom, prenom);
        Table = new ArrayList<ObjectId>();
    }

    public List<ObjectId> getTable() {
        return Table;
    }

    public void setTable(List<ObjectId> table) {
        Table = table;
    }

    @Override
    public void Screen(){
        Panel panel = super.deconnection();
        panel.setLayoutManager(new GridLayout(2));
        panel.addComponent(new EmptySpace(new TerminalSize(0, 0)));
        //panel.addComponent(new Label("Menu serveur").setPosition(new TerminalPosition(1)));
        panel.addComponent(new Label("Menu serveur"));
        panel.addComponent(new EmptySpace());
        List<Integer> listEtages = getFloors(getDbQueries().getWaiterTables(this));
        List<Table> listTables = getDbQueries().getWaiterTables(this);

        for (Integer etage :listEtages) {
            panel.addComponent(new Label("Etage n°" + etage.intValue() + " :"));
            panel.addComponent(new EmptySpace());
            for (Table t : listTables) {
                if(etage.intValue() == t.getEtage()) {
                    String str = "Table n°" + t.getNumero() + " | Etat : " + t.getEtat();
                    Label lbl = setColorStates(t,new Label(str));
                    panel.addComponent(lbl);
                    new Button("Consulter", new Runnable() {
                        @Override
                        public void run() {
                            screenInfosTable(t);
                        }
                    }).addTo(panel);

                }
            }
            panel.addComponent(new EmptySpace());
            panel.addComponent(new EmptySpace());
        }

        BasicWindow window = new BasicWindow();
        window.setComponent(panel);
        try {
            MainTerminal.getConsole().switchWindow(window);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Récupère une liste d'entier en fonction du nombre d'étages existant.
     * @param lTable
     * @return lEtage
     */
    public List<Integer> getFloors(List<Table> lTable){
        List<Integer> lEtage = new ArrayList<Integer>();
        for (Table tab : lTable) {
            if (!lEtage.contains(Integer.valueOf(tab.getEtage()))){
                lEtage.add((Integer) tab.getEtage());
            }
        }
        return lEtage;
    }


    /**
     * Retourne un label colorisé en fonction d'un état
     * @param table
     * @param lbl
     * @return lbl (type Label)
     */
    public Label setColorStates(Table table, Label lbl){
        switch (table.getEtat()){
            case "libre":
                lbl.setBackgroundColor(TextColor.ANSI.GREEN);
                break;
            case "occupé":
                lbl.setBackgroundColor(TextColor.ANSI.YELLOW_BRIGHT);
                break;
            case "a débarassée":
                lbl.setBackgroundColor(TextColor.ANSI.RED_BRIGHT);
                break;
            case "réservé":
                lbl.setBackgroundColor(new TextColor.RGB(255,127,0));
                break;
        }
        return lbl;
    }


    /**
     * Affiche la fenêtre terminale des informations sur une table
     * @param table
     */
    public void screenInfosTable(Table table){
        Panel panel = super.deconnection();
        panel.addComponent(new EmptySpace());
        panel.addComponent(new Label(table.toString()));

        new Button("Retour", new Runnable() {
            @Override
            public void run() {
                Screen();
            }
        }).addTo(panel);

        if (table.getEtat().equals("occupé")) {
            new Button("Ajouter plat", new Runnable() {
                @Override
                public void run() {
                    orderEntryScreen(table);
                }
            }).addTo(panel);
        }

        panel.addComponent(new EmptySpace());

        BasicWindow window = new BasicWindow();
        window.setComponent(panel);
        try {
            MainTerminal.getConsole().switchWindow(window);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    /**
     * Affiche la fenêtre terminale de la commande courante à une table donné
     * @param table (type Table)
     */
    public void orderEntryScreen(Table table){
        ArrayList<ComboBox<String>> ingredientsList = new ArrayList<>();
        Panel panel = new Panel();
        panel.setLayoutManager(new GridLayout(1));

        panel.addComponent(new EmptySpace());

        panel.addComponent(new Label("Commande : "));

        //On récupère pas la commande pour le client
        /*if(table.getOrder() != null){
            for (Preparation p : table.getOrder().getPreparation()){
                panel.addComponent(new Label(p.Plat));
            }

        else{
            Order ord = new Order();
        }
        }*/
        panel.addComponent(new EmptySpace());

        panel.addComponent(new Label("Catégorie du plat :"));
        ComboBox<String>categories = categoriesComboBox().addTo(panel);



        new Button("Retour", new Runnable() {
            @Override
            public void run() {
                screenInfosTable(table);
            }
        }).addTo(panel);

        new Button("Valider ajout", new Runnable() {
            @Override
            public void run() {
               // getDbQueries().newPreparation();
                orderEntryScreen(table);
            }
        }).addTo(panel);

        new Button("Terminer commande", new Runnable() {
            @Override
            public void run() {
                //getDbQueries().updateOrder(table.getOrder());
                Screen();
            }
        }).addTo(panel);

        BasicWindow window = new BasicWindow();
        window.setComponent(panel);
        try {
            MainTerminal.getConsole().switchWindow(window);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private ComboBox<String> categoriesComboBox(){
        ComboBox<String> categories = new ComboBox<String>();
        for(Categorie c : super.getDbQueries().getCategories()){
            categories.addItem(c.nom);
        }
        return categories;
    }

}


