package fr.ul.miage;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import org.bson.types.ObjectId;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;

public class Director extends Staff {

    public Director(String login, String mdp, String nom, String prenom) {
        super(login, mdp, nom, prenom);
    }

    boolean isWaiter = false;

    @Override
    public void screen() throws IOException {
        setupWindowAndSwitch(AffichageMenu(),"",1);
    }

    /**
     * Permet de mettre en place la fenêtre et de switcher cette fenêtre dans le terminal principal.
     * @param panel
     */
    private void setupWindowAndSwitch(Panel panel, String menuName, int nbColonne){
        panel.setLayoutManager(new GridLayout(nbColonne));
        panel.addComponent(new EmptySpace(new TerminalSize(0, 0)));
        panel.addComponent(new Label(menuName));
        BasicWindow window = new BasicWindow();
        window.setComponent(panel);
        try {
            MainTerminal.getConsole().switchWindow(window);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Permet l'affichage du menu du directeur qui comporte de par exemple pouvoir gérer les employés
     * @return
     */
    public Panel AffichageMenu() {
        Panel panel = super.deconnection();
        panel.setLayoutManager(new GridLayout(1));
        panel.addComponent(new EmptySpace());
        panel.addComponent(new Label("Menu du directeur").addStyle(SGR.BOLD));
        panel.addComponent(new EmptySpace());
        new Button("Gérer les employés", new Runnable() { //Affiche un bouton qui redirige vers la fenêtre pour gérer les employés.
            @Override
            public void run() {
                setupWindowAndSwitch(GererEmployes(new Label("")),"",1);
            }
        }).addTo(panel);
        buttonManageIngredients().addTo(panel);
        buttonMenuJour().addTo(panel);
        buttonRotationMoyen().addTo(panel);
        buttonPreparationMoyen().addTo(panel);
        buttonCADejDiner().addTo(panel);
        buttonPartRecette().addTo(panel);
        buttonAnalyseVente().addTo(panel);
        setupWindowAndSwitch(panel,"",1);
        return panel;
    }

    /**
     * Permet l'affichage du menu pour gérer les employés : en créer des nouveaux, les modifiers ou encore leurs suivis.
     * @param message
     * @return
     */
    public Panel GererEmployes(Label message) {
        Panel panel = super.deconnection();
        new Button("Retour en arrière", new Runnable() { //Affiche un bouton qui permet de revenir en arrière donc sur le menu
            @Override
            public void run() {
                setupWindowAndSwitch(AffichageMenu(),"",1);
            }
        }).addTo(panel);
        panel.addComponent(new Label("Menu Employés").addStyle(SGR.BOLD));
        panel.addComponent(message.setBackgroundColor(TextColor.ANSI.GREEN));  //Affiche un message de confirmation (création)
        panel.addComponent(new EmptySpace());
        panel.setLayoutManager(new GridLayout(1));
        //Label lblOutput = lbl;
        new Button("Consulter les employés", new Runnable() { //Affiche un bouton qui redirige vers la fenêtre pour gérer les employés.
            @Override
            public void run() {
                setupWindowAndSwitch(ConsulterEmployes(),"",1);
            }
        }).addTo(panel);
        new Button("Ajouter un nouvel employé", new Runnable() { //Affiche un bouton qui redirige vers la fenêtre pour ajouter un employé
            @Override
            public void run() {
                setupWindowAndSwitch(AjouterEmploye("","","","",0,new ArrayList<>(), getDbQueries().getAllTable(), isWaiter),"",2);
            }
        }).addTo(panel);
        new Button("Modifier les employés", new Runnable() { //Affiche un bouton qui redirige vers la fenêtre pour ajouter un employé
            @Override
            public void run() {
                setupWindowAndSwitch(ListeModifierEmploye(),"",2);
            }
        }).addTo(panel);
        new Button("Suivre les employés", new Runnable() { //Affiche un bouton qui redirige vers la fenêtre pour ajouter un employé
            @Override
            public void run() {
                setupWindowAndSwitch(ListeSuiviEmploye(),"",2);
            }
        }).addTo(panel);
        setupWindowAndSwitch(panel,"",1);
        return panel;
    }

    /**
     * Affiche la liste des employés pour ensuite les
     * @return
     */
    public Panel ListeModifierEmploye(){
        Panel panel = super.deconnection();
        panel.addComponent(new EmptySpace());
        new Button("Retour en arrière", new Runnable() { //Affiche un bouton qui permet de revenir en arrière donc sur le menu
            @Override
            public void run() {
                setupWindowAndSwitch(GererEmployes(new Label("")),"",1);
            }
        }).addTo(panel);
        panel.addComponent(new EmptySpace());
        panel.setLayoutManager(new GridLayout(2));
        panel.addComponent(new Label("Liste employés : ").addStyle(SGR.BOLD));
        panel.addComponent(new EmptySpace());
        List<Staff> employe = getDbQueries().getAllStaff();
        for(Staff e : employe){
            panel.addComponent(new Label("   - " + e.getNom() + " " + e.getPrenom()));
            new Button("Modifier", new Runnable() { //Affiche un bouton qui redirige vers la fenêtre pour voir les détails des employés
                @Override
                public void run() {
                    List<Table> table = new ArrayList<>();
                    String role = "";
                    if(e instanceof Waiter){ //Si c'est un serveur
                        role = "serveur";
                        isWaiter = true; //On met le booléen a vrai
                        Waiter a = (Waiter) e;
                        if(a != null){
                            table = getDbQueries().getTableId(a.getTable()); //et on récupère ses tables
                        }
                    }
                    if(e instanceof Cook){ //Si ce n'est pas un serveur, on met le booléen a faux et on instancie le role
                        isWaiter = false; role="cuisinier";} else if(e instanceof ServiceAssistant){isWaiter = false; role="assistant de service";} else if(e instanceof Butler){isWaiter = false; role="maitre d'hotel";} else if(e instanceof Director){isWaiter = false; role="directeur";}

                    setupWindowAndSwitch(ModifierEmploye(e.getId(),e.getLogin(), e.getMdp(), e.getNom(), e.getPrenom(), role, table, getDbQueries().getAllTable()),"",1);
                }
            }).addTo(panel);
        }
        setupWindowAndSwitch(panel,"",2);
        return panel;
    }

    /**
     * Affiche la liste des employés pour ensuite les
     * @return
     */
    public Panel ModifierEmploye(ObjectId id, String log, String m, String n, String p, String r, List<Table> tablesA, List<Table> tableAll){
        Panel panel = super.deconnection();
        panel.addComponent(new EmptySpace());
        new Button("Retour en arrière", new Runnable() { //Affiche un bouton qui permet de revenir en arrière donc sur le menu
            @Override
            public void run() {
                setupWindowAndSwitch(ListeModifierEmploye(),"",1);
            }
        }).addTo(panel);
        ComboBox<String> roles = new ComboBox<String>();
        roles.addItem("cuisinier").addItem("serveur").addItem("assistant de service").addItem("maitre d'hotel").addItem("directeur"); //On définit les roles possibles

        panel.addComponent(new EmptySpace());
        panel.addComponent(new Label("Modification de l'employé").addStyle(SGR.BOLD));
        panel.addComponent(new EmptySpace());
        panel.addComponent(new Label("Login de l'employé:"));
        final TextBox login = new TextBox().setText(log).addTo(panel); // Contient le login de l'employé

        panel.addComponent(new Label("Mot de passe de l'employé:"));
        final TextBox mdp = new TextBox().setText(m).addTo(panel); // Contient le mot de passe de l'employé

        panel.addComponent(new Label("Nom de l'employé:"));
        final TextBox nom = new TextBox().setText(n).setValidationPattern(Pattern.compile("[A-Za-z ]*")).addTo(panel); // Contient le nom de l'employé

        panel.addComponent(new Label("Prénom de l'employé:"));
        final TextBox prenom = new TextBox().setText(p).setValidationPattern(Pattern.compile("[A-Za-z ]*")).addTo(panel); // Contient le prénom de l'employé

        panel.addComponent(new Label("Rôle de l'employé :"));
        ComboBox<String> role = roles; // Permet de sélectionner le rôle de l'employé
        role.setSelectedItem(r);
        panel.addComponent(role);
        role.addListener(new ComboBox.Listener() { //Ajoute un Listener à la combobox qui permet d'éxécuter une méthode a chaque fois qu'une valeur est sélectionnée dans la combobox
            @Override
            public void onSelectionChanged(int selectedIndex, int previousSelection, boolean changedByUserInteraction) {
                Object selected = role.getSelectedItem(); //On récupère le role sélectionné
                if(selected.equals("serveur")){ //Si c'est le serveur, on rappelle la fenêtre avec le booleen IsWaiter a vrai
                    isWaiter = true;
                    setupWindowAndSwitch(ModifierEmploye(id,login.getText(), mdp.getText(), nom.getText(), prenom.getText(), role.getSelectedItem(), tablesA, tableAll ),"",2);
                }
                else{ //Si ce n'est pas le serveur, on rappelle la fenêtre avec le booleen IsWaiter a faux
                    isWaiter = false;
                    setupWindowAndSwitch(ModifierEmploye(id,login.getText(), mdp.getText(), nom.getText(), prenom.getText(), role.getSelectedItem(), tablesA, tableAll),"",2);
                }
            }
        });
        if(isWaiter){ //Si isWaiter est a vrai alors on affiche le menu pour les tables
            affichageTableServeurModification(panel,id, tablesA, tableAll, login.getText(), mdp.getText(), nom.getText(), prenom.getText(), role.getSelectedItem());
        }
        panel.addComponent(new EmptySpace());
        new Button("Modifier", new Runnable() { //Affiche un bouton qui permet de modifier l'employé
            @Override
            public void run() {
                getDbQueries().modificationEmploye(id,login.getText(), mdp.getText(), nom.getText(), prenom.getText(), role.getSelectedItem(), tablesA); //modifie l'employé dans le bdd
                setupWindowAndSwitch(GererEmployes(new Label("L'employé "+ nom.getText() + " " + prenom.getText() + " a bien été modifié")),"",1); //Renvoie vers la fenêtre de gestion des employés avec un message de confirmation
            }
        }).addTo(panel);
        setupWindowAndSwitch(panel,"",2);
        return panel;
    }

    /**
     * Permet d'afficher les tables affectées ou non de l'employé pour la modification
     * @param panel
     * @param tableAffectes
     * @param tables
     * @param login
     * @param mdp
     * @param nom
     * @param prenom
     * @param role
     */
    public void affichageTableServeurModification(Panel panel, ObjectId id, List<Table> tableAffectes, List<Table> tables, String login, String mdp, String nom, String prenom, String role){
        panel.addComponent(new Label("Tables affectées :"));
        panel.addComponent(new EmptySpace());
        if(tableAffectes != null){ //S'il existe des tables affectées
            for(Table e : tableAffectes){
                panel.addComponent(new Label("  - Table n°"+e.getNumero()+" étage :" +e.getEtage())); //On les ajoute
                new Button("Retirer la table" , new Runnable() {
                    @Override
                    public void run() {
                        tableAffectes.remove(e);
                        setupWindowAndSwitch(ModifierEmploye(id,login, mdp, nom, prenom, role, tableAffectes, tables),"",2);
                    }
                }).addTo(panel);
            }
        }
        panel.addComponent(new Label("Tables non affectées :"));
        panel.addComponent(new EmptySpace());
        if(tables != null){ //S'il existe des tables non affectées
            for(Table e : tables){
                boolean existe = false; //On dit qu'il n'existe pas de base dans les tables affectés
                for(Table affecte : tableAffectes){ //On parcoure les tables affectés
                    if(affecte.get_id().equals(e.get_id())){ //On regarde si la table existe dans les tables affectés
                        existe = true; //Si oui alors on met le booléen a vrai
                    }
                }
                if(!existe){ //Si le booléen est a faux donc que la table n'existe pas
                    panel.addComponent(new Label("  - Table n°"+e.getNumero()+" étage :" +e.getEtage())); //On l'affiche
                    new Button("Affecter la table" , new Runnable() { //Bouton qui va permettre d'affecter une table
                        @Override
                        public void run() {
                            tableAffectes.add(e); //va rajouter dans la liste de table affectés la table sélectionnée
                            setupWindowAndSwitch(ModifierEmploye(id,login, mdp, nom, prenom, role, tableAffectes, tables),"",2);
                        }
                    }).addTo(panel);
                }

            }
        }
    }


    /**
     * Permet de suivre le type d'employés que l'on souhaite
     * @return
     */
    public Panel ListeSuiviEmploye(){
        Panel panel = super.deconnection();
        new Button("Retour en arrière", new Runnable() { //Affiche un bouton qui permet de revenir en arrière donc sur le menu
            @Override
            public void run() {
                setupWindowAndSwitch(GererEmployes(new Label("")),"",1);
            }
        }).addTo(panel);
        panel.setLayoutManager(new GridLayout(1));
        panel.addComponent(new EmptySpace());
        new Button("Suivre les serveurs", new Runnable() { //Affiche un bouton qui redirige vers la fenêtre pour ajouter un employé
            @Override
            public void run() {
                setupWindowAndSwitch(suiviEmploye("serveur"),"",2);
            }
        }).addTo(panel);
        new Button("Suivre les cuisiniers", new Runnable() { //Affiche un bouton qui redirige vers la fenêtre pour ajouter un employé
            @Override
            public void run() {
                setupWindowAndSwitch(suiviEmploye("cuisinier"),"",2);
            }
        }).addTo(panel);
        new Button("Suivre les assistants de service", new Runnable() { //Affiche un bouton qui redirige vers la fenêtre pour ajouter un employé
            @Override
            public void run() {
                setupWindowAndSwitch(suiviEmploye("assistant de service"),"",2);
            }
        }).addTo(panel);
        setupWindowAndSwitch(panel,"",1);
        return panel;
    }

    /**
     * Va afficher les informations de suivi pour un role donné
     * @param role
     * @return
     */
    public Panel suiviEmploye(String role){
        Panel panel = super.deconnection();
        new Button("Retour en arrière", new Runnable() { //Affiche un bouton qui permet de revenir en arrière donc sur le menu
            @Override
            public void run() {
                setupWindowAndSwitch(ListeSuiviEmploye(),"",1);
            }
        }).addTo(panel);
        List<Staff> staff = getDbQueries().getAllStaff();
        panel.addComponent(new Label("Liste des " + role + "s : "));
        for(Staff s : staff){
            switch (role){ //Permet d'afficher l'interface en fonction du rôle
                case "serveur": //Si c'est un rôle serveur
                    if(s instanceof Waiter){ //Et une instance de Waiter
                        panel.addComponent(new Label("  - " +s.getNom() + " " + s.getPrenom()+" : ")); //On affiche les serveurs et leurs tables
                        List<Table> table =getDbQueries().getTableId(((Waiter) s).getTable());
                        for(Table t : table){
                            panel.addComponent(new Label("\t - Table n°"+t.getNumero()+", étage n°"+t.getEtage()+", état : " +t.getEtat()));
                        }
                    }
                    break;
                case "assistant de service": // Si c'est un rôle assistant de service
                    if(s instanceof ServiceAssistant){ // Et une instance de ServiceAssistant
                        panel.addComponent(new Label("  - " +s.getNom() + " " + s.getPrenom()));//On affiche les assistants de service et les tables à débarasser
                    }
                    break;
                case "cuisinier":// Si c'est un rôle cuisinier
                    if(s instanceof Cook){ // Et une instance de Cook
                        panel.addComponent(new Label("  - " +s.getNom() + " " + s.getPrenom())); //On affiche les cuisiniers et les plats en train d'être préparer
                    }
                    break;
            }
        }
        if(role.equals("assistant de service")){
            List<Table> table =getDbQueries().getAllTable(); //Les assistants de service ont tous les mêmes tables à débarasser
            panel.addComponent(new Label("Tables à débarasser : "));
            for(Table t : table){
                if(t.getEtat().equals("débarassée")){
                    panel.addComponent(new Label("\t - Table n°"+t.getNumero()+", étage n°"+t.getEtage()+", état : " +t.getEtat()));
                }
            }
        }
        if(role.equals("cuisinier")){
            List<Preparation> prepa = getDbQueries().getPreparationsEnCours(); //Les cuisiniers travaillent en équipe sur un même plat.
            panel.addComponent(new Label("Plats en préparation : "));
            for(Preparation p : prepa){
                if(p.fin != null){
                    Cook.Plat plat = getDbQueries().getPlat(p.Plat);
                    panel.addComponent(new Label("\t - " + plat.nom + " commencé le " + p.heureCommande));
                }
            }
        }
        panel.setLayoutManager(new GridLayout(1));
        setupWindowAndSwitch(panel,"",1);
        return panel;
    }

    /**
     * Affiche la liste de tous les employés avec la possibilité de voir en détails leurs informations
     * @return
     */
    public Panel ConsulterEmployes(){
        Panel panel = super.deconnection();
        panel.addComponent(new EmptySpace());
        new Button("Retour en arrière", new Runnable() { //Affiche un bouton qui permet de revenir en arrière donc sur le menu
            @Override
            public void run() {
                setupWindowAndSwitch(GererEmployes(new Label("")),"",1);
            }
        }).addTo(panel);
        panel.addComponent(new EmptySpace());
        panel.setLayoutManager(new GridLayout(2));
        panel.addComponent(new Label("Liste employés : ").addStyle(SGR.BOLD));
        panel.addComponent(new EmptySpace());
        List<Staff> employe = getDbQueries().getAllStaff();
        for(Staff e : employe){
            panel.addComponent(new Label("   - " + e.getNom() + " " + e.getPrenom()));
            new Button("Détails", new Runnable() { //Affiche un bouton qui redirige vers la fenêtre pour voir les détails des employés
                @Override
                public void run() {
                    setupWindowAndSwitch(DetailsEmployes(e),"",1);
                }
            }).addTo(panel);
        }
        setupWindowAndSwitch(panel,"",2);
        return panel;
    }

    /**
     * Permet d'afficher en détails les informations de l'employé + les tables si c'est un serveur
     * @param staff
     * @return
     */
    public Panel DetailsEmployes(Object staff){
        Panel panel = super.deconnection();
        new Button("Retour en arrière", new Runnable() { //Affiche un bouton qui permet de revenir en arrière donc sur le menu
            @Override
            public void run() {
                setupWindowAndSwitch(ConsulterEmployes(),"",1);
            }
        }).addTo(panel);
        Staff employe = (Staff) staff;
        panel.setLayoutManager(new GridLayout(1));
        panel.addComponent(new EmptySpace());
        panel.addComponent(new Label("Nom : " + employe.getNom())); //Affichage des informations de l'employés
        panel.addComponent(new Label("Prenom : " + employe.getPrenom()));
        panel.addComponent(new Label("Login : " + employe.getLogin()));
        panel.addComponent(new Label("Mot de passe : " + employe.getMdp()));
        if(staff instanceof Waiter){
            panel.addComponent(new Label("Liste des tables affectés : "));
            List<Table> tablesAll = getDbQueries().getAllTable();
            List<Table> tables = new ArrayList<>();
            Waiter a = (Waiter) staff;
            for(Table t: tablesAll){
                if(a.getTable().contains(t.get_id())){
                    panel.addComponent(new Label("  - Table n°"+t.getNumero() + ", étage : " + t.getEtage()));
                }
            }
        }
        setupWindowAndSwitch(panel,"",1);
        return panel;
    }

    /**
     * Permet d'ajouter un employé
     * @param log
     * @param m
     * @param n
     * @param p
     * @param r
     * @param tablesA
     * @param tableAll
     * @param isWaiter
     * @return
     */
    public Panel AjouterEmploye(String log, String m, String n, String p, int r, List<Table> tablesA, List<Table> tableAll, boolean isWaiter) {
        Panel panel = super.deconnection();
        panel.setLayoutManager(new GridLayout(2));
        panel.addComponent(new EmptySpace());
        new Button("Retour en arrière", new Runnable() { //Affiche un bouton qui permet de revenir en arrière donc sur le menu
            @Override
            public void run() {
                setupWindowAndSwitch(GererEmployes(new Label("")),"",1);
            }
        }).addTo(panel);
        panel.addComponent(new EmptySpace());
        panel.addComponent(new EmptySpace());

        List<Table> tables = tableAll; // Une liste qui contient toutes les tables non affectées
        List<Table> tableAffectes = tablesA; // Une liste qui contient toutes les tables affectées
        ComboBox<String> roles = new ComboBox<String>();
        roles.addItem("cuisinier").addItem("serveur").addItem("assistant de service").addItem("maitre d'hotel").addItem("directeur"); //On définit les roles possibles

        panel.addComponent(new EmptySpace());
        panel.addComponent(new Label("Ajout d'un nouvel employé").addStyle(SGR.BOLD));
        panel.addComponent(new EmptySpace());

        panel.addComponent(new Label("Login de l'employé:"));
        final TextBox login = new TextBox().setText(log).addTo(panel); // Contient le login de l'employé

        panel.addComponent(new Label("Mot de passe de l'employé:"));
        final TextBox mdp = new TextBox().setText(m).addTo(panel); // Contient le mot de passe de l'employé

        panel.addComponent(new Label("Nom de l'employé:"));
        final TextBox nom = new TextBox().setText(n).setValidationPattern(Pattern.compile("[A-Za-z ]*")).addTo(panel); // Contient le nom de l'employé

        panel.addComponent(new Label("Prénom de l'employé:"));
        final TextBox prenom = new TextBox().setText(p).setValidationPattern(Pattern.compile("[A-Za-z ]*")).addTo(panel); // Contient le prénom de l'employé

        panel.addComponent(new Label("Rôle de l'employé :"));
        ComboBox<String> role = roles; // Permet de sélectionner le rôle de l'employé
        role.setSelectedIndex(r);
        panel.addComponent(role);
        role.addListener(new ComboBox.Listener() { //Ajoute un Listener à la combobox qui permet d'éxécuter une méthode a chaque fois qu'une valeur est sélectionnée dans la combobox
            @Override
            public void onSelectionChanged(int selectedIndex, int previousSelection, boolean changedByUserInteraction) {
                Object selected = role.getSelectedItem(); //On récupère le role sélectionné
                if(selected.equals("serveur")){ //Si c'est le serveur, on rappelle la fenêtre avec le booleen IsWaiter a vrai
                    setupWindowAndSwitch(AjouterEmploye(login.getText(), mdp.getText(), nom.getText(), prenom.getText(), role.getSelectedIndex(),tableAffectes, tableAll, true),"",2);
                }
                else{ //Si ce n'est pas le serveur, on rappelle la fenêtre avec le booleen IsWaiter a faux
                    setupWindowAndSwitch(AjouterEmploye(login.getText(), mdp.getText(), nom.getText(), prenom.getText(), role.getSelectedIndex(),tableAffectes, tableAll, false),"",2);
                }
            }
        });
        if(isWaiter){ //Si isWaiter est a vrai alors on affiche le menu pour les tables
            affichageTableServeur(panel, tableAffectes, tables, login.getText(), mdp.getText(), nom.getText(), prenom.getText(), role.getSelectedIndex());
        }
        panel.addComponent(new EmptySpace());
        new Button("Créer le nouvel employé" , new Runnable() { //Bouton pour créer un nouvel employé
            @Override
            public void run() {
                getDbQueries().addStaff(login.getText(), mdp.getText(), nom.getText(), prenom.getText(), role.getSelectedItem(), tableAffectes); //Appelle a la requête qui va insérer le nouvel employé dans la BDD
                setupWindowAndSwitch((GererEmployes(new Label("Le nouvel employé " + nom.getText() + " " + prenom.getText() +" a bien été créer"))),"",2);
            }
        }).addTo(panel);
        setupWindowAndSwitch(panel,"",2);

        return panel;
    }

    /**
     * Permet d'afficher les tables affectées ou non au nouvel employé
     * @param panel
     * @param tableAffectes
     * @param tables
     * @param login
     * @param mdp
     * @param nom
     * @param prenom
     * @param role
     */
    public void affichageTableServeur(Panel panel, List<Table> tableAffectes, List<Table> tables, String login, String mdp, String nom, String prenom, int role){
        panel.addComponent(new Label("Tables affectées :"));
        if(tableAffectes != null){ //S'il existe des tables affectées
            for(Table e : tableAffectes){
                panel.addComponent(new EmptySpace());
                panel.addComponent(new Label("  - Table n°"+e.getNumero()+" étage :" +e.getEtage())); //On les ajoute
            }
        }
        panel.addComponent(new EmptySpace());
        panel.addComponent(new Label("Tables non affectées :"));
        panel.addComponent(new EmptySpace());
        if(tables != null){ //S'il existe des tables non affectées
            for(Table e : tables){
                if (!tableAffectes.contains(e)) { //On vérifie que cette table n'existe pas dans les tables affectées a l'employé
                    panel.addComponent(new Label("  - Table n°"+e.getNumero()+" étage :" +e.getEtage()));
                    new Button("Affecter la table" , new Runnable() {
                        @Override
                        public void run() {
                            tableAffectes.add(e);
                            setupWindowAndSwitch(AjouterEmploye(login, mdp, nom, prenom, role, tableAffectes, tables, true),"",2);
                        }
                    }).addTo(panel);
                }

            }
        }
    }

    private Button buttonManageIngredients(){
        return new Button("Gérer les ingrédients", new Runnable() {
            @Override
            public void run() {
                setupWindowAndSwitch(panelManageIngredient(),"Gérer les ingrédients",3);
            }
        });
    }

    private Button mainMenu(){
        return new Button("Retour au menu", new Runnable() {
            @Override
            public void run() {
                setupWindowAndSwitch(AffichageMenu(),"",1);
            }
        });
    }

    private Panel panelManageIngredient(){
        Panel panel = new Panel();
        mainMenu().addTo(panel);
        panel.addComponent(new EmptySpace());
        panel.addComponent(new EmptySpace());

        ArrayList<Ingredient> ingredients = getDbQueries().getIngredients();
        for (Ingredient i : ingredients){
            panel.addComponent(new Label(i.nom + " : " + i.stock));
            TextBox quantity = new TextBox().setValidationPattern(Pattern.compile("[0-9]*")).addTo(panel);
            quantity.addTo(panel);
            changeQuantity(i._id,quantity).addTo(panel);
        }
        return panel;
    }

    private Button changeQuantity(ObjectId id, TextBox quantity){
        return new Button("Modifier", new Runnable() {
            @Override
            public void run() {
                if(quantity.getText().equals("")){ // Si l'input est vide on affiche un message d'indication.
                    setupWindowAndSwitch(panelManageIngredient(),"Merci d'entrer une quantité",3);
                    return;
                }
                int q = Integer.parseInt(quantity.getText());
                getDbQueries().updateIngredient(id,q);
                setupWindowAndSwitch(panelManageIngredient(),"Modification effectuée !",3);
            }
        });
    }

    private Button buttonMenuJour(){
        return new Button("Voir la carte du jour", new Runnable() {
            @Override
            public void run() {
                setupWindowAndSwitch(panelMenuDujour(),"",1);
            }
        });
    }

    private Button buttonModifierMenuJour(){
        return new Button("Modifier la carte du jour", new Runnable() {
            @Override
            public void run() {
                setupWindowAndSwitch(panelModifierMenuDujour(),"Modification de la carte du jour",3);
            }
        });
    }

    private Panel panelMenuDujour(){
        Panel panel = new Panel();
        mainMenu().addTo(panel);
        buttonModifierMenuJour().addTo(panel);
        panel.addComponent(new Label("Composition du menu du jour :"));

        ArrayList<Cook.Plat> plats  = getDbQueries().getAllPlats();
        String message = "Le menu est vide";
        for (Cook.Plat p : plats){
            if(p.platDuJour){
                message = "";
                panel.addComponent(new Label(p.nom));
            }
        }
        panel.addComponent(new Label(message));
        return panel;
    }

    private Panel panelModifierMenuDujour(){
        Panel panel = new Panel();
        mainMenu().addTo(panel);
        panel.addComponent(new EmptySpace());
        panel.addComponent(new EmptySpace());

        ArrayList<Cook.Plat> plats  = getDbQueries().getAllPlats();
        for (Cook.Plat p : plats){
            panel.addComponent(new Label(p.nom));
            panel.addComponent(new Label(p.platDuJour ? "Dans le menu" : "Pas dans le menu"));
            buttonTogglePlatDuJour(p).addTo(panel);
        }
        return panel;
    }

    private Button buttonTogglePlatDuJour(Cook.Plat plat){
        return new Button("Modifier la carte du jour", new Runnable() {
            @Override
            public void run() {
                getDbQueries().modifierPlat(plat, !plat.platDuJour);
                setupWindowAndSwitch(panelModifierMenuDujour(),"Modifier",3);
            }
        });
    }

    /**
     * Permet d'afficher un boutton qui va rediriger vers le panel de rotation moyen
     * @return
     */
    private Button buttonRotationMoyen(){
        return new Button("Voir le temps de rotation moyen des clients", new Runnable() {
            @Override
            public void run() {
                try {
                    setupWindowAndSwitch(panelRotationMoyen(),"",1);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Affiche un panel qui va calculer le temps de rotation moyen
     * @return
     * @throws ParseException
     * @throws ParseException
     */
    private Panel panelRotationMoyen() throws ParseException, ParseException {
        Panel panel = new Panel();
        mainMenu().addTo(panel); //Afiche le boutton de retour au menu

        Long temps = null;
        int tempsMoyen = 0; //Stocke le temps moyen
        int compteur = 0; //Compte le nombre de commandes
        SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<Order> commandes = getDbQueries().getAllCommandes();
        for(Order c : commandes){
            if(c.getDateFin()!= null && !c.getDateFin().equals("")){ //Si la commande est bien terminée
                temps = SDF.parse(c.getDateFin()).getTime()-(SDF.parse(c.getDateDebut())).getTime(); //On parse en date et on récupère la différence des 2 dates
                tempsMoyen += temps; //On ajoute au temps moyen le temps calculé avant
                compteur++; //On incrémente le compeur
            }

        }
        tempsMoyen = tempsMoyen/compteur; //Pour avoir le temps moyen, on prend le temp moyen qu'on divise par le compteur qui représente le nb de commande
        panel.addComponent(new Label("Le temps de rotation moyen des clients est de :"));
        panel.addComponent(new Label((tempsMoyen%86400000)/3600000+" heures, "+((tempsMoyen%86400000)%3600000)/60000+" minutes et "+(((tempsMoyen%86400000)%3600000)%60000)/1000+" secondes")); //On effectue un calcul pour avoir précisement le temps moyen
        return panel;
    }

    /**
     * Permet d'afficher un boutton qui va rediriger vers le panel de préparation moyen
     * @return
     */
    private Button buttonPreparationMoyen(){
        return new Button("Voir le temps de préparation moyen des cuisiniers", new Runnable() {
            @Override
            public void run() {
                try {
                    setupWindowAndSwitch(panelPreparationMoyen(),"",1);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Permet d'afficher le temps de préparation moyen
     * @return
     * @throws ParseException
     * @throws ParseException
     */
    private Panel panelPreparationMoyen() throws ParseException, ParseException {
        Panel panel = new Panel();
        mainMenu().addTo(panel);

        Long temps = null;
        int tempsMoyen = 0; //Va stocker le temps moyen
        int compteur = 0; //Va compter le nombre de préparation
        SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<Preparation> preparations = getDbQueries().getPreparationsEnCours(); //Récupère toutes les préparations qui ont au moin commencé à étre préparé
        for(Preparation c : preparations){ //On parcoure les préparations
            if(c.fin!= null && !c.fin.equals("")){ //Si la preparation est finie
                temps = SDF.parse(c.fin).getTime()-(SDF.parse(c.heureCommande)).getTime(); //On parse la date et on stocke la différence entre les 2 dates
                tempsMoyen += temps;
                compteur++; //On incrémente le compteur
            }

        }
        tempsMoyen = tempsMoyen/compteur; //On calcul le temps moyen en reprenant le temps moyen et en le divisant par le nombre de preparation
        panel.addComponent(new Label("Le temps de préparation moyen des cuisiniers est de :"));
        panel.addComponent(new Label((tempsMoyen%86400000)/3600000+" heures, "+((tempsMoyen%86400000)%3600000)/60000+" minutes et "+(((tempsMoyen%86400000)%3600000)%60000)/1000+" secondes"));
        return panel;
    }

    /**
     * Permet d'afficher un boutton qui va rediriger vers le panel CA du déjeuner et du diner
     * @return
     */
    private Button buttonCADejDiner(){
        return new Button("Voir le CA du déjeuner et du diner", new Runnable() {
            @Override
            public void run() {
                try {
                    setupWindowAndSwitch(panelCADejDiner(),"",1);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Permet d'afficher le CA du déjeuner et du diner
     * @return
     * @throws ParseException
     */
    private Panel panelCADejDiner() throws ParseException {
        Panel panel = new Panel();
        mainMenu().addTo(panel);

        Date dateDejeunerDebut = new SimpleDateFormat("HH:mm:ss").parse("12:00:00"); //On définit les dates du déjeuner
        Date dateDejeunerFin = new SimpleDateFormat("HH:mm:ss").parse("14:00:00");
        Date dateDinerDebut = new SimpleDateFormat("HH:mm:ss").parse("18:00:00"); // et du diner
        Date dateDinerFin = new SimpleDateFormat("HH:mm:ss").parse("20:00:00");
        int CAdej = 0;
        int CAdiner = 0;
        SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<Order> commandes = getDbQueries().getAllCommandes();

        for(Order c : commandes){
            if(c.getDateFin()!= null && !c.getDateFin().equals("")){ //Si la commande est bien terminée
                Date debut = SDF.parse(c.getDateDebut()); //On parse les dates
                Date fin = SDF.parse(c.getDateFin());
                if(debut.getHours() >= dateDejeunerDebut.getHours() && fin.getHours() <= dateDejeunerFin.getHours()){ //On vérifie si la date est comprise entre la date du déjeuner de debut et fin
                    CAdej += c.getMontant();
                }
                if(debut.getHours() >= dateDinerDebut.getHours() && fin.getHours() <= dateDinerFin.getHours()){ //On vérifie si la date est comprise entre la date du diner de début et fin
                    CAdiner += c.getMontant();
                }
            }

        }
        panel.addComponent(new Label("Le CA du déjeuner (12h - 15h) est de "+ CAdej + " euros"));
        panel.addComponent(new Label("Le CA du diner (18h - 21h) est de " + CAdiner + " euros"));
        return panel;
    }

    /**
     * Permet d'afficher un boutton qui va rediriger vers le panel de la part recette
     * @return
     */
    private Button buttonPartRecette(){
        return new Button("Consulter les parts de recette de chaque plat", new Runnable() {
            @Override
            public void run() {
                try {
                    setupWindowAndSwitch(panelPartRecette(),"",1);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Affiche un panel qui va permettre d'afficher les pars de recette générés par plats
     * @return
     * @throws ParseException
     */
    private Panel panelPartRecette() throws ParseException {
        Panel panel = new Panel();
        mainMenu().addTo(panel);
        panel.addComponent(new Label("Parts de recette générées par plats"));
        Map<String, Double> produitPrix = new HashMap<>(); //Créer une map pour stocker les plats et leurs montant
        List<Order> commandes = getDbQueries().getAllCommandes(); //On récupère toutes les commandes

        for(Order c : commandes){
            if(c.getDateFin()!= null && !c.getDateFin().equals("")){ //On vérifie que la commande est bien termintée
                for(Preparation p : getDbQueries().getPreparationID(c.getPreparation())){  //On va récupérer une liste de préparation à partir d'une liste d'object id et on boucle dessus
                    if(p.debut && p.fin != null){ //Si la preparation est bien terminée
                        Cook.Plat plat = getDbQueries().getPlat(p.Plat); //On récupère un object plat avec l'objectId
                        if(produitPrix.containsKey(plat.nom)){ //On regarde si le nom du plat existe déjà dans le hashmap
                            double montant = produitPrix.get(plat.nom) + plat.prix; //Si oui on récupère le montant et on ajoute le prix du plat
                            produitPrix.put(plat.nom, montant); //On met a jour dans le hashmap
                        }
                        else{ //Si non
                            produitPrix.put(plat.nom, plat.prix); //On ajoute dans le hashmap
                        }
                    }
                }

            }
        }
        for(Map.Entry entry : produitPrix.entrySet()){ //On parcoure le hashmap
            panel.addComponent(new Label("   - " +entry.getKey() + " a générer " + entry.getValue() + " €"));
        }
        return panel;
    }

    /**
     * Permet d'afficher un boutton qui va rediriger vers le panel de l'analyse de vente
     * @return
     */
    private Button buttonAnalyseVente(){
        return new Button("Analyse des ventes", new Runnable() {
            @Override
            public void run() {
                setupWindowAndSwitch(panelRecette(),"",1);
            }
        });
    }

    /**
     * Affiche un panel qui va permettre d'afficher des boutons pour consulter la recette quotidienne, hebdomadaire et mensuelle.
     * @return
     */
    private Panel panelRecette()  {
        Panel panel = new Panel();
        mainMenu().addTo(panel);
        new Button("Recette quotidienne", new Runnable() {
            @Override
            public void run() {
                try {
                    setupWindowAndSwitch(panelRecetteQuotidienne(),"",1);
                } catch (ParseException e) {

                }
            }
        }).addTo(panel);
        new Button("Recette hebdomadaire", new Runnable() {
            @Override
            public void run() {
                try {
                    setupWindowAndSwitch(panelRecetteHebdomadaire(),"",1);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }).addTo(panel);
        new Button("Recette mensuelle", new Runnable() {
            @Override
            public void run() {
                try {
                    setupWindowAndSwitch(panelRecetteMensuelle(),"",1);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }).addTo(panel);
        return panel;
    }

    /**
     * Permet d'afficher la recette quotidienne
     * @return
     * @throws ParseException
     */
    private Panel panelRecetteQuotidienne() throws ParseException {
        Panel panel = new Panel();
        mainMenu().addTo(panel);
        panel.addComponent(new Label("Recette quotidienne : "));
        Map<String, Double> recettes = new HashMap<>(); //Initialisation du hashmap pour stocker la date et le montant
        List<Order> commandes = getDbQueries().getAllCommandes(); //On récupère toutes les commandes

        for(Order c : commandes) {
            if (c.getDateFin() != null && !c.getDateFin().equals("")) { //On vérifie que la commande est bien terminée.
                String debut = c.getDateDebut().split(" ")[0]; //On split sur la date pour récupérer juste la date sans le temps.
                if(recettes.containsKey(debut)){ //Si la date est contenue dans le hashmap
                    double montant = c.getMontant() + recettes.get(debut);
                    recettes.put(debut, montant);
                }
                else{
                    recettes.put(debut, c.getMontant());
                }
            }
        }

        Map sortedMap = new TreeMap(recettes); //Permet de trier par le jour dans la date
        Set set2 = sortedMap.entrySet();
        Iterator iterator2 = set2.iterator();
        while(iterator2.hasNext()) {
            Map.Entry me2 = (Map.Entry)iterator2.next();
            panel.addComponent(new Label("   - " +me2.getKey() + " : " + me2.getValue() + " €"));
        }
        return panel;

    }
    /**
     * Permet d'afficher la recette hebdomadaire
     * @return
     * @throws ParseException
     */
    private Panel panelRecetteHebdomadaire() throws ParseException {
        Panel panel = new Panel();
        mainMenu().addTo(panel);
        Map<Integer, Double> recettes = new HashMap<>(); //Initialisation du hashmap pour stocker la date et le montant
        List<Order> commandes = getDbQueries().getAllCommandes(); //On récupère toutes les commandes
        Calendar gc = Calendar.getInstance(); //On utilise l'object calendar pour les dates
        String format = "dd-MM-yyyy"; //On définit le format de la date

        for(Order c : commandes) {
            if (c.getDateFin() != null && !c.getDateFin().equals("")) { //On vérifie que la commande est bien terminée.
                String date = c.getDateDebut().split(" ")[0]; //Alors on split sur la date pour récupérer que la date et pas l'heure
                DateFormat df = new SimpleDateFormat(format); //On initialise la date avec le format
                Date dateCommande = df.parse(date); //On parse la date par rapport au format
                gc.setTime(dateCommande); // On met la date dans le calendrier
                int numSemaine = gc.get(Calendar.WEEK_OF_YEAR); //Permet de récupérer le numero de la semaine

                if(recettes.containsKey(numSemaine)){ //Si la date est contenue dans le hashmap
                    double montant = c.getMontant() + recettes.get(numSemaine);
                    recettes.put(numSemaine, montant);
                }
                else{
                    recettes.put(numSemaine, c.getMontant());
                }
            }
        }

        Map sortedMap = new TreeMap(recettes); //Permet de trier par le numero de la semaine
        Set set2 = sortedMap.entrySet();
        Iterator iterator2 = set2.iterator();
        while(iterator2.hasNext()) {
            Map.Entry me2 = (Map.Entry)iterator2.next();
            panel.addComponent(new Label("   - Semaine " +me2.getKey() + " : " + me2.getValue() + " €"));
        }
        return panel;

    }

    /**
     * Permet d'afficher la recette mensuelle
     * @return
     * @throws ParseException
     */
    private Panel panelRecetteMensuelle() throws ParseException {
        Panel panel = new Panel();
        mainMenu().addTo(panel);
        panel.addComponent(new Label("Recette mensuelle : "));
        Map<String, Double> recettes = new HashMap<>(); //Initialisation du hashmap pour stocker la date et le montant
        SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");
        List<Order> commandes = getDbQueries().getAllCommandes(); //On récupère toutes les commandes

        for(Order c : commandes) {
            if (c.getDateFin() != null && !c.getDateFin().equals("")) { //Si la commande est bien terminée
                String date = c.getDateDebut().split(" ")[0]; //Alors on split sur la date pour récupérer que la date et pas l'heure
                String mois = date.split("-")[1]; //On split pour récupérer le mois
                String annee = date.split("-")[2]; //On split pour récupérer l'année
                String debut = mois + "/" + annee;
                if(recettes.containsKey(debut)){ //Si le mois et l'année sont contenu dans le hashmap
                    double montant = c.getMontant() + recettes.get(debut);
                    recettes.put(debut, montant);
                }
                else{
                    recettes.put(debut, c.getMontant());
                }
            }
        }

        Map sortedMap = new TreeMap(recettes); //Permet de trier par mois
        Set set2 = sortedMap.entrySet();
        Iterator iterator2 = set2.iterator();
        while(iterator2.hasNext()) {
            Map.Entry me2 = (Map.Entry)iterator2.next();
            panel.addComponent(new Label("   - " +me2.getKey() + " : " + me2.getValue() + " €"));
        }
        return panel;
    }
}