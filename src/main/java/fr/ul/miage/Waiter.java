package fr.ul.miage;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.Panel;
import org.bson.types.ObjectId;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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
    public void screen(){
        Panel panel = super.deconnection();
        panel.setLayoutManager(new GridLayout(2));
        panel.addComponent(new EmptySpace(new TerminalSize(0, 0)));
        panel.addComponent(new Label("Menu serveur"));
        panel.addComponent(new EmptySpace());
        List<Integer> listEtages = getFloors(getDbQueries().getWaiterTables(this));
        List<fr.ul.miage.Table> listTables = getDbQueries().getWaiterTables(this);

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
            emptySpace(panel);
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
            case "débarassée":
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
        Panel panelInfo = new Panel();

        panelInfo.addComponent(new Label(table.toString()));
        panel.addComponent(panelInfo);
        Timer timer = new Timer();
        tempsReelInfosTable(panelInfo, table.get_id(), timer);

        emptySpace(panel);

        new Button("Retour", new Runnable() {
            @Override
            public void run() {
                timer.cancel();
                screen();
            }
        }).addTo(panel);

        if (table.getEtat().equals("occupé")) {
            new Button("Ajouter plat", new Runnable() {
                @Override
                public void run() {
                    timer.cancel();
                    orderEntryScreen(table);
                }
            }).addTo(panel);
        }

        if(table.getCommande() != null){
            new Button("Terminer commande", new Runnable() {
                @Override
                public void run() {
                    Order ord = getDbQueries().getOrderFromTable(table.getCommande());
                    getDbQueries().addEndDateToOrder(table);
                    table.setEtat("débarassée");
                    getDbQueries().updateTableDebarassee(table);
                    getDbQueries().removeOrderFromTable(table);
                    screen();
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
     * Affichage des informations de la table en temps réel
     * @param panel
     * @param oidTables
     * @param timer
     */
    private void tempsReelInfosTable(Panel panel, ObjectId oidTables, Timer timer){
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                panel.removeAllComponents();
                panel.addComponent(new Label(getDbQueries().getTable(oidTables).toString()));
            }
        }, 0, 4000);
    }

    /**
     * Affiche la fenêtre terminale de la commande courante à une table donné
     * @param table (type Table)
     */
    public void orderEntryScreen(Table table){

        ArrayList<ComboBox<String>> ingredientsList = new ArrayList<>();
        Panel panel = new Panel();
        panel.setLayoutManager(new GridLayout(3));
        panel.addComponent(new EmptySpace());

        new Button("Retour", new Runnable() {
            @Override
            public void run() {
                screenInfosTable(table);
            }
        }).addTo(panel);
        emptySpace(panel);

        panel.addComponent(new Label("Commande : "));
        emptySpace(panel);

        panel.addComponent(new Label("Catégorie du plat :"));
        emptySpace(panel);

        ComboBox<String> cat = getCategoriesComboBox(); // On ajoute dans la combobox les catégories de plats
        if(cat != null){
            cat.setSelectedIndex(0);
            panel.addComponent(cat);
        }
        else {
            panel.addComponent(new Label("Aucunes catégories de disponible "));
        }

        emptySpace(panel);

        Panel panSecond = new Panel();
        panel.addComponent(panSecond);

        //Listener pour exécuter un morceau de code dés qu'on sélectionne une catégorie

        if(cat != null){
            cat.addListener(new ComboBox.Listener() {
                @Override
                public void onSelectionChanged(int selectedIndex, int previousSelection, boolean changedByUserInteraction) {
                    String selected = cat.getSelectedItem();
                    getDishes(selected,panSecond,table);
                }
            });
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
     * Récupère les catégories disponibles et les ajoutes dans une ComboBox
     * @return categories
     */
    private ComboBox<String> getCategoriesComboBox(){
        ComboBox<String> categories = new ComboBox<String>();
        for(String c : getDbQueries().getCategoriesWithAtLeastOneDishAvailable()){
            categories.addItem(c);
        }
        if(categories.getItemCount() == 0){
            return null;
        }
        return categories;
    }


    /**
     * Récupère et ajoute les plats à un panel donné
     * @param cat
     * @param panel
     * @param table
     */
    private void getDishes(String cat, Panel panel,Table table){
        panel.removeAllComponents();
        panel.setLayoutManager(new GridLayout(3));
        panel.addComponent(new EmptySpace());
        emptySpace(panel);
        for(Cook.Plat plat : super.getDbQueries().getDishesAvailable(cat)) {
            panel.addComponent(new Label(plat.nom + " : " + plat.prix + " €"));;
            CheckBox check = new CheckBox("Menu enfant ?");
            panel.addComponent(check);
            ajouterCommande(check, table, plat).addTo(panel);
        }

        panel.addComponent(new EmptySpace());
    }

    /**
     *
     * @param checkBox
     * @param table
     * @param plat
     * @return
     */
    private Button ajouterCommande(CheckBox checkBox, Table table, Cook.Plat plat){
        return new Button("Ajouter produit", new Runnable() {
            @Override
            public void run() {
                checkAndActOnTheOrder(table,plat,checkBox);
                screenInfosTable(table);
            }
        });
    }

    /**
     * Faire des empty space (utile pour les layout à 2 colonnes)
     * @param panel
     */
    public void emptySpace(Panel panel){
        panel.addComponent(new EmptySpace());
        panel.addComponent(new EmptySpace());
    }


    /**
     * Regarde si une commande existe et effectue des opérations si oui ou si non
     */
    public void checkAndActOnTheOrder(Table table, Cook.Plat plat,CheckBox checkBox){
        Preparation p = null;
        if(checkBox.isChecked())
            p = new Preparation(new ObjectId(),false, plat._id, true);
        else
            p = new Preparation(new ObjectId(),false, plat._id, false);

        if(table.getCommande() == null){
            Order ord = new Order(new ObjectId(), plat.prix);
            getDbQueries().newPreparation(p);
            ord.getPreparation().add(p._id);
            getDbQueries().newOrder(ord);
            table.setCommande(ord.get_id());
            getDbQueries().addOrderToTable(table, ord.get_id());
            updateStockIngredient(plat);
        }
        else{
            getDbQueries().newPreparation(p);
            Order ord = getDbQueries().getOrderFromTable(table.getCommande());
            ord.getPreparation().add(p._id);
            ord.setMontant(ord.getMontant().doubleValue() + plat.prix);
            getDbQueries().updateOrder(ord);
            updateStockIngredient(plat);
        }
    }

    public void  updateStockIngredient(Cook.Plat plat){
        for (String ing : plat.Ingredient) {
            getDbQueries().updateIngredient(getDbQueries().getIngredient(ing).getObjectId("_id"),
                    getDbQueries().getIngredient(ing).getInteger("stock").intValue()-1);
        }
    }


}


