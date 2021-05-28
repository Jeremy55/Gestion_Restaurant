package fr.ul.miage;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import org.bson.types.ObjectId;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
     * Permet l'affichage du menu du directeur qui comporte de par exemple pouvoir gérer les employés et les statistiques
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
        new Button("Suivre mes employés", new Runnable() { //Affiche un bouton qui redirige vers la fenêtre pour ajouter un employé
            @Override
            public void run() {
                setupWindowAndSwitch(ListeSuiviEmploye(),"",2);
            }
        }).addTo(panel);
        setupWindowAndSwitch(panel,"",1);
        return panel;
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

}
