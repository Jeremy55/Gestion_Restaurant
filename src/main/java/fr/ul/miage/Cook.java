package fr.ul.miage;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import org.bson.types.ObjectId;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;



public class Cook extends Staff {

    public class BackLog{

    }

    private String message;

    public Cook(ObjectId id, String login, String mdp, String nom, String prenom) {
        super(id, login, mdp, nom, prenom);
    }

    @Override
    public void Screen() {
        Panel panel = super.deconnection();
        buttonAddRecipe().addTo(panel);
        if(!(message == null)){
            panel.addComponent(new Label(message));
            message = null;
        }
        panel.setLayoutManager(new GridLayout(1));
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
                panel.setLayoutManager(new GridLayout(1));
                panel.addComponent(new EmptySpace(new TerminalSize(0, 0)));
                BasicWindow window = new BasicWindow();
                window.setComponent(panel);
                try {
                    MainTerminal.getConsole().switchWindow(window);
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
                Screen();
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
                Screen(); //Retour au menu.
            }
        });
    }
}

