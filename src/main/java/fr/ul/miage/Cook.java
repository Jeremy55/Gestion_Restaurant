package fr.ul.miage;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import org.bson.types.ObjectId;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;



public class Cook extends Staff {

    public class Plat{
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

        @Override
        public String toString() {
            return "Plat{" +
                    "_id=" + _id +
                    ", nom='" + nom + '\'' +
                    ", Ingredient=" + Ingredient +
                    ", Categorie=" + Categorie +
                    ", prix=" + prix +
                    ", platDuJour=" + platDuJour +
                    '}';
        }
    }

    private String message;

    public Cook( String login, String mdp, String nom, String prenom) {
        super( login, mdp, nom, prenom);
    }

    /**
     * Fenêtre principale du programme avec le menu du cusinier.
     */
    @Override
    public void screen() {
        Panel panel = super.deconnection();
        buttonAddRecipe().addTo(panel);
        buttonBackLogPreparations().addTo(panel);
        buttonSeeIngredientsStock().addTo(panel);
        if(!(message == null)){
            panel.addComponent(new Label(message));
            message = null;
        }
        setupWindowAndSwitch(panel,"Menu");
    }

    /**
     * Permet de mettre en place la fenêtre et de switcher cette fenêtre dans le terminal principal.
     * @param panel
     */
    private void setupWindowAndSwitch(Panel panel,String menuName){
        panel.setLayoutManager(new GridLayout(1));
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
     * @return bouton qui redirige vers la page des préparations.
     */
    private Button buttonBackLogPreparations(){
        return new Button("Voir les plats à preparer", new Runnable() {
            @Override
            public void run(){
                setupWindowAndSwitch(panelBackLogPreparations(),"Plat à préparer");
            }
        });
    }



    private Button buttonSeeIngredientsStock(){
        return new Button("Voir les Stocks d'ingrédients", new Runnable() {
            @Override
            public void run(){
                setupWindowAndSwitch(panelIngredientsStock(),"Ingrédients en stock");
            }
        });
    }

    public  void test(){
        setupWindowAndSwitch(panelIngredientsStock(),"Ingrédients en stock");
    }

    private Panel panelIngredientsStock(){
        Panel panel = new Panel();
        Timer timer = new Timer();

        buttonReturnMainmenu(timer).addTo(panel);

        Panel panelIngredients = new Panel();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                panelIngredients.removeAllComponents();
                ArrayList<Ingredient> ingredients = getDbQueries().getIngredients();
                for (Ingredient i : ingredients){
                    panelIngredients.addComponent(new Label(i.nom + " : " + i.stock));
                }
            }
        }, 0, 2000);

        panel.addComponent(panelIngredients);
        return panel;
    }

    /**
     * Affiche toutes les préparations à faire sous forme de bouton.
     * @return
     */
    private Panel panelBackLogPreparations(){
        Panel panel = new Panel();
        buttonReturnMainmenu().addTo(panel);
        ArrayList<Preparation> sortedPreparations = orderPreparations(getDbQueries().getPreparations());
        for(Preparation p : sortedPreparations){
            buttonStartPreparation(p).addTo(panel);
        }
        return panel;
    }

    public ArrayList<Preparation> orderPreparations(ArrayList<Preparation> preparations){
        // Etape 1 : Tranformation des string qui representent les dates en date réelle.
        for(Preparation p : preparations){
            try {
                p.trueDate = new SimpleDateFormat("dd-M-yyy hh:mm:ss").parse(p.heureCommande);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        // Etape 2 : Tri par rapport au date ( voir la classe preparation )
        Collections.sort(preparations);
        // Etape 4 : On récupère les menus enfants.
        ArrayList<Preparation> preparationsEnfans = new ArrayList<Preparation>();
        for(Preparation p : preparations){
            if(p.menuEnfant){
                preparationsEnfans.add(p);
            }

        }
        // Etape 5 : On retire les menus enfants pour les rajouter en tête de liste.
        preparations.removeAll(preparationsEnfans);
        Collections.sort(preparationsEnfans);
        preparations.addAll(0,preparationsEnfans);

        return preparations;
    }

    /**
     * Bouton lié à une préparation, permet de démarrer la préparation quand actionné.
     * @param preparation
     * @return Bouton lié à une préparation.
     */
    private Button buttonStartPreparation(Preparation preparation){
        Plat plat = getDbQueries().getPlat(preparation.Plat);
        return new Button(plat.nom, new Runnable() {
            @Override
            public void run() {
                setupWindowAndSwitch(panelStartPreparation(plat,preparation),"Préparation d'un plat");
            }
        });
    }

    private Panel panelStartPreparation(Plat plat, Preparation preparation){
        getDbQueries().updatePreparation(preparation);
        Panel panel = new Panel();
        panel.addComponent(new Label(plat.nom));
        panel.addComponent(new Label("Composition :"));
        panel.addComponent(new Label(plat.Ingredient.toString()));
        buttonFinishPreparation(preparation).addTo(panel);
        return panel;
    }

    private Button buttonFinishPreparation(Preparation p){
        return new Button("Préparation terminée", new Runnable() {
            @Override
            public void run() {
                p.fin = new Date().toString();
                getDbQueries().updatePreparation(p);
                setupWindowAndSwitch(panelBackLogPreparations(),"Plat restant");
            }
        });
    }

    private Button buttonAddRecipe(){
        return new Button("Ajouter un plat", new Runnable() {
            @Override
            public void run() {
                ArrayList<ComboBox<String>> ingredientsList = new ArrayList<>(); // Arraylist qui va contenir tous les combobox pour récupérer les ingrédients à la fin.
                Panel panel = new Panel();
                panel.addComponent(new Label("Ajouter un nouveau plat"));
                buttonReturnMainmenu().addTo(panel); // Retour au menu de base.
                panel.addComponent(new Label("Nom du plat :"));
                final TextBox nomPlat = new TextBox().setValidationPattern(Pattern.compile("[A-Za-z ]*")).addTo(panel); // Contient le nom du plat.
                panel.addComponent(new Label("Catégorie du plat :"));
                ComboBox<String>categories = categoriesComboBox().addTo(panel); // Permet de sélectionner la catégorie du plat.
                panel.addComponent(new Label("Prix :"));
                final TextBox prixPlat = new TextBox().setValidationPattern(Pattern.compile("[0-9.]*")).addTo(panel);
                submitDish(nomPlat,ingredientsList,categories,prixPlat).addTo(panel); // Bouton pour envoyer le plat vers la BDD
                panel.addComponent(new Label("Composition du plat :"));
                buttonAddIngredient(panel,ingredientsList).addTo(panel); // Bouton qui permet d'ajouter d'autre comboBox contenant des ingrédients dans la console.
                ingredientsList.add(ingredientComboBox());// ComboBox contenant les ingrédients disponibles dans la BDD.
                ingredientsList.get(0).addTo(panel); // Ajoute la ComboBox dans l'interface.
            }
        });
    }

    private ComboBox<String> ingredientComboBox(){
        ComboBox<String> ingredients = new ComboBox<String>();
        for(Ingredient i :super.getDbQueries().getIngredients()){
            ingredients.addItem(i.nom);
        }
        return ingredients;
    }

    private ComboBox<String> categoriesComboBox(){
        ComboBox<String> categories = new ComboBox<String>();
        for(Categorie c : super.getDbQueries().getCategories()){
            categories.addItem(c.nom);
        }
        return categories;
    }

    private Button buttonAddIngredient(Panel panel,ArrayList<ComboBox<String>> ingredientsList){
        return new Button("Ajouter un ingredient", new Runnable() {
            @Override
            public void run() {
                ComboBox<String> newComboBox = ingredientComboBox(); // Création d'une nouvelle comboBox.
                ingredientsList.add(newComboBox); // Ajout de la comboBox dans l'arraylist qui contient tous les ingredients.
                newComboBox.addTo(panel); // Ajoute la comboBox dans l'interface.
            }
        });
    }

    private Button buttonReturnMainmenu(){
        return new Button("Retour au menu", new Runnable() {
            @Override
            public void run() {
                screen();
            }
        });
    }

    private Button buttonReturnMainmenu(Timer timer){
        return new Button("Retour au menu", new Runnable() {
            @Override
            public void run() {
                timer.cancel();
                screen();
            }
        });
    }

    private Button submitDish(TextBox name, ArrayList<ComboBox<String>> ingredientList,ComboBox categorie,TextBox prix){
        return new Button("Ajouter le nouveau plat", new Runnable() {
            @Override
            public void run() {
                ArrayList<String> listeIngredients = new ArrayList<>();
                for(ComboBox<String> c : ingredientList){
                    listeIngredients.add(c.getText());
                }
                message = "Le plat " + name.getText() + " a bien été ajouté dans la BDD.";
                ArrayList<String> categories = new ArrayList<>();
                categories.add(categorie.getText());
                getDbQueries().newDish(name.getText(),listeIngredients,categories,Double.parseDouble(prix.getText()));
                screen(); //Retour au menu.
            }
        });
    }
}

